package com.maximcuker.notesmvi.di

import com.maximcuker.notesmvi.framework.presentation.MainActivity
import com.maximcuker.notesmvi.framework.presentation.BaseApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ProductionModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: BaseApplication): AppComponent
    }

    fun inject(mainActivity: MainActivity)
}