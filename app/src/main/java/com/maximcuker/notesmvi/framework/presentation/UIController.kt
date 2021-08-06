package com.maximcuker.notesmvi.framework.presentation

import com.maximcuker.notesmvi.business.domain.state.DialogInputCaptureCallback
import com.maximcuker.notesmvi.business.domain.state.Response
import com.maximcuker.notesmvi.business.domain.state.StateMessageCallback

interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}