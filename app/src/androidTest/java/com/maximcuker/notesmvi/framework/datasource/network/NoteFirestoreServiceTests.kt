package com.maximcuker.notesmvi.framework.datasource.network

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maximcuker.notesmvi.business.domain.model.Note
import com.maximcuker.notesmvi.business.domain.model.NoteFactory
import com.maximcuker.notesmvi.di.TestAppComponent
import com.maximcuker.notesmvi.framework.BaseTest
import com.maximcuker.notesmvi.framework.datasource.data.NoteDataFactory
import com.maximcuker.notesmvi.framework.datasource.network.abstraction.NoteFirestoreService
import com.maximcuker.notesmvi.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.maximcuker.notesmvi.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.NOTES_COLLECTION
import com.maximcuker.notesmvi.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.USER_ID
import com.maximcuker.notesmvi.framework.datasource.network.util.NetworkMapper
import com.maximcuker.notesmvi.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/*
LEGEND:
1. CBS = "Confirm by searching"

Test cases:
1. insert a single note, CBS
2. update a random note, CBS
3. insert a list of notes, CBS
4. delete a single note, CBS
5. insert a deleted note into "deletes" node, CBS
6. insert a list of deleted notes into "deletes" node, CBS
7. delete a 'deleted note' (note from "deletes" node). CBS
 */
@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteFirestoreServiceTests: BaseTest(){

    // system in test
    private lateinit var noteFirestoreService: NoteFirestoreService

    // dependencies
    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var noteDataFactory: NoteDataFactory

    @Inject
    lateinit var noteFactory: NoteFactory

    @Inject
    lateinit var networkMapper: NetworkMapper

    init {
        injectTest()
        signIn()
        insertTestData()
    }

    @Before
    fun before(){
        noteFirestoreService = NoteFirestoreServiceImpl(
            firebaseAuth = FirebaseAuth.getInstance(),
            firestore = firestore,
            networkMapper = networkMapper
        )
    }

    private fun signIn() = runBlocking{
        firebaseAuth.signInWithEmailAndPassword(
            EMAIL,
            PASSWORD
        ).await()
    }

    private fun insertTestData() {
        val entityList = networkMapper.noteListToEntityList(
            noteDataFactory.produceListOfNotes()
        )
        for (entity in entityList) {
            firestore.collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .document(entity.id)
                .set(entity)
        }
    }
    @Test
    fun insertSingleNote_CBS() = runBlocking{
        val note = noteDataFactory.createSingleNote(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )

        noteFirestoreService.insertOrUpdateNote(note)

        val searchResult = noteFirestoreService.searchNote(note)

        assertEquals(note, searchResult)
    }

    @Test
    fun updateSingleNote_CBS() = runBlocking{

        val searchResults = noteFirestoreService.getAllNotes()

        // choose a random note from list to update
        val randomNote = searchResults.get(Random.nextInt(0,searchResults.size-1) + 1)
        val UPDATED_TITLE = UUID.randomUUID().toString()
        val UPDATED_BODY = UUID.randomUUID().toString()
        var updatedNote = noteFactory.createSingleNote(
            id = randomNote.id,
            title = UPDATED_TITLE,
            body = UPDATED_BODY
        )

        // make the update
        noteFirestoreService.insertOrUpdateNote(updatedNote)

        // query the note after update
        updatedNote = noteFirestoreService.searchNote(updatedNote)!!

        assertEquals(UPDATED_TITLE, updatedNote.title)
        assertEquals(UPDATED_BODY, updatedNote.body)
    }

    @Test
    fun insertNoteList_CBS() = runBlocking {
        val list = noteDataFactory.produceListOfNotes()

        noteFirestoreService.insertOrUpdateNotes(list)

        for (note in list) {
            printLogD("TetsInsetNotes", "note: ${note.id}  ${note.title}  ${note.body}  ${note.created_at}  ${note.updated_at}")
        }
        printLogD("TetsInsetNotes", "note: ")

        val searchResults = noteFirestoreService.getAllNotes()
        for (note in searchResults) {
            printLogD("TetsInsetNotes", "note: ${note.id}  ${note.title}  ${note.body}  ${note.created_at}  ${note.updated_at}")
        }

        assertTrue { searchResults.containsAll(list) }
    }

    @Test
    fun deleteSingleNote_CBS() = runBlocking {
        val noteList = noteFirestoreService.getAllNotes()

        // choose one at random to delete
        val noteToDelete = noteList.get(Random.nextInt(0, noteList.size - 1) + 1)

        noteFirestoreService.deleteNote(noteToDelete.id)

        // confirm it no longer exists in firestore
        val searchResults = noteFirestoreService.getAllNotes()

        assertFalse { searchResults.contains(noteToDelete) }
    }

    @Test
    fun insertIntoDeletesNode_CBS() = runBlocking {
        val noteList = noteFirestoreService.getAllNotes()

        // choose one at random to insert into "deletes" node
        val noteToDelete = noteList.get(Random.nextInt(0, noteList.size - 1) + 1)

        noteFirestoreService.insertDeletedNote(noteToDelete)

        // confirm it is now in the "deletes" node
        val searchResults = noteFirestoreService.getDeletedNotes()

        assertTrue { searchResults.contains(noteToDelete) }
    }

    @Test
    fun insertListIntoDeletesNode_CBS() = runBlocking {
        val noteList = ArrayList(noteFirestoreService.getAllNotes())

        // choose some random notes to add to "deletes" node
        val notesToDelete: ArrayList<Note> = ArrayList()

        // 1st
        var noteToDelete = noteList.get(Random.nextInt(0, noteList.size - 1) + 1)
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // 2nd
        noteToDelete = noteList.get(Random.nextInt(0, noteList.size - 1) + 1)
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // 3rd
        noteToDelete = noteList.get(Random.nextInt(0, noteList.size - 1) + 1)
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // 4th
        noteToDelete = noteList.get(Random.nextInt(0, noteList.size - 1) + 1)
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // insert into "deletes" node
        noteFirestoreService
            .insertDeletedNotes(notesToDelete)

        // confirm the notes are in "deletes" node
        val searchResults = noteFirestoreService.getDeletedNotes()

        assertTrue { searchResults.containsAll(notesToDelete) }
    }

    @Test
    fun deleteDeletedNote_CBS() = runBlocking {
        val note = noteFactory.createSingleNote(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )

        // insert into "deletes" node
        noteFirestoreService.insertDeletedNote(note)

        // confirm note is in "deletes" node
        var searchResults = noteFirestoreService.getDeletedNotes()

        assertTrue { searchResults.contains(note) }

        // delete from "deletes" node
        noteFirestoreService.deleteDeletedNote(note)

        // confirm note is deleted from "deletes" node
        searchResults = noteFirestoreService.getDeletedNotes()

        assertFalse { searchResults.contains(note) }
    }

    companion object{
        const val PASSWORD = "password"
        const val EMAIL = "test@test.test"
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }
}
