package com.maximcuker.notesmvi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.maximcuker.notesmvi.business.domain.model.Note
import com.maximcuker.notesmvi.business.domain.model.NoteFactory
import com.maximcuker.notesmvi.business.domain.util.DateUtil
import com.maximcuker.notesmvi.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.maximcuker.notesmvi.framework.datasource.network.util.NetworkMapper
import com.maximcuker.notesmvi.util.cLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}