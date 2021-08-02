package com.maximcuker.notesmvi.business

import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.firebase.firestore.FirebaseFirestore
import com.maximcuker.notesmvi.di.TestAppComponent
import com.maximcuker.notesmvi.framework.presentation.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class TempTest {

    @ExperimentalCoroutinesApi
    @FlowPreview
    val application: TestBaseApplication = ApplicationProvider.getApplicationContext() as TestBaseApplication

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    init {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

    @Test
    fun someRandomTest() {
        assert(::firebaseFirestore.isInitialized)
    }
}