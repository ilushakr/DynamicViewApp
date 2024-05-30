/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.design.ui.xml.widgets.base

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable

class RoundRectDrawable(
    backgroundColor: ColorStateList?,
    private val corners: FloatArray
) : Drawable() {

    private val mPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    }
    private val mBoundsF by lazy {
        RectF()
    }
    private val mBoundsI by lazy {
        Rect()
    }

    private var mBackground: ColorStateList? = null
    private var mTintFilter: PorterDuffColorFilter? = null
    private var mTint: ColorStateList? = null
    private var mTintMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN

    init {
        setBackground(backgroundColor)
    }

    private fun setBackground(color: ColorStateList?) {
        (color ?: ColorStateList.valueOf(Color.TRANSPARENT)).let {
            mBackground = it
            mPaint.color = it.getColorForState(state, it.defaultColor)
        }
    }

    override fun draw(canvas: Canvas) {
        val paint = mPaint
        val clearColorFilter: Boolean
        if (mTintFilter != null && paint.colorFilter == null) {
            paint.colorFilter = mTintFilter
            clearColorFilter = true
        } else {
            clearColorFilter = false
        }

        canvas.drawPath(
            Path().apply {
                addRoundRect(mBoundsF, corners, Path.Direction.CW)
            },
            paint
        )

        if (clearColorFilter) {
            paint.colorFilter = null
        }
    }

    private fun updateBounds(bounds: Rect?) {
        with(bounds ?: getBounds()) {
            mBoundsF[this.left.toFloat(), this.top.toFloat(), this.right.toFloat()] = this.bottom.toFloat()
            mBoundsI.set(this)
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updateBounds(bounds)
    }

//    override fun getOutline(outline: Outline) {
//        outline.setRoundRect(mBoundsI, corners.max())
//    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    var color: ColorStateList?
        get() = mBackground
        set(color) {
            setBackground(color)
            invalidateSelf()
        }

    override fun setTintList(tint: ColorStateList?) {
        mTint = tint
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        mTintMode = tintMode
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun onStateChange(stateSet: IntArray): Boolean {
        val newColor = mBackground!!.getColorForState(stateSet, mBackground!!.defaultColor)
        val colorChanged = newColor != mPaint.color
        if (colorChanged) {
            mPaint.color = newColor
        }
        if (mTint != null && mTintMode != null) {
            mTintFilter = createTintFilter(mTint, mTintMode)
            return true
        }
        return colorChanged
    }

    override fun isStateful(): Boolean {
        return mTint != null && mTint!!.isStateful || mBackground != null && mBackground!!.isStateful || super.isStateful()
    }

    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    private fun createTintFilter(tint: ColorStateList?, tintMode: PorterDuff.Mode?): PorterDuffColorFilter? {
        if (tint == null || tintMode == null) {
            return null
        }
        val color = tint.getColorForState(state, Color.TRANSPARENT)
        return PorterDuffColorFilter(color, tintMode)
    }
}