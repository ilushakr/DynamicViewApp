package com.example.design.ui.xml.widgets.base

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.design.provider.api.AppColor
import com.example.design.provider.api.ResourceProvider
import com.example.design.ui.xml.extensions.dp
import com.example.design.ui.xml.extensions.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


abstract class BaseRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), KoinComponent {

    protected val resourceProvider: ResourceProvider by inject()

    open val cornersFlow: MutableStateFlow<FloatArray> // move to attr
        get() = MutableStateFlow(
            floatArrayOf(
                0f.dp, 0f.dp,   // Top left radius in px
                0f.dp, 0f.dp,   // Top right radius in px
                0f.dp, 0f.dp,     // Bottom right radius in px
                0f.dp, 0f.dp,      // Bottom left radius in px
            )
        )

    abstract val backgroundColorState: Flow<AppColor?>

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        lifecycleScope?.launch {
            combine(backgroundColorState, cornersFlow) { backgroundColor, corners ->
                RoundRectDrawable(
                    backgroundColor = backgroundColor?.let(ColorStateList::valueOf),
                    corners = corners,
                )
            }.collectLatest(::setBackground)
        }
    }

    private val mClippingPath = Path()
    private val clippingRectF = RectF()
    private val clippingRect = Rect()

    override fun onDraw(c: Canvas) {
        c.getClipBounds(clippingRect)
        clippingRectF.set(clippingRect)
        mClippingPath.addRoundRect(
            clippingRectF,
            cornersFlow.value,
            Path.Direction.CW
        )
        c.clipPath(mClippingPath)
        super.onDraw(c)
    }

}

open class BackgroundRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr), KoinComponent {

    override val backgroundColorState: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.backgroundColor
        }

}


open class SurfaceRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BackgroundRecyclerView(context, attrs, defStyleAttr), KoinComponent {

    override val backgroundColorState: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.surfaceColor
        }

}

open class SecondaryRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr), KoinComponent {

    override val backgroundColorState: Flow<AppColor?>
        get() = resourceProvider.currentPaletteState.map {
            it?.secondaryBackgroundColor
        }

}


open class SettingsSurfaceRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr), KoinComponent {

    init {
        elevation = 2f.dp
        outlineProvider = ViewOutlineProvider.BOUNDS
        clipToPadding = true
    }

    override val cornersFlow: MutableStateFlow<FloatArray>
        get() = MutableStateFlow(
            floatArrayOf(
                8f.dp, 8f.dp,   // Top left radius in px
                8f.dp, 8f.dp,   // Top right radius in px
                8f.dp, 8f.dp,     // Bottom right radius in px
                8f.dp, 8f.dp,      // Bottom left radius in px
            )
        )

    override val backgroundColorState = MutableStateFlow(resourceProvider.currentPaletteState.value?.surfaceColor)
}