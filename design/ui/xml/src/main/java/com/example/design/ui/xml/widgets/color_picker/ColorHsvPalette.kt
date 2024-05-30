package com.example.design.ui.xml.widgets.color_picker

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.BitmapDrawable
import kotlin.math.min


class ColorHsvPalette(private val colors: IntArray, resources: Resources?, bitmap: Bitmap?) : BitmapDrawable(resources, bitmap)
{
    private val huePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }
    }

    private val saturationPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun draw(canvas: Canvas) {

        val width = bounds.width()
        val height = bounds.height()
        val centerX = width * 0.5f
        val centerY = height * 0.5f
        val radius = min(width, height) * 0.5f
        val sweepShader: Shader = SweepGradient(
            centerX,
            centerY,
            colors,
            null
        )
        huePaint.shader = sweepShader
        val strokeWidth = radius * 0.7f
        huePaint.strokeWidth = strokeWidth
        val saturationShader: Shader = RadialGradient(
            centerX,
            centerY,
            radius * 0.8f,
            Color.WHITE,
            0x00FFFFFF,
            Shader.TileMode.CLAMP
        )

        saturationPaint.shader = saturationShader
        canvas.drawCircle(centerX, centerY, radius - radius * 0.35f, huePaint)
        canvas.drawCircle(centerX, centerY, radius, saturationPaint)
    }

    override fun setAlpha(alpha: Int) {
        huePaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        huePaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}