package com.ryan.obre_mitra.customview

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.ryan.obre_mitra.R

class CustomPhone : AppCompatEditText, View.OnTouchListener {
    var isPhoneValid: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setHint(R.string.phone)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_PHONE)
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkPhone()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun checkPhone() {
        val email = text?.trim()
        if (email.isNullOrEmpty()) {
            isPhoneValid = false
            error = "Masukkan No HP"
        } else if (!Patterns.PHONE.matcher(email).matches()) {
            isPhoneValid = false
            error = "Masukkan No HP dengan benar"
        } else {
            isPhoneValid = true
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) checkPhone()
    }

}