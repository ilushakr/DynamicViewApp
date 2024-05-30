package com.example.design.ui.xml.widgets.color_picker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.example.design.ui.xml.extensions.dp
import com.example.design.ui.xml.mixColorWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val predefinedViewState by lazy {
        ViewState(
            PositionDataUiModel(
                primaryColorPosition = PositionUiModel(
                    angle = 0f.toRad,
                    position = calculatePointerPosition(0f)
                ),
                leftColorPosition = ColorSchemeTypeUiModel.Analog.typeDefaultAngle.toRad.run {
                    PositionUiModel(
                        angle = this.toRad,
                        position = calculatePointerPosition(this)
                    )
                },
                rightColorPosition = (-ColorSchemeTypeUiModel.Analog.typeDefaultAngle).toRad.run {
                    PositionUiModel(
                        angle = this.toRad,
                        position = calculatePointerPosition(this)
                    )
                },
                colorSchemeTemplateUiModel = ColorSchemeTemplateUiModel(
                    type = ColorSchemeTypeUiModel.Analog,
                    typeAngle = ColorSchemeTypeUiModel.Analog.typeDefaultAngle,
                    direction = ColorSchemeDirectionUiModel.Forward
                )
            ),
            selectedColors = SelectedColorsUiModel(
                primaryColor = getColorByRadianWithIntensity(
                    colors = colors,
                    angleRad = 0f.toRad,
                ),
                secondaryRightColor = getColorByRadianWithIntensity(
                    colors = colors,
                    angleRad = ColorSchemeTypeUiModel.Analog.typeDefaultAngle.toRad,
                ),
                secondaryLeftColor = getColorByRadianWithIntensity(
                    colors = colors,
                    angleRad = (-ColorSchemeTypeUiModel.Analog.typeDefaultAngle).toRad,
                )
            )
        )
    }

    private var squareRespectTo: SquareRespectTo = SquareRespectTo.WIDTH

    private val Float.toRad
        get() = (this * Math.PI / 180).toFloat()

    private val viewRadius: Float
        get() = min(width, height) / 2f

    private val pointerRadius: Float
        get() = viewRadius * 3 / 4

    private val mCenterPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private val pointerPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    var colors: IntArray = DEFAULT_PALETTE_COLOR_LIST.clone().apply {
        for (i in this.indices) {
            this[i] = this[i].mixColorWith(0.65f)
        }
    }
        set(value) {
            field = value
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            setImageDrawable(ColorHsvPalette(colors, resources, bitmap))
            invalidate()
            onNewColorSelection()
        }

    private val _viewState = MutableStateFlow(predefinedViewState)
    val viewState: StateFlow<ViewState> = _viewState

    var colorSchemeTemplateUiModel: ColorSchemeTemplateUiModel
        get() = _viewState.value.positionData.colorSchemeTemplateUiModel
        set(value) {
            onNewColorSelection(colorScheme = value)
        }

    fun setUpDefaultPosition(rad: Float, scheme: ColorSchemeTemplateUiModel) {
        onNewColorSelection(
            rad = rad,
            colorScheme = scheme
        )
    }

    init {
        scaleType = ScaleType.FIT_START
        post {
            onNewColorSelection(_viewState.value.positionData.primaryColorPosition.angle)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width * 0.5f
        val centerY = height * 0.5f
        val radius = min(width, height) * 0.5f
        val innerRadius = radius * 0.3f - 10
        canvas.drawCircle(centerX, centerY, innerRadius, mCenterPaint)

        with(_viewState.value.positionData) {
            canvas.drawPointerCircle(primaryColorPosition.position, Color.DKGRAY)
            canvas.drawPointerCircle(leftColorPosition.position, Color.LTGRAY)
            canvas.drawPointerCircle(rightColorPosition.position, Color.LTGRAY)
        }
    }

    private fun Canvas.drawPointerCircle(position: FloatArray, color: Int) {
        drawCircle(
            position[0],
            position[1],
            6f.dp,
            pointerPaint.apply { this.color = color }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x - viewRadius
        val y = event.y - viewRadius

        val rad = atan2(y.toDouble(), x.toDouble()).toFloat()

//        val eventDistance = sqrt(x.pow(2) + y.pow(2)) / radius
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                onNewColorSelection(rad)
            }
        }
        return true
    }

    private fun updateState(
        newPositionData: PositionDataUiModel,
        newSelectedColors: SelectedColorsUiModel
    ) {
        _viewState.value = _viewState.value.copy(
            positionData = newPositionData,
            selectedColors = newSelectedColors
        )

        invalidate()
    }

    private fun onNewColorSelection(
        rad: Float = _viewState.value.positionData.angle,
        colorScheme: ColorSchemeTemplateUiModel = _viewState.value.positionData.colorSchemeTemplateUiModel
    ) {

        val rightNewAngle = colorScheme.typeAngle

        val primaryColorPosition = PositionUiModel(
            angle = rad,
            position = calculatePointerPosition(rad)
        )
        val leftColorPosition = PositionUiModel(
            angle = rad - rightNewAngle.toRad,
            position = calculatePointerPosition(rad = rad - rightNewAngle.toRad)
        )
        val rightColorPosition = PositionUiModel(
            angle = rad + rightNewAngle.toRad,
            position = calculatePointerPosition(rad = rad + rightNewAngle.toRad)
        )

        val primarySelectedColor = getColorByRadianWithIntensity(
            colors = colors,
            angleRad = primaryColorPosition.angle.getRadMod(),
        )
        val secondaryRightSelectedColor = getColorByRadianWithIntensity(
            colors = colors,
            angleRad = when (colorScheme.direction) {
                ColorSchemeDirectionUiModel.Forward -> rightColorPosition
                ColorSchemeDirectionUiModel.Reversed -> leftColorPosition
            }.angle.getRadMod(),
        )
        val secondaryLeftSelectedColor = getColorByRadianWithIntensity(
            colors = colors,
            angleRad = when (colorScheme.direction) {
                ColorSchemeDirectionUiModel.Forward -> leftColorPosition
                ColorSchemeDirectionUiModel.Reversed -> rightColorPosition
            }.angle.getRadMod(),
        )

        val selectedColors = SelectedColorsUiModel(
            primaryColor = primarySelectedColor,
            secondaryRightColor = secondaryRightSelectedColor,
            secondaryLeftColor = secondaryLeftSelectedColor
        )

        mCenterPaint.color = primarySelectedColor

        updateState(
            newPositionData = PositionDataUiModel(
                primaryColorPosition = primaryColorPosition,
                leftColorPosition = leftColorPosition,
                rightColorPosition = rightColorPosition,
                colorSchemeTemplateUiModel = colorScheme
            ),
            newSelectedColors = selectedColors
        )
    }

    private fun calculatePointerPosition(rad: Float): FloatArray {
        val xCenterOfView = width * 0.5f
        val yCenterOfView = height * 0.5f
        val x = xCenterOfView + pointerRadius * cos(rad)
        val y = yCenterOfView + pointerRadius * sin(rad)
        return floatArrayOf(x, y)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val dimen = when (squareRespectTo) {
            SquareRespectTo.WIDTH -> measuredWidth
            SquareRespectTo.HEIGHT -> measuredHeight
        }
        setMeasuredDimension(dimen, dimen)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        setImageDrawable(ColorHsvPalette(colors, resources, bitmap))
    }

    private fun Float.getRadMod(): Float {
        var radMod = this / (2 * Math.PI)
        if (radMod < 0) {
            radMod += 1f
        }
        return radMod.toFloat()
    }

    private fun ave(s: Int, d: Int, p: Float): Int {
        return s + (p * (d - s)).roundToInt()
    }

    private fun getColorByRadianWithIntensity(
        colors: IntArray,
        angleRad: Float,
        fractionOfPrimaryColor: Float = 1f
    ): Int {
        if (angleRad <= 0) {
            return colors[0]
        }
        if (angleRad >= 1) {
            return colors[colors.size - 1]
        }
        var p = angleRad * (colors.size - 1)
        val i = p.toInt()
        p -= i.toFloat()

        // now p is just the fractional part [0...1) and i is the index
        val c0 = colors[i]
        val c1 = colors[i + 1]
        val a = ave(Color.alpha(c0), Color.alpha(c1), p)
        val r = ave(Color.red(c0), Color.red(c1), p)
        val g = ave(Color.green(c0), Color.green(c1), p)
        val b = ave(Color.blue(c0), Color.blue(c1), p)
        val mixedColor = Color.argb(a, r, g, b).mixColorWith(fractionOfPrimaryColor)
        return mixedColor
    }

    data class ViewState(
        val positionData: PositionDataUiModel,
        val selectedColors: SelectedColorsUiModel
    )

    data class PositionDataUiModel(
        val primaryColorPosition: PositionUiModel,
        val leftColorPosition: PositionUiModel,
        val rightColorPosition: PositionUiModel,
        val colorSchemeTemplateUiModel: ColorSchemeTemplateUiModel
    ) {
        val angle: Float
            get() = primaryColorPosition.angle
    }

    data class PositionUiModel(val angle: Float, val position: FloatArray) : Serializable {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PositionUiModel

            if (angle != other.angle) return false
            if (!position.contentEquals(other.position)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = angle.hashCode()
            result = 31 * result + position.contentHashCode()
            return result
        }
    }

    data class ColorSchemeTemplateUiModel(
        val type: ColorSchemeTypeUiModel,
        val typeAngle: Float,
        val direction: ColorSchemeDirectionUiModel
    )

    enum class ColorSchemeTypeUiModel(
        val typeDefaultAngle: Float,
        val typeAngleRange: ClosedRange<Float> = 10f..60f
    ) {
        Triangle(typeDefaultAngle = 120f), Analog(typeDefaultAngle = 60f)
    }

    enum class ColorSchemeDirectionUiModel {
        Forward, Reversed
    }

    data class SelectedColorsUiModel(
        val primaryColor: Int,
        val secondaryRightColor: Int,
        val secondaryLeftColor: Int
    )

    enum class SquareRespectTo {
        WIDTH, HEIGHT
    }

    /*
    private fun rotateColor(color: Int, rad: Float): Int {
        val deg = rad * 180 / 3.1415927f
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val cm = ColorMatrix()
        val tmp = ColorMatrix()
        cm.setRGB2YUV()
        tmp.setRotate(0, deg)
        cm.postConcat(tmp)
        tmp.setYUV2RGB()
        cm.postConcat(tmp)
        val a = cm.array
        val ir = floatToByte(a[0] * r + a[1] * g + a[2] * b)
        val ig = floatToByte(a[5] * r + a[6] * g + a[7] * b)
        val ib = floatToByte(a[10] * r + a[11] * g + a[12] * b)
        return Color.argb(
            Color.alpha(color), pinToByte(ir),
            pinToByte(ig), pinToByte(ib)
        )
    }

    private fun floatToByte(x: Float): Int {
        return x.roundToInt()
    }

    private fun pinToByte(n: Int): Int {
        var n = n
        if (n < 0) {
            n = 0
        } else if (n > 255) {
            n = 255
        }
        return n
    }

    private fun Canvas.drawPointerSquare(position: FloatArray, color: Int) {
        val positionX = position[0]
        val positionY = position[1]
        drawRect(
            positionX - 6f.dp,
            positionY + 6.dp,
            positionX + 6.dp,
            positionY - 6.dp,
            pointerPaint.apply { this.color = color })
    }

    private fun Canvas.drawPointerTriangle(position: FloatArray, color: Int) {
        val x = position[0].toInt()
        val y = position[1].toInt()

        val p1 = Point(x, y)
        val pointX = x + 12.dp / 2
        val pointY = y - 12.dp
        val p2 = Point(pointX, pointY)
        val p3 = Point(x + 12.dp, y)
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(p1.x.toFloat(), p1.y.toFloat())
        path.lineTo(p2.x.toFloat(), p2.y.toFloat())
        path.lineTo(p3.x.toFloat(), p3.y.toFloat())
        path.close()
        drawPath(path, pointerPaint.apply { this.color = color })
    }

     */

    companion object {

        @JvmStatic
        val DEFAULT_PALETTE_COLOR_LIST = intArrayOf(
            Color.rgb(255, 211, 0),
            Color.rgb(255, 255, 0),
            Color.rgb(159, 238, 0),
            Color.rgb(0, 204, 0),
            Color.rgb(0, 153, 153),
            Color.rgb(18, 64, 171),
            Color.rgb(57, 20, 176),
            Color.rgb(113, 9, 171),
            Color.rgb(205, 0, 116),
            Color.rgb(255, 0, 0),
            Color.rgb(255, 116, 0),
            Color.rgb(255, 170, 0),
            Color.rgb(255, 211, 0),
        )

    }
}