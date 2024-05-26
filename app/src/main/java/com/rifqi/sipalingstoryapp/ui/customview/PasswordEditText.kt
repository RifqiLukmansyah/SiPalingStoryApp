package com.rifqi.sipalingstoryapp.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.rifqi.sipalingstoryapp.R

class PasswordEditText(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatEditText(context, attrs, defStyleAttr) {

    private var charLength = 0

    init {
        init()
    }


    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Do Nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                charLength = s.length
                if (charLength == 0) return
                error =
                    if (charLength < 8) context.getString(R.string.validation_password) else null
            }

            override fun afterTextChanged(edt: Editable?) {
                //Do Nothing
            }
        })
    }
}