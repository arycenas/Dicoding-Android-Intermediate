package com.intermediate.storyapp.view.edit

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EmailInputEditText : AppCompatEditText {
    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int):
            super(context, attributeSet, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                if (s.toString().matches(pattern.toRegex())) {
                    println("Email valid")
                } else if (s.toString().length < 1) {
                    error = null
                } else if (s.toString().isEmpty()) {
                    setError("Email empty")
                } else {
                    setError("Email invalid")
                }
            }
        })
    }
}