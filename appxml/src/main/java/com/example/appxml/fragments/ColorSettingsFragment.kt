package com.example.appxml.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appxml.R
import com.example.appxml.base.BaseBindingFragment
import com.example.appxml.databinding.FragmentColorSettingsBinding
import com.example.design.provider.api.AppColor
import com.example.design.provider.api.ColorSchemeDirection
import com.example.design.provider.api.ColorSchemeTemplate
import com.example.design.provider.api.ColorSchemeType
import com.example.design.provider.api.CurrentAppTheme
import com.example.design.provider.api.CurrentPalette
import com.example.design.provider.api.PaletteMode
import com.example.design.ui.xml.isLightColor
import com.example.design.ui.xml.mixColorWith
import com.example.design.ui.xml.widgets.color_picker.ColorPickerView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class ColorSettingsFragment : BaseBindingFragment<FragmentColorSettingsBinding>(FragmentColorSettingsBinding::inflate) {

    private val settingsPaletteState = MutableStateFlow(resourceProvider.currentPalette)

    private fun getTextColorWithRespectOfLuminance(color: AppColor): AppColor {
        val textColor = when (isLightColor(color)) {
            true -> Color.DKGRAY
            false -> Color.WHITE
        }

        return textColor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        lifecycleScope.launch {
            settingsPaletteState.filterNotNull().collectLatest(::updatePalette)
        }

        with(binding) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    colorPicker.viewState.collectLatest {

                        val primaryColorHolder = with(it.selectedColors.primaryColor) {
                            ColorHolder(
                                baseColor = this.mixColorWith(0.9f),
                                darkColor = this.mixColorWith(0.80f, Color.BLACK),
                                lightColor = this.mixColorWith(0.4f)
                            )
                        }

                        val secondaryRightColorHolder = with(it.selectedColors.secondaryRightColor) {
                            ColorHolder(
                                baseColor = this.mixColorWith(0.9f),
                                darkColor = this.mixColorWith(0.80f, Color.BLACK),
                                lightColor = this.mixColorWith(0.4f)
                            )
                        }

                        val secondaryLeftColorHolder = with(it.selectedColors.secondaryLeftColor) {
                            ColorHolder(
                                baseColor = this.mixColorWith(0.9f),
                                darkColor = this.mixColorWith(0.80f, Color.BLACK),
                                lightColor = this.mixColorWith(0.08f)
                            )
                        }

                        setUpRvColors(primaryColorHolder, primaryRv)
                        setUpRvColors(secondaryLeftColorHolder, secondaryLeftRv)
                        setUpRvColors(secondaryRightColorHolder, secondaryRightRv)

                        val colors = it.selectedColors

                        settingsPaletteState.value?.let { currentNotNullPalette ->
                            val newAppTheme = CurrentAppTheme(
                                backgroundColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> secondaryLeftColorHolder.lightColor
                                    PaletteMode.Dark -> secondaryLeftColorHolder.lightColor.mixColorWith(0.2f, Color.BLACK)
                                },
                                surfaceColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> secondaryLeftColorHolder.lightColor
                                    PaletteMode.Dark -> secondaryLeftColorHolder.lightColor.mixColorWith(0.3f, Color.BLACK)
                                },
                                primarySurfaceColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> primaryColorHolder.lightColor
                                    PaletteMode.Dark -> primaryColorHolder.lightColor
                                },
                                secondaryBackgroundColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> secondaryRightColorHolder.lightColor
                                    PaletteMode.Dark -> secondaryRightColorHolder.lightColor
                                },
                                systemBarsColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> secondaryLeftColorHolder.darkColor.mixColorWith(0.7f, Color.BLACK)
                                    PaletteMode.Dark -> secondaryLeftColorHolder.lightColor.mixColorWith(0.15f, Color.BLACK)
                                },
                                backgroundDarkColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> secondaryLeftColorHolder.darkColor
                                    PaletteMode.Dark -> secondaryLeftColorHolder.darkColor
                                },
                                secondaryBackgroundDarkColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> secondaryRightColorHolder.darkColor
                                    PaletteMode.Dark -> secondaryRightColorHolder.darkColor
                                },
                                primaryDarkColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> primaryColorHolder.darkColor
                                    PaletteMode.Dark -> primaryColorHolder.darkColor
                                },
                                primaryColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> primaryColorHolder.baseColor
                                    PaletteMode.Dark -> primaryColorHolder.baseColor
                                },
                                secondaryColor = when (currentNotNullPalette.mode) {
                                    PaletteMode.Light -> secondaryRightColorHolder.baseColor
                                    PaletteMode.Dark -> secondaryRightColorHolder.baseColor
                                },
                                primaryColorAngle = it.positionData.angle,
                                template = with(colorPicker.colorSchemeTemplateUiModel) {
                                    ColorSchemeTemplate(
                                        type = ColorSchemeType.valueOf(this.type.name),
                                        typeAngle = this.typeAngle,
                                        direction = ColorSchemeDirection.valueOf(this.direction.name)
                                    )
                                }
                            )

                            settingsPaletteState.value = currentNotNullPalette.copy(
                                lightTheme = if (currentNotNullPalette.mode == PaletteMode.Light) newAppTheme else currentNotNullPalette.lightTheme,
                                darkTheme = if (currentNotNullPalette.mode == PaletteMode.Dark) newAppTheme else currentNotNullPalette.darkTheme,
                            )

                            resourceProvider.currentPalette = currentNotNullPalette.copy(
                                lightTheme = if (currentNotNullPalette.mode == PaletteMode.Light) newAppTheme else currentNotNullPalette.lightTheme,
                                darkTheme = if (currentNotNullPalette.mode == PaletteMode.Dark) newAppTheme else currentNotNullPalette.darkTheme,
                                mode = resourceProvider.currentPalette?.mode ?: currentNotNullPalette.mode
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updatePalette(newPalette: CurrentPalette) = with(binding) {
        loremIpsum1Tv.backgroundColorFlow.value = newPalette.secondaryBackgroundColor
        loremIpsum1Tv.textColorFlow.value = getTextColorWithRespectOfLuminance(newPalette.secondaryBackgroundColor)

        loremIpsum2Tv.backgroundColorFlow.value = newPalette.primarySurfaceColor
        loremIpsum2Tv.textColorFlow.value = getTextColorWithRespectOfLuminance(newPalette.primarySurfaceColor)

        loremIpsumLt.backgroundColorState.value = newPalette.surfaceColor
        contentBackgroundLt.backgroundColorState.value = newPalette.backgroundColor
        newPalette.backgroundColor.also { backgroundColor ->
            colorSchemeTypeTl.backgroundColorFlow.value = backgroundColor
            directionTl.backgroundColorFlow.value = backgroundColor
            modeTl.backgroundColorFlow.value = backgroundColor
        }
        newPalette.primarySurfaceColor.also { primarySurfaceColor ->
            colorSchemeTypeTl.selectedTabIndicatorColorFlow.value = primarySurfaceColor
            directionTl.selectedTabIndicatorColorFlow.value = primarySurfaceColor
            modeTl.selectedTabIndicatorColorFlow.value = primarySurfaceColor

            colorSchemeTypeTl.unSelectedTabTextColorFlow.value = when (newPalette.mode) {
                PaletteMode.Light -> Color.BLACK
                PaletteMode.Dark -> primarySurfaceColor
            }
            directionTl.unSelectedTabTextColorFlow.value = when (newPalette.mode) {
                PaletteMode.Light -> Color.BLACK
                PaletteMode.Dark -> primarySurfaceColor
            }
            modeTl.unSelectedTabTextColorFlow.value = when (newPalette.mode) {
                PaletteMode.Light -> Color.BLACK
                PaletteMode.Dark -> primarySurfaceColor
            }
        }

        colorPicker.setBackgroundColor(newPalette.backgroundColor)

        primaryRv.backgroundColorState.value = newPalette.surfaceColor
        secondaryRightRv.backgroundColorState.value = newPalette.surfaceColor
        secondaryLeftRv.backgroundColorState.value = newPalette.surfaceColor

        newPalette.systemBarsColor.let { color ->
            requireView().findViewById<View>(R.id.status_bar_view).setBackgroundColor(color)
            requireView().findViewById<View>(R.id.navigation_bar_view).setBackgroundColor(color)
//                            setBarAppearance(color)
        }
    }

    private fun initTabLayouts() = with(binding) {
        settingsPaletteState.value?.let { currentPalette ->
            setUpTabLayout(
                tabLayout = colorSchemeTypeTl,
                tabs = listOf(
                    TabLayoutData(
                        text = ColorPickerView.ColorSchemeTypeUiModel.Triangle.name,
                        id = R.id.triangle_scheme_tab_id,
                        tag = ColorPickerView.ColorSchemeTypeUiModel.Triangle
                    ),
                    TabLayoutData(
                        text = ColorPickerView.ColorSchemeTypeUiModel.Analog.name,
                        id = R.id.analog_scheme_tab_id,
                        tag = ColorPickerView.ColorSchemeTypeUiModel.Analog
                    )
                ),
                preselectedTabTag = ColorPickerView.ColorSchemeTypeUiModel.valueOf(currentPalette.currentTemplate.type.name)
            ) { tab ->
                val newType = when (tab.id) {
                    R.id.triangle_scheme_tab_id -> ColorPickerView.ColorSchemeTypeUiModel.Triangle
                    R.id.analog_scheme_tab_id -> ColorPickerView.ColorSchemeTypeUiModel.Analog
                    else -> return@setUpTabLayout
                }
                if (colorPicker.colorSchemeTemplateUiModel.type != newType) {
                    colorPicker.colorSchemeTemplateUiModel = colorPicker.colorSchemeTemplateUiModel.copy(
                        type = newType,
                        typeAngle = newType.typeDefaultAngle,
                    )
                }
            }

            setUpTabLayout(
                tabLayout = directionTl,
                tabs = listOf(
                    TabLayoutData(
                        text = ColorPickerView.ColorSchemeDirectionUiModel.Forward.name,
                        id = R.id.forward_direction_tab_id,
                        tag = ColorPickerView.ColorSchemeDirectionUiModel.Forward
                    ),
                    TabLayoutData(
                        text = ColorPickerView.ColorSchemeDirectionUiModel.Reversed.name,
                        id = R.id.reversed_direction_tab_id,
                        tag = ColorPickerView.ColorSchemeDirectionUiModel.Reversed
                    )
                ),
                preselectedTabTag = ColorPickerView.ColorSchemeDirectionUiModel.valueOf(currentPalette.currentTemplate.direction.name)
            ) { tab ->
                val newDirection = when (tab.id) {
                    R.id.forward_direction_tab_id -> ColorPickerView.ColorSchemeDirectionUiModel.Forward
                    R.id.reversed_direction_tab_id -> ColorPickerView.ColorSchemeDirectionUiModel.Reversed
                    else -> return@setUpTabLayout
                }
                if (colorPicker.colorSchemeTemplateUiModel.direction != newDirection) {
                    colorPicker.colorSchemeTemplateUiModel = colorPicker.colorSchemeTemplateUiModel.copy(
                        direction = newDirection
                    )
                }
            }

            setUpTabLayout(
                tabLayout = modeTl,
                tabs = listOf(
                    TabLayoutData(
                        text = PaletteMode.Light.name,
                        id = R.id.light_mode_tab_id,
                        tag = PaletteMode.Light
                    ),
                    TabLayoutData(
                        text = PaletteMode.Dark.name,
                        id = R.id.dark_mode_tab_id,
                        tag = PaletteMode.Dark
                    )
                ),
                preselectedTabTag = PaletteMode.valueOf(currentPalette.mode.name)
            ) { tab ->
                tab.view.let(::changeTheme)
            }
        }
    }

    private fun setUpTabLayout(
        tabLayout: TabLayout,
        tabs: List<TabLayoutData>,
        preselectedTabTag: Any,
        onTabSelected: (tab: TabLayout.Tab) -> Unit,
    ) {
        tabLayout.apply {
            tabs.forEach { tabData ->
                addTab(
                    newTab().apply {
                        text = tabData.text
                        id = tabData.id
                        tag = tabData.tag
                    }
                )
            }

            settingsPaletteState.value?.let {
                preselectTab(this, preselectedTabTag)
            }

            addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            onTabSelected.invoke(it)
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {}

                }
            )
        }
    }

    private fun preselectTab(tabLayout: TabLayout, tag: Any?) {
        with(tabLayout) {
            for (i in 0 until tabCount) {
                if (getTabAt(i)?.tag == tag) {
                    selectTab(getTabAt(i))
                }
            }
        }
    }

    private fun initViews() {
        with(binding) {
            statusBarView.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    resourceProvider.barsHeightsHolder.statusBarHeight
                )
            navigationBarView.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    resourceProvider.barsHeightsHolder.navigationBarHeight
                )
        }

        settingsPaletteState.value?.let {
            binding.colorPicker.setUpDefaultPosition(
                rad = it.currentPrimaryAngleRad,
                scheme = ColorPickerView.ColorSchemeTemplateUiModel(
                    type = ColorPickerView.ColorSchemeTypeUiModel.valueOf(it.currentTemplate.type.name),
                    typeAngle = it.currentTemplate.typeAngle,
                    direction = ColorPickerView.ColorSchemeDirectionUiModel.valueOf(it.currentTemplate.direction.name)
                )
            )
        }
        initTabLayouts()
        initRecyclers()
    }

    private fun changeTheme(myView: View) = with(binding) {
        if (themeSwiperIv.isVisible) {
            return
        }

        val w = containerFl.measuredWidth
        val h = containerFl.measuredHeight

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        containerFl.draw(canvas)

        themeSwiperIv.setImageBitmap(bitmap)
        themeSwiperIv.isVisible = true

        val finalRadius = kotlin.math.hypot(w.toFloat(), h.toFloat())

        saveToProvider()

        val arr = IntArray(2).apply(myView::getLocationOnScreen)

        val centreX = arr[0] + myView.width / 2
        val centreY = arr[1] + myView.height / 2

        val anim = ViewAnimationUtils.createCircularReveal(
            containerFl,
            centreX,
            centreY,
            0f,
            finalRadius
        )
        anim.duration = 600L
        anim.doOnEnd {
            themeSwiperIv.setImageDrawable(null)
            themeSwiperIv.isVisible = false
        }
        anim.start()
    }

    private fun saveToProvider() {
        settingsPaletteState.value?.let { plt ->
            val newPalette = plt.copy(
                mode = when (plt.mode) {
                    PaletteMode.Light -> PaletteMode.Dark
                    PaletteMode.Dark -> PaletteMode.Light
                }
            )

            settingsPaletteState.value = newPalette

            val currentTemplate = newPalette.currentTemplate
            binding.colorPicker.setUpDefaultPosition(
                rad = newPalette.currentPrimaryAngleRad,
                scheme = ColorPickerView.ColorSchemeTemplateUiModel(
                    type = ColorPickerView.ColorSchemeTypeUiModel.valueOf(currentTemplate.type.name),
                    typeAngle = currentTemplate.typeAngle,
                    direction = ColorPickerView.ColorSchemeDirectionUiModel.valueOf(currentTemplate.direction.name)
                )
            )

            preselectTab(binding.colorSchemeTypeTl, ColorPickerView.ColorSchemeTypeUiModel.valueOf(currentTemplate.type.name))
            preselectTab(binding.directionTl, ColorPickerView.ColorSchemeDirectionUiModel.valueOf(currentTemplate.direction.name))
        }
    }

    private fun setUpRvColors(primaryColorHolder: ColorHolder, rv: RecyclerView) {
        if (rv.childCount < 3) return
        val colorList = listOf(
            primaryColorHolder.baseColor,
            primaryColorHolder.darkColor,
            primaryColorHolder.lightColor
        )
        colorList.forEachIndexed { index, i ->
            rv.children.toList()[index].setBackgroundColor(i)
        }
    }

    private fun initRecyclers() = with(binding) {
        primaryRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = MyAdapter()
        }

        secondaryRightRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = MyAdapter()
        }

        secondaryLeftRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = MyAdapter()
        }
    }

    class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                View(parent.context).apply {
                    val layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.MATCH_PARENT
                    )
                    this.layoutParams = layoutParams
                }
            ) {}
        }

        override fun getItemCount() = 3

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
    }

    private data class TabLayoutData(
        val text: String,
        @IdRes val id: Int,
        val tag: Any
    )

    data class ColorHolder(
        val baseColor: AppColor,
        val darkColor: AppColor,
        val lightColor: AppColor,
    )

    companion object {
        const val TAG = "ColorSettingsFragment"
    }
}