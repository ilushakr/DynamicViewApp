package com.example.design.ui.xml.extensions

import android.content.res.Resources
import android.util.TypedValue

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Float?.dp: Float
    get() = (this ?: 0f) * Resources.getSystem().displayMetrics.density

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this, Resources.getSystem().displayMetrics
    )