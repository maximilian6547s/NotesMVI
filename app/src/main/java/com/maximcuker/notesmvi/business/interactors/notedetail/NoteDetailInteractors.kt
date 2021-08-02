package com.maximcuker.notesmvi.business.interactors.notedetail

import com.maximcuker.notesmvi.business.interactors.common.DeleteNote
import com.maximcuker.notesmvi.framework.presentation.notedetail.state.NoteDetailViewState

// Use cases
class NoteDetailInteractors (
    val deleteNote: DeleteNote<NoteDetailViewState>,
    val updateNote: UpdateNote
)