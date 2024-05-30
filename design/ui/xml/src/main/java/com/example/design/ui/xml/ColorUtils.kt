package com.example.design.ui.xml

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

fun Int.mixColorWith(fractionOfPrimaryColor: Float, secondColor: Int = Color.WHITE): Int {
    return ColorUtils.blendARGB(secondColor, this, fractionOfPrimaryColor)
}

fun blendARGB(
    @ColorInt color1: Int, @ColorInt color2: Int,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float
): Int {
    val inverseRatio = ratio
    val a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio
    val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
    val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
    val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

fun isLightColor(argb: Int): Boolean {
    return getLuminance(argb) > 0.5
}

fun getLuminance(argb: Int): Double {
    return ColorUtils.calculateLuminance(argb)
}