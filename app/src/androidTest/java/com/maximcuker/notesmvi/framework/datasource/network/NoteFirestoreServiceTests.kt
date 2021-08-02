package com.maximcuker.notesmvi.framework.datasource.network

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maximcuker.notesmvi.business.domain.model.NoteFactory
import com.maximcuker.notesmvi.di.TestAppComponent
import com.maximcuker.notesmvi.framework.BaseTest
import com.maximcuker.notesmvi.framework.datasource.data.NoteDataFactory
import com.maximcuker.notesmvi.framework.datasource.network.abstraction.NoteFirestoreService
import com.maximcuker.notesmvi.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.maximcuker.notesmvi.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.NOTES_COLLECTION
import com.maximcuker.notesmvi.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.USER_ID
import com.maximcuker.notesmvi.framework.datasource.network.util.NetworkMapper
import com.maximcuker.notesmvi.framework.presentation.TestBaseApplication
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun queryAllnotes() = runBlocking {
        val notes = noteFirestoreService.getAllNotes()
        printLogD("FirebaseServiceTest", "notes : ${notes.size}")
        assertTrue { notes.size > 10 }
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
