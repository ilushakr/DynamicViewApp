package com.example.design.provider.api

typealias AppColor = Int

data class ColorSchemeTemplate(
    val type: ColorSchemeType,
    val typeAngle: Float,
    val direction: ColorSchemeDirection
)

enum class ColorSchemeType {
    Triangle, Analog
}

enum class ColorSchemeDirection {
    Forward, Reversed
}

data class CurrentAppTheme(
    val backgroundColor: AppColor,
    val surfaceColor: AppColor,
    val primarySurfaceColor: AppColor,
    val secondaryBackgroundColor: AppColor,
    val systemBarsColor: AppColor,
    val backgroundDarkColor: AppColor,
    val secondaryBackgroundDarkColor: AppColor,
    val primaryDarkColor: AppColor,
    val primaryColor: AppColor,
    val secondaryColor: AppColor,

    val primaryColorAngle: Float,
    val template: ColorSchemeTemplate
)

enum class PaletteMode {
    Light, Dark
}

data class CurrentPalette(
    val lightTheme: CurrentAppTheme,
    val darkTheme: CurrentAppTheme,
    val mode: PaletteMode
) : AppThemeColor {

    val currentTheme: CurrentAppTheme
        get() = when (mode) {
            PaletteMode.Light -> lightTheme
            PaletteMode.Dark -> darkTheme
        }

    val currentPrimaryAngleRad: Float
        get() = when (mode) {
            PaletteMode.Light -> lightTheme.primaryColorAngle
            PaletteMode.Dark -> darkTheme.primaryColorAngle
        }

    val currentTemplate: ColorSchemeTemplate
        get() = when (mode) {
            PaletteMode.Light -> lightTheme.template
            PaletteMode.Dark -> darkTheme.template
        }

    override val backgroundColor: AppColor
        get() = currentTheme.backgroundColor

    override val surfaceColor: AppColor
        get() = currentTheme.surfaceColor

    override val primarySurfaceColor: AppColor
        get() = currentTheme.primarySurfaceColor

    override val secondaryBackgroundColor: AppColor
        get() = currentTheme.secondaryBackgroundColor

    override val systemBarsColor: AppColor
        get() = currentTheme.systemBarsColor

    override val backgroundDarkColor: AppColor
        get() = currentTheme.backgroundDarkColor

    override val secondaryBackgroundDarkColor: AppColor
        get() = currentTheme.secondaryBackgroundDarkColor

    override val primaryDarkColor: AppColor
        get() = currentTheme.primaryDarkColor

    override val primaryColor: AppColor
        get() = currentTheme.primaryColor

    override val secondaryColor: AppColor
        get() = currentTheme.secondaryColor
}

interface AppThemeColor {
    val backgroundColor: AppColor
    val surfaceColor: AppColor
    val primarySurfaceColor: AppColor
    val secondaryBackgroundColor: AppColor
    val systemBarsColor: AppColor
    val primaryColor: AppColor
    val secondaryColor: AppColor

    val primaryDarkColor: AppColor
    val backgroundDarkColor: AppColor
    val secondaryBackgroundDarkColor: AppColor
}