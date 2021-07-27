package com.maximcuker.notesmvi.business.interactors.notelist

import com.maximcuker.notesmvi.business.data.cache.abstraction.NoteCacheDataSource
import com.maximcuker.notesmvi.business.data.network.abstraction.NoteNetworkDataSource
import com.maximcuker.notesmvi.business.domain.model.Note
import com.maximcuker.notesmvi.business.domain.model.NoteFactory
import com.maximcuker.notesmvi.business.domain.state.*
import com.maximcuker.notesmvi.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class InsertNewNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
    private val noteFactory: NoteFactory
) {

    fun insertNewNote(
        id: String? = null,
        title: String,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>> = flow {
        val newNote = noteFactory.createSingleNote(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            body = ""
        )
        val cacheResult = noteCacheDataSource.insertNote(newNote)

        var cacheResponse: DataState<NoteListViewState>? = null
        if (cacheResult > 0) {
            val viewState = NoteListViewState(
                newNote = newNote
            )
            cacheResponse = DataState.data(
                response = Response(
                    message = INSERT_NOTE_SUCCESS,
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ),
                data = viewState,
                stateEvent = stateEvent
            )
        } else {
            cacheResponse = DataState.data(
                response = Response(
                    message = INSERT_NOTE_FAILED,
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Error()
                ),
                data = null,
                stateEvent = stateEvent
            )
        }
        emit(cacheResponse)

        updateNetwork(cacheResponse.stateMessage?.response?.message, newNote)

    }

    private suspend fun updateNetwork(cacheResponse: String?, newNote:Note) {
        if (cacheResponse.equals(INSERT_NOTE_SUCCESS)) {
            noteNetworkDataSource.insertOrUpdateNote(newNote)
        }
    }

    companion object {
        const val INSERT_NOTE_SUCCESS = "Successfully inserted new note."
        const val INSERT_NOTE_FAILED = "Failed to insert new note."
    }
}