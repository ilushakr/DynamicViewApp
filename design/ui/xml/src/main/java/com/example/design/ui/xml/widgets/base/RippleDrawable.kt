package com.example.design.ui.xml.widgets.base

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.design.provider.api.AppColor

class RoundedRippleDrawable(
    corners: FloatArray,
    rippleColor: AppColor = Color.argb(40, Color.BLACK.red, Color.BLACK.green, Color.BLACK.blue)
) : RippleDrawable(
    ColorStateList.valueOf(rippleColor),
    null,
    ShapeDrawable(RoundRectShape(corners, null, null))
)