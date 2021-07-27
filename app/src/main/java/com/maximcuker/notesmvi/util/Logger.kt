package com.maximcuker.notesmvi.util

import android.util.Log
import com.maximcuker.notesmvi.util.Constants.DEBUG
import com.maximcuker.notesmvi.util.Constants.TAG

var isUnitTest = false

fun printLogD(className: String?, message: String ) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    }
    else if(DEBUG && isUnitTest){
        println("$className: $message")
    }
}