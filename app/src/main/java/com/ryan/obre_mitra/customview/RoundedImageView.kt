package com.ryan.obre_mitra.customview

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundedImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private val paint = Paint()

    init {
        paint.isAntiAlias = true
        paint.shader = BitmapShader((drawable as BitmapDrawable).bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    override fun onDraw(canvas: Canvas) {
        val radius = Math.min(width / 2f, height / 2f)
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }
}