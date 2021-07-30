package com.maximcuker.notesmvi.framework.datasource.network.implementation

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.maximcuker.notesmvi.business.domain.model.Note
import com.maximcuker.notesmvi.framework.datasource.network.abstraction.NoteFirestoreService
import com.maximcuker.notesmvi.framework.datasource.network.model.NoteNetworkEntity
import com.maximcuker.notesmvi.framework.datasource.network.util.NetworkMapper
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Firestore doc refs:
 * 1. add:  https://firebase.google.com/docs/firestore/manage-data/add-data
 * 2. delete: https://firebase.google.com/docs/firestore/manage-data/delete-data
 * 3. update: https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
 * 4. query: https://firebase.google.com/docs/firestore/query-data/queries
 */
@Singleton
class NoteFirestoreServiceImpl
@Inject
constructor(
    private val firebaseAuth: FirebaseAuth, // might include auth in the future
    private val firestore: FirebaseFirestore,
    private val networkMapper: NetworkMapper
): NoteFirestoreService {

    override suspend fun insertOrUpdateNote(note: Note) {
        val entity = networkMapper.mapToEntity(note)
        entity.updated_at = Timestamp.now() // for updates
        firestore
            .collection(USERS_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .await() // await convert request Task into suspend function
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {

        if(notes.size > 500){
            throw Exception("Cannot insert more than 500 notes at a time into firestore.")
        }

        val collectionRef = firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)

        firestore.runBatch { batch ->
            for(note in notes){
                val entity = networkMapper.mapToEntity(note)
                entity.updated_at = Timestamp.now()
                val documentRef = collectionRef.document(note.id)
                batch.set(documentRef, entity)
            }
        }.await() // await convert request Task into suspend function

    }

    override suspend fun deleteNote(primaryKey: String) {
        firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(primaryKey)
            .delete()
            .await() // await convert request Task into suspend function
    }

    override suspend fun insertDeletedNote(note: Note) {
        val entity = networkMapper.mapToEntity(note)
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .await() // await convert request Task into suspend function
    }

    override suspend fun insertDeletedNotes(notes: List<Note>) {
        if(notes.size > 500){
            throw Exception("Cannot delete more than 500 notes at a time in firestore.")
        }

        val collectionRef = firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)

        firestore.runBatch { batch ->
            for(note in notes){
                val documentRef = collectionRef.document(note.id)
                batch.set(documentRef, networkMapper.mapToEntity(note))
            }
        }.await() // await convert request Task into suspend function
    }

    override suspend fun deleteDeletedNote(note: Note) {
        val entity = networkMapper.mapToEntity(note)
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .delete()
            .await() // await convert request Task into suspend function
    }

    // used in testing
    override suspend fun deleteAllNotes() {
        firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .delete()
            .await() // await convert request Task into suspend function
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .delete()
            .await() // await convert request Task into suspend function
    }

    override suspend fun getDeletedNotes(): List<Note> {
        return networkMapper.entityListToNoteList(
            firestore
                .collection(DELETES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .await().toObjects(NoteNetworkEntity::class.java) // await convert request Task into suspend function
        )
    }

    override suspend fun searchNote(note: Note): Note? {
        return firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .get()
            .await()
            .toObject(NoteNetworkEntity::class.java)?.let {
                networkMapper.mapFromEntity(it)
            }
    }

    override suspend fun getAllNotes(): List<Note> {
        return networkMapper.entityListToNoteList(
            firestore
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .await()
                .toObjects(NoteNetworkEntity::class.java)
        )
    }

    companion object {
        const val NOTES_COLLECTION = "notes"
        const val USERS_COLLECTION = "users"
        const val DELETES_COLLECTION = "deletes"
        const val USER_ID = "d76PqCDeytf7Bwyzjym38lvytnr2" // hardcoded for single user
        const val EMAIL = "maxsah@mail.com"
    }


}