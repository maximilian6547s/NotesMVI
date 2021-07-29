package com.maximcuker.notesmvi.framework.presentation.notedetail.state

import android.os.Parcelable
import com.maximcuker.notesmvi.business.domain.model.Note
import com.maximcuker.notesmvi.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteDetailViewState(

    var note: Note? = null,

    var isUpdatePending: Boolean? = null

) : Parcelable, ViewState
