package com.example.design.ui.xml.extensions

import android.graphics.Rect
import android.os.Build
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.design.ui.xml.isLightColor

fun Fragment.statusBarHeight() = with(requireActivity().window.decorView){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rootWindowInsets.getInsets(WindowInsets.Type.statusBars()).top
    } else {
        Rect().apply { getWindowVisibleDisplayFrame(this) }.top
    }
}

fun Fragment.navigationBarHeight() = with(requireActivity().window.decorView){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rootWindowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom
    } else {
        Rect().apply { getWindowVisibleDisplayFrame(this) }.top
    }
}

fun Fragment.setBarAppearance(color: Int){

    val isLightColor = isLightColor(color)

    requireActivity().apply {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLightColor
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = isLightColor
    }
}