package com.maximcuker.notesmvi.business.interactors.splash

import com.maximcuker.notesmvi.business.data.cache.CacheResponseHandler
import com.maximcuker.notesmvi.business.data.cache.abstraction.NoteCacheDataSource
import com.maximcuker.notesmvi.business.data.network.ApiResponseHandler
import com.maximcuker.notesmvi.business.data.network.abstraction.NoteNetworkDataSource
import com.maximcuker.notesmvi.business.data.util.safeApiCall
import com.maximcuker.notesmvi.business.data.util.safeCacheCall
import com.maximcuker.notesmvi.business.domain.model.Note
import com.maximcuker.notesmvi.business.domain.state.DataState
import com.maximcuker.notesmvi.business.domain.util.DateUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

/*
    Query all notes in the cache. It will then search firestore for
    each corresponding note but with an extra filter: It will only return notes where
    cached_note.updated_at < network_note.updated_at. It will update the cached notes
    where that condition is met. If the note does not exist in Firestore (maybe due to
    network being down at time of insertion), insert it
    (**This must be done AFTER
    checking for deleted notes and performing that sync**).
 */
class SyncNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
) {

    suspend fun syncNotes() {

        val cachedNotesList = getCachedNotes()
        val networkNotesList = getNetworkNotes()

        syncNetworkNotesWithCachedNotes(
            ArrayList(cachedNotesList),
            networkNotesList
        )
    }

    private suspend fun getCachedNotes(): List<Note> {
        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.getAllNotes()
        }

        val response = object : CacheResponseHandler<List<Note>, List<Note>>(
            response = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<Note>): DataState<List<Note>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()

        return response?.data ?: ArrayList()
    }

    private suspend fun getNetworkNotes(): List<Note> {
        val networkResult = safeApiCall(IO) {
            //for production app its not a good idea
            noteNetworkDataSource.getAllNotes()
        }

        val response = object : ApiResponseHandler<List<Note>, List<Note>>(
            response = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<Note>): DataState<List<Note>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()

        return response?.data ?: ArrayList()
    }

    // get all notes from network
    // if they do not exist in cache, insert them
    // if they do exist in cache, make sure they are up to date
    // while looping, remove notes from the cachedNotes list. If any remain, it means they
    // should be in the network but aren't. So insert them.
    private suspend fun syncNetworkNotesWithCachedNotes(
        cachedNotes: ArrayList<Note>,
        networkNotes: List<Note>
    ) = withContext(IO) {

        for (note in networkNotes) {
            noteCacheDataSource.searchNoteById(note.id)?.let { cachedNote ->
                cachedNotes.remove(cachedNote)
                checkIfCachedNoteRequiresUpdate(cachedNote, note)
            } ?: safeApiCall(IO) { noteCacheDataSource.insertNote(note) }
        }

        // insert remaining into network
        for (cachedNote in cachedNotes) {
            safeApiCall(IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }
    }

    private suspend fun checkIfCachedNoteRequiresUpdate(cachedNote: Note, networkNote: Note) {
        val cacheUpdatedAt = cachedNote.updated_at
        val networkUpdatedAt = networkNote.updated_at

        if (networkUpdatedAt > cacheUpdatedAt) {
            safeCacheCall(IO) {
                noteCacheDataSource.updateNote(
                    primaryKey = networkNote.id,
                    newTitle = networkNote.title,
                    newBody = networkNote.body,
                    timestamp = networkNote.updated_at
                )
            }
        } else if(networkUpdatedAt < cacheUpdatedAt) {
            safeApiCall(IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }
    }

}