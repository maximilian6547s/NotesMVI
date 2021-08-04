package com.maximcuker.notesmvi.framework.presentation.notelist

import android.content.SharedPreferences
import com.maximcuker.notesmvi.business.domain.model.NoteFactory
import com.maximcuker.notesmvi.business.domain.state.StateEvent
import com.maximcuker.notesmvi.business.interactors.notelist.NoteListInteractors
import com.maximcuker.notesmvi.framework.presentation.common.BaseViewModel
import com.maximcuker.notesmvi.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class NoteListViewModel
constructor(
    private val noteListInteractors: NoteListInteractors,
    private val noteFactory: NoteFactory,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
): BaseViewModel<NoteListViewState>(){

    override fun handleNewData(data: NoteListViewState) {

    }

    override fun setStateEvent(stateEvent: StateEvent) {
    }

    override fun initNewViewState(): NoteListViewState {
        return NoteListViewState()
    }

}