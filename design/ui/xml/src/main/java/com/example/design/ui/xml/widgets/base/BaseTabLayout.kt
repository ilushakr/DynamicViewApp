package com.example.design.ui.xml.widgets.base

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import com.example.design.provider.api.AppColor
import com.example.design.provider.api.PaletteMode
import com.example.design.provider.api.ResourceProvider
import com.example.design.resources.R
import com.example.design.ui.xml.extensions.lifecycleScope
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr), KoinComponent {

    protected val resourceProvider: ResourceProvider by inject()

    open val backgroundColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.backgroundColor
        }

    open val selectedTabIndicatorColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.primarySurfaceColor
        }

    open val selectedTabTextColorFlow: Flow<AppColor?>
        get() = flowOf(Color.BLACK)

    open val unSelectedTabTextColorFlow: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.filterNotNull().map { currentPalette ->
            when (currentPalette.mode) {
                PaletteMode.Light -> Color.BLACK
                PaletteMode.Dark -> currentPalette.primarySurfaceColor
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        lifecycleScope?.launch {
            launch {
                backgroundColorFlow.filterNotNull().collectLatest { backgroundColor ->
                    backgroundTintList = ColorStateList.valueOf(backgroundColor)
                }
            }
            launch {
                selectedTabIndicatorColorFlow.filterNotNull().collectLatest { selectedTabIndicatorColor ->
                    setSelectedTabIndicatorColor(selectedTabIndicatorColor)
                }
            }

            launch {
                combine(unSelectedTabTextColorFlow.filterNotNull(), selectedTabTextColorFlow.filterNotNull()) { selectedTabTextColor, unSelectedTabTextColor ->
                    Pair(selectedTabTextColor, unSelectedTabTextColor)
                }.collectLatest { colorPair ->
                    setTabTextColors(colorPair.first, colorPair.second)
                }
            }
        }
    }

}

open class SettingsBaseTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabLayout(context, attrs, defStyleAttr), KoinComponent {

    override val backgroundColorFlow = MutableStateFlow(resourceProvider.currentPaletteState.value?.backgroundColor)

    override val selectedTabIndicatorColorFlow = MutableStateFlow(resourceProvider.currentPaletteState.value?.primarySurfaceColor)

    override val unSelectedTabTextColorFlow = MutableStateFlow(
        resourceProvider.currentPaletteState.value?.let {
            when (it.mode) {
                PaletteMode.Light -> Color.BLACK
                PaletteMode.Dark -> it.primarySurfaceColor
            }
        }
    )
}