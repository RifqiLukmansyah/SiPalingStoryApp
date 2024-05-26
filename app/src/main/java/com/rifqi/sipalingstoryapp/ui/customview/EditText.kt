package com.rifqi.sipalingstoryapp.ui.customview

import android.widget.EditText

fun EditText.showError(message: String) {
    error = message
    requestFocus()
}