package com.maximcuker.notesmvi.framework.presentation.notedetail

import com.maximcuker.notesmvi.business.domain.state.StateEvent
import com.maximcuker.notesmvi.business.interactors.notedetail.NoteDetailInteractors
import com.maximcuker.notesmvi.framework.presentation.common.BaseViewModel
import com.maximcuker.notesmvi.framework.presentation.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class NoteDetailViewModel
@Inject
constructor(
    private val noteDetailInteractors: NoteDetailInteractors
): BaseViewModel<NoteDetailViewState>(){

    override fun handleNewData(data: NoteDetailViewState) {

    }

    override fun setStateEvent(stateEvent: StateEvent) {

    }

    override fun initNewViewState(): NoteDetailViewState {
        return NoteDetailViewState()
    }

}
