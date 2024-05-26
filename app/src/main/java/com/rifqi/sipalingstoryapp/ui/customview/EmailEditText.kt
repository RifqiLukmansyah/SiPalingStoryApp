package com.rifqi.sipalingstoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas

import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet

import android.view.View

import androidx.appcompat.widget.AppCompatEditText

import com.rifqi.sipalingstoryapp.R

class EmailEditText(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (s.isNotEmpty()) {
                    if (!s.toString().isEmailCorrect()) {
                        context.getString(R.string.validation_email)
                    } else null
                } else null
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }
    fun String.isEmailCorrect(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.hint_email)
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}