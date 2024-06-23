package com.ryan.obre_mitra.customview

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ryan.obre_mitra.R

class CustomPassword : AppCompatEditText, View.OnTouchListener {
    var isPassValid: Boolean = false
    private var showPasswordDrawable: Drawable? = null
    private var hidePasswordDrawable: Drawable? = null
    private var isPasswordVisible = false

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
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length < 8) {
                    error = "Masukkan password minimal 8 karakter"
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        showPasswordDrawable = ContextCompat.getDrawable(context, R.drawable.ic_show_password)
        hidePasswordDrawable = ContextCompat.getDrawable(context, R.drawable.ic_hide_password)

        if (showPasswordDrawable == null || hidePasswordDrawable == null) {
            Log.e("CustomPassword", "Drawable not found")
        }

        setCompoundDrawablesWithIntrinsicBounds(null, null, showPasswordDrawable, null)

        setOnTouchListener(this)
    }

    private fun checkPass() {
        val pass = text?.trim()
        when {
            pass.isNullOrEmpty() -> {
                isPassValid = false
                error = "Mohon Masukkan Password"
            }
            else -> {
                isPassValid = true
            }
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) checkPass()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP && event.x >= (right - compoundPaddingRight)) {
            togglePasswordVisibility()
            return true
        }
        return false
    }

    private fun togglePasswordVisibility() {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd

        isPasswordVisible = !isPasswordVisible
        transformationMethod = if (isPasswordVisible) {
            null
        } else {
            PasswordTransformationMethod.getInstance()
        }

        val drawable = if (isPasswordVisible) hidePasswordDrawable else showPasswordDrawable
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

        // Debugging logging
        Log.d("CustomPassword", "Password visibility toggled: $isPasswordVisible")

        // Pastikan bounds drawable disetel ulang
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        // Setel ulang posisi kursor dan pastikan tampilan di-refresh
        setSelection(selectionStart, selectionEnd)
        invalidate()
        requestLayout()
    }
}