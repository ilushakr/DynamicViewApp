package com.example.design.provider.impl

import android.content.Context
import com.example.design.provider.api.BarsHeightsHolder
import com.example.design.provider.api.ColorSchemeDirection
import com.example.design.provider.api.ColorSchemeTemplate
import com.example.design.provider.api.ColorSchemeType
import com.example.design.provider.api.CurrentAppTheme
import com.example.design.provider.api.CurrentPalette
import com.example.design.provider.api.PaletteMode
import com.example.design.provider.api.ResourceProvider
import com.example.preferences.api.PreferencesProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

import java.io.InputStream


class ResourceProviderImpl(
    private val appContext: Context,
    private val prefsProvider: PreferencesProvider
) : ResourceProvider {

    private val _cornerRadiusState = MutableStateFlow<Float?>(null)
    private val _textSizeState = MutableStateFlow<Float?>(null)
    private val _currentPaletteState = MutableStateFlow<CurrentPalette?>(null)

    private var _barsHeightsHolder: BarsHeightsHolder = BarsHeightsHolder(0, 0)

    private val predefinedPalette by lazy {
        try {
            val stream: InputStream = appContext.assets.open("predefined_palette.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }?.let {
            Json.decodeFromString<CurrentPaletteSerializable>(it)
        }?.let {
            CurrentPalette(
                lightTheme = it.lightTheme.getTheme(),
                darkTheme = it.darkTheme.getTheme(),
                PaletteMode.valueOf(it.mode.name)
            )
        }
    }

    init {
        _cornerRadiusState.value = prefsProvider.getFloat(CORNER_RADIUS)
        _textSizeState.value = prefsProvider.getFloat(TEXT_SIZE)

        _currentPaletteState.value = prefsProvider.getString(CURRENT_PALETTE).takeIf { !it.isNullOrBlank() }?.let {
            with(Json.decodeFromString<CurrentPaletteSerializable>(it)) {
                CurrentPalette(
                    lightTheme = this.lightTheme.getTheme(),
                    darkTheme = this.darkTheme.getTheme(),
                    PaletteMode.valueOf(this.mode.name)
                )
            }
        } ?: predefinedPalette
    }


    override val cornerRadiusState: StateFlow<Float?>
        get() = _cornerRadiusState

    override var cornerRadius: Float?
        get() = _cornerRadiusState.value
        set(value) {
            _cornerRadiusState.value = value
            prefsProvider.setValue(CORNER_RADIUS, value)
        }


    override val textSizeState: StateFlow<Float?>
        get() = _textSizeState

    override var textSize: Float?
        get() = _textSizeState.value
        set(value) {
            _textSizeState.value = value
            prefsProvider.setValue(TEXT_SIZE, value)
        }

    override val currentPaletteState: StateFlow<CurrentPalette?>
        get() = _currentPaletteState


    override var currentPalette: CurrentPalette?
        get() = _currentPaletteState.value
        set(value) {
            _currentPaletteState.value = value
            value?.let {
                val json = Json.encodeToString(
                    CurrentPaletteSerializable(
                        lightTheme = value.lightTheme.getTheme(),
                        darkTheme = value.darkTheme.getTheme(),
                        mode = PaletteModeSerializable.valueOf(value.mode.name)
                    )
                )

                prefsProvider.setValue(CURRENT_PALETTE, json)
            } ?: prefsProvider.setValue(CURRENT_PALETTE, null)

        }

    override var barsHeightsHolder: BarsHeightsHolder
        get() = _barsHeightsHolder
        set(value) {
            _barsHeightsHolder = value
        }

    private fun CurrentAppThemeSerializable.getTheme(): CurrentAppTheme {
        return with(this) {
            CurrentAppTheme(
                backgroundColor = backgroundColor,
                surfaceColor = surfaceColor,
                primarySurfaceColor = primarySurfaceColor,
                secondaryBackgroundColor = secondaryBackgroundColor,
                systemBarsColor = systemBarsColor,
                backgroundDarkColor = backgroundDarkColor,
                secondaryBackgroundDarkColor = secondaryBackgroundDarkColor,
                primaryDarkColor = primaryDarkColor,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                primaryColorAngle = this.primaryColorAngle,
                template = with(this.template) {
                    ColorSchemeTemplate(
                        type = ColorSchemeType.valueOf(type.name),
                        typeAngle = typeAngle,
                        direction = ColorSchemeDirection.valueOf(direction.name)
                    )
                }
            )
        }
    }

    private fun CurrentAppTheme.getTheme(): CurrentAppThemeSerializable {
        return with(this) {
            CurrentAppThemeSerializable(
                backgroundColor = backgroundColor,
                surfaceColor = surfaceColor,
                primarySurfaceColor = primarySurfaceColor,
                secondaryBackgroundColor = secondaryBackgroundColor,
                systemBarsColor = systemBarsColor,
                backgroundDarkColor = backgroundDarkColor,
                secondaryBackgroundDarkColor = secondaryBackgroundDarkColor,
                primaryDarkColor = primaryDarkColor,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                primaryColorAngle = this.primaryColorAngle,
                template = with(this.template) {
                    ColorSchemeTemplateSerializable(
                        type = ColorSchemeTypeSerializable.valueOf(type.name),
                        typeAngle = typeAngle,
                        direction = ColorSchemeDirectionSerializable.valueOf(direction.name)
                    )
                }
            )
        }
    }

    companion object {
        private const val CORNER_RADIUS = "CORNER_RADIUS"
        private const val TEXT_SIZE = "TEXT_SIZE"
        private const val CURRENT_PALETTE = "CURRENT_PALETTE"
    }
}