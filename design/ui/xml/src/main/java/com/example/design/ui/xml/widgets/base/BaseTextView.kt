package com.example.design.ui.xml.widgets.base

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import com.example.design.provider.api.AppColor
import com.example.design.provider.api.ResourceProvider
import com.example.design.ui.xml.R
import com.example.design.ui.xml.extensions.dp
import com.example.design.ui.xml.extensions.lifecycleScope
import com.example.design.ui.xml.extensions.obtainStyle
import com.example.design.ui.xml.isLightColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr), KoinComponent {

    protected val resourceProvider: ResourceProvider by inject()

    private val allowLuminanceAutoCheck = true // move to attr

    abstract val backgroundColorFlow: Flow<AppColor?>

    open val cornersFlow: Flow<FloatArray> // move to attr
        get() = resourceProvider.cornerRadiusState.map {
            floatArrayOf(
                it.dp, it.dp,   // Top left radius in px
                it.dp, it.dp,   // Top right radius in px
                it.dp, it.dp,     // Bottom right radius in px
                it.dp, it.dp,      // Bottom left radius in px
            )
        }

    abstract val textColorFlow: Flow<AppColor?>

    private val isFixedCornerRadius: Boolean
    private val isFixedTextSize: Boolean
    private val fixedCornerRadiusArray: FloatArray
    private val fixedTextSize: Int

    init {
        obtainStyle(attrs, R.styleable.BaseTextView, defStyleAttr) {
            isFixedCornerRadius = getBoolean(R.styleable.BaseTextView_isFixedTextCornerRadius, false)
            isFixedTextSize = getBoolean(R.styleable.BaseTextView_isFixedTextSize, false)
            fixedCornerRadiusArray = getDimensionPixelSize(R.styleable.BaseTextView_textViewCornerRadius, 0).run {
                FloatArray(8) { this.toFloat() }
            }
            fixedTextSize = getDimensionPixelSize(R.styleable.BaseTextView_android_textSize, 0)
        }

        if (isFixedCornerRadius) {
            foreground = RoundedRippleDrawable(fixedCornerRadiusArray)
        }
        if (isFixedTextSize) {
            this@BaseTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fixedTextSize.toFloat())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        lifecycleScope?.launch {
            if (!isFixedCornerRadius) {
                launch {
                    cornersFlow.collectLatest {
                        foreground = RoundedRippleDrawable(it)
                    }
                }
            }

            launch {
                combine(backgroundColorFlow, textColorFlow) { backgroundColor, textColor ->
                    when (allowLuminanceAutoCheck) {
                        true -> getTextColorWithRespectOfLuminance(backgroundColor)
                        false -> textColor
                    }
                }.filterNotNull().collectLatest(::setTextColor)
            }

            launch {
                combine(backgroundColorFlow, cornersFlow) { backgroundColor, corners ->
                    RoundRectDrawable(
                        backgroundColor?.let(ColorStateList::valueOf),
                        when (isFixedCornerRadius) {
                            true -> fixedCornerRadiusArray
                            false -> corners
                        }
                    )
                }.collectLatest(::setBackgroundDrawable)
            }

            if (!isFixedTextSize) {
                launch {
                    resourceProvider.textSizeState.filterNotNull().collectLatest { textSize -> this@BaseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                    }
                }
            }

        }
    }

    private fun getTextColorWithRespectOfLuminance(color: AppColor?): AppColor? {
        color ?: return null

        val textColor = when (isLightColor(color)) {
            true -> Color.DKGRAY
            false -> Color.WHITE
        }

        return textColor
    }
}


open class SurfaceTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTextView(context, attrs, defStyleAttr) {

    override val backgroundColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.primarySurfaceColor
        }

    override val textColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.primaryDarkColor
        }

    override val cornersFlow: Flow<FloatArray>
        get() = resourceProvider.cornerRadiusState.map {
            floatArrayOf(
                it.dp, it.dp,   // Top left radius in px
                it.dp, it.dp,   // Top right radius in px
                0f, 0f,     // Bottom right radius in px
                it.dp, it.dp,      // Bottom left radius in px
            )
        }
}

open class BackgroundTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTextView(context, attrs, defStyleAttr) {

    override val backgroundColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.backgroundColor
        }

    override val textColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.backgroundDarkColor
        }
}

open class SecondaryTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTextView(context, attrs, defStyleAttr) {

    override val backgroundColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.secondaryBackgroundColor
        }

    override val textColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.secondaryBackgroundDarkColor
        }

    override val cornersFlow: Flow<FloatArray>
        get() = resourceProvider.cornerRadiusState.map {
            floatArrayOf(
                it.dp, it.dp,   // Top left radius in px
                it.dp, it.dp,   // Top right radius in px
                it.dp, it.dp,   // Bottom right radius in px
                0f, 0f,      // Bottom left radius in px
            )
        }
}


open class SettingsSecondaryTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTextView(context, attrs, defStyleAttr) {

    override val backgroundColorFlow = MutableStateFlow(resourceProvider.currentPaletteState.value?.secondaryBackgroundColor)

    override val textColorFlow = MutableStateFlow(resourceProvider.currentPaletteState.value?.secondaryBackgroundDarkColor)

    override val cornersFlow: Flow<FloatArray>
        get() = resourceProvider.cornerRadiusState.map {
            floatArrayOf(
                it.dp, it.dp,   // Top left radius in px
                it.dp, it.dp,   // Top right radius in px
                it.dp, it.dp,   // Bottom right radius in px
                0f, 0f,      // Bottom left radius in px
            )
        }
}

open class SettingsSurfaceTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTextView(context, attrs, defStyleAttr) {

    override val backgroundColorFlow = MutableStateFlow(resourceProvider.currentPaletteState.value?.primarySurfaceColor)

    override val textColorFlow = MutableStateFlow(resourceProvider.currentPaletteState.value?.primaryDarkColor)

    override val cornersFlow: Flow<FloatArray>
        get() = resourceProvider.cornerRadiusState.map {
            floatArrayOf(
                it.dp, it.dp,   // Top left radius in px
                it.dp, it.dp,   // Top right radius in px
                0f, 0f,     // Bottom right radius in px
                it.dp, it.dp,      // Bottom left radius in px
            )
        }
}