package com.example.design.ui.xml.widgets.base

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.example.design.provider.api.AppColor
import com.example.design.provider.api.ResourceProvider
import com.example.design.ui.xml.extensions.lifecycleScope
import com.example.design.ui.xml.mixColorWith
import com.google.android.material.slider.Slider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.sliderStyle
) : Slider(context, attrs, defStyleAttr), KoinComponent {

    protected val resourceProvider: ResourceProvider by inject()

    abstract val primaryColorState: Flow<AppColor?>

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        lifecycleScope?.launch {
            primaryColorState.filterNotNull().collectLatest {
                this@BaseSlider.trackActiveTintList = ColorStateList.valueOf(it)
                this@BaseSlider.trackInactiveTintList = ColorStateList.valueOf(it.mixColorWith(0.35f))
                this@BaseSlider.thumbTintList = ColorStateList.valueOf(it)
            }
        }
    }

}

open class PrimarySlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.sliderStyle
) : BaseSlider(context, attrs, defStyleAttr), KoinComponent {

    override val primaryColorState: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.primaryColor
        }

}

open class SecondarySlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.sliderStyle
) : BaseSlider(context, attrs, defStyleAttr), KoinComponent {

    override val primaryColorState: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.secondaryColor
        }

}