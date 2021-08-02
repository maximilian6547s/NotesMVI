package com.maximcuker.notesmvi.framework.presentation

import com.maximcuker.notesmvi.di.DaggerTestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TestBaseApplication: BaseApplication() {

    override fun initAppComponent() {
        appComponent = DaggerTestAppComponent.factory().create(this)
    }
}