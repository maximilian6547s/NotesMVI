package com.maximcuker.notesmvi.business.interactors.notelist

import com.maximcuker.notesmvi.business.interactors.common.DeleteNote
import com.maximcuker.notesmvi.framework.presentation.notelist.state.NoteListViewState

// Use cases
class NoteListInteractors (
    val insertNewNote: InsertNewNote,
    val deleteNote: DeleteNote<NoteListViewState>,
    val searchNotes: SearchNotes,
    val getNumNotes: GetNumNotes,
    val restoreDeletedNote: RestoreDeletedNote,
    val deleteMultipleNotes: DeleteMultipleNotes
)