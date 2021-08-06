package com.maximcuker.notesmvi.di

import com.maximcuker.notesmvi.framework.presentation.MainActivity
import com.maximcuker.notesmvi.framework.presentation.BaseApplication
import com.maximcuker.notesmvi.framework.presentation.notedetail.NoteDetailFragment
import com.maximcuker.notesmvi.framework.presentation.notelist.NoteListFragment
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        AppModule::class,
        ProductionModule::class,
        NoteFragmentFactoryModule::class,
        NoteViewModelModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: BaseApplication): AppComponent
    }

    fun inject(mainActivity: MainActivity)

    fun inject(noteListFragment: NoteListFragment)

    fun inject(noteDetailFragment: NoteDetailFragment)
}