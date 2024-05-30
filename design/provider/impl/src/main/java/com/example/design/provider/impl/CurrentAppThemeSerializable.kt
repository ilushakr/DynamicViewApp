package com.example.design.provider.impl

import kotlinx.serialization.Serializable

//@Serializable
//internal data class ColorDataSerializable(
//    val baseColor: Int,
//    val darkColor: Int,
//    val lightColor: Int
//)

@Serializable
internal data class ColorSchemeTemplateSerializable(
    val type: ColorSchemeTypeSerializable,
    val typeAngle: Float,
    val direction: ColorSchemeDirectionSerializable
)

@Serializable
internal enum class ColorSchemeTypeSerializable {
    Triangle, Analog
}

@Serializable
internal enum class ColorSchemeDirectionSerializable {
    Forward, Reversed
}

@Serializable
internal data class CurrentAppThemeSerializable(
    val backgroundColor: Int,
    val surfaceColor: Int,
    val primarySurfaceColor: Int,
    val secondaryBackgroundColor: Int,
    val systemBarsColor: Int,
    val backgroundDarkColor: Int,
    val secondaryBackgroundDarkColor: Int,
    val primaryDarkColor: Int,
    val primaryColor: Int,
    val secondaryColor: Int,

    val primaryColorAngle: Float,
    val template: ColorSchemeTemplateSerializable
)

@Serializable
internal enum class PaletteModeSerializable {
    Light, Dark
}

@Serializable
internal data class CurrentPaletteSerializable(
    val lightTheme: CurrentAppThemeSerializable,
    val darkTheme: CurrentAppThemeSerializable,
    val mode: PaletteModeSerializable
)
