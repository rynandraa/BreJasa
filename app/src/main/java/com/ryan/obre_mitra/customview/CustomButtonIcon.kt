package com.ryan.obre_mitra.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.ryan.obre_mitra.R

class CustomButtonIcon : AppCompatButton{
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setTextColor(textColor)
        textSize = 16f
        gravity = Gravity.CENTER

        background = if (isEnabled) enableButton else disableButton
    }

    private lateinit var enableButton: Drawable
    private lateinit var disableButton: Drawable
    private var textColor: Int = 0

    private fun init() {
        textColor = ContextCompat.getColor(context, android.R.color.background_light)
        enableButton = ContextCompat.getDrawable(context, R.drawable.background_button) as Drawable
        disableButton =
            ContextCompat.getDrawable(context, R.drawable.background_disable) as Drawable
    }

}