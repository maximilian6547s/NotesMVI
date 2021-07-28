package com.maximcuker.notesmvi.di

import com.maximcuker.notesmvi.business.data.NoteDataFactory
import com.maximcuker.notesmvi.business.data.cache.FakeNoteCacheDataSourceImpl
import com.maximcuker.notesmvi.business.data.cache.abstraction.NoteCacheDataSource
import com.maximcuker.notesmvi.business.data.network.FakeNoteNetworkDataSourceImpl
import com.maximcuker.notesmvi.business.data.network.abstraction.NoteNetworkDataSource
import com.maximcuker.notesmvi.business.domain.model.Note
import com.maximcuker.notesmvi.business.domain.model.NoteFactory
import com.maximcuker.notesmvi.business.domain.util.DateUtil
import com.maximcuker.notesmvi.util.isUnitTest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

//class for manual dependency injection
class DependencyContainer {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
    val dateUtil = DateUtil(dateFormat)
    lateinit var noteNetworkDataSource: NoteNetworkDataSource
    lateinit var noteCacheDataSource: NoteCacheDataSource
    lateinit var noteFactory: NoteFactory
    lateinit var noteDataFactory: NoteDataFactory
    private var notesData: HashMap<String, Note> = HashMap()

    init {
        isUnitTest = true // for Logger.kt
    }

    fun build() {
        this.javaClass.classLoader?.let {classLoader ->
            noteDataFactory = NoteDataFactory(classLoader)

            //fake data set
            notesData = noteDataFactory.produceHashMapOfNotes(noteDataFactory.produceListOfNotes())
        }
        noteFactory = NoteFactory(dateUtil)
        noteNetworkDataSource = FakeNoteNetworkDataSourceImpl(
            notesData = notesData,
            deletedNotesData = HashMap()
        )
        noteCacheDataSource = FakeNoteCacheDataSourceImpl(
            notesData = notesData,
            dateUtil = dateUtil
        )
    }

}