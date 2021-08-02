package com.maximcuker.notesmvi.framework.presentation

import android.app.Application
import com.maximcuker.notesmvi.di.AppComponent
import com.maximcuker.notesmvi.di.DaggerAppComponent
import kotlinx.coroutines.*

@FlowPreview
@ExperimentalCoroutinesApi
open class BaseApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    open fun initAppComponent() {
        appComponent = DaggerAppComponent.factory().create(this)
    }
}