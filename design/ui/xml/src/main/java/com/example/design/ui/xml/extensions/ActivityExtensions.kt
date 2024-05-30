package com.example.design.ui.xml.extensions

import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.design.ui.xml.isLightColor
import com.example.design.ui.xml.mixColorWith

fun AppCompatActivity.setBarAppearance(color: Int){

    val isLightColor = isLightColor(color)

    this.apply {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLightColor
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = isLightColor
    }
}

fun AppCompatActivity.tintSystemBars(
    finishColor: Int,
    startColor: Int = window.statusBarColor,
    duration: Long = 0,
    applyBarAppearance: Boolean = true
) {

    if (applyBarAppearance) {
        setBarAppearance(finishColor)
    }

    if (finishColor == startColor) return

    when {
        duration == 0L -> {
            window.statusBarColor = finishColor
            window.navigationBarColor = finishColor
        }

        else -> {
            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener { animation ->
                    val position = animation.animatedFraction
                    val blended = finishColor.mixColorWith(position, startColor)
                    try {
                        window.statusBarColor = blended
                        window.navigationBarColor = blended
                    } catch (_: Throwable) {

                    }
                }
                setDuration(duration)
                start()
            }
        }
    }
}

fun AppCompatActivity.statusBarHeight() =with(window.decorView){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rootWindowInsets.getInsets(WindowInsets.Type.statusBars()).top
    } else {
        Rect().apply { getWindowVisibleDisplayFrame(this) }.top
    }
}

fun AppCompatActivity.navigationBarHeight() =with(window.decorView){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rootWindowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom
    } else {
        Rect().apply { getWindowVisibleDisplayFrame(this) }.top
    }
}