package com.example.design.ui.xml.extensions

import android.content.res.TypedArray
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

val View.lifecycleScope: CoroutineScope?
    get() = findViewTreeLifecycleOwner()?.lifecycleScope

fun View.setCorners(cornerRadiusDP: Float) {

    val mOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {

            val left = 0
            val top = 0;
            val right = view.width
            val bottom = view.height

            outline.setRoundRect(left, top, right, bottom, cornerRadiusDP)

        }
    }

    outlineProvider = mOutlineProvider
    clipToOutline = true

}

data class SlideChangeEvent(val value: Float, val fromUser: Boolean)

fun Slider.state(initialValue: Float? = null): Flow<SlideChangeEvent> = callbackFlow {

    initialValue?.let(::setValue)

    val listener = Slider.OnChangeListener { _, value, fromUser ->
        launch { send(SlideChangeEvent(value, fromUser)) }
    }
    addOnChangeListener(listener)
    awaitClose { removeOnChangeListener(listener) }

}.onStart { emit(SlideChangeEvent(initialValue ?: value, false)) }

@OptIn(ExperimentalContracts::class)
inline fun View.obtainStyle(
    attrs: AttributeSet?,
    style: IntArray,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    block: TypedArray.() -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val a = context.obtainStyledAttributes(attrs, style, defStyleAttr, defStyleRes)
    block.invoke(a)
    a.recycle()
}