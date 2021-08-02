package com.maximcuker.notesmvi.di

import com.maximcuker.notesmvi.business.TempTest
import com.maximcuker.notesmvi.framework.presentation.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
    TestModule::class
    ]
)
interface TestAppComponent: AppComponent {

    @Component.Factory
    interface Factory{
        @ExperimentalCoroutinesApi
        @FlowPreview
        fun create(@BindsInstance app: TestBaseApplication) : TestAppComponent
    }

    fun inject(tempTest: TempTest)
}