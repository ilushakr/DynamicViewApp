package com.example.design.ui.xml

import android.graphics.Color
import com.example.design.provider.api.ColorSchemeDirection
import com.example.design.provider.api.ColorSchemeTemplate
import com.example.design.provider.api.ColorSchemeType
import com.example.design.provider.api.CurrentAppTheme
import com.example.design.provider.api.CurrentPalette
import com.example.design.provider.api.PaletteMode
import com.example.design.ui.xml.widgets.color_picker.ColorPickerView

//val DEFAULT_LIGHT_APP_THEME =  CurrentAppTheme(
//    backgroundColor = -788742,
//    surfaceColor = -788742,
//    primarySurfaceColor = -148289,
//    secondaryBackgroundColor = Color.BLUE.mixColorWith(0.1f),
//    systemBarsColor = Color.YELLOW.mixColorWith(0.8f),
//    backgroundDarkColor = Color.YELLOW.mixColorWith(0.8f),
//    secondaryBackgroundDarkColor = Color.BLUE.mixColorWith(0.8f),
//    primaryDarkColor = Color.RED.mixColorWith(0.8f),
//    primaryColorAngle = (Math.PI / 2).toFloat(),
//    template = ColorSchemeTemplate(
//        type = ColorSchemeType.Triangle,
//        typeAngle = ColorPickerView.ColorSchemeTypeUiModel.Triangle.typeDefaultAngle,
//        direction = ColorSchemeDirection.Forward
//    )
//)
//
//val DEFAULT_DARK_APP_THEME = CurrentAppTheme(
//    backgroundColor = Color.YELLOW.mixColorWith(0.1f),
//    surfaceColor = Color.YELLOW.mixColorWith(0.1f),
//    primarySurfaceColor = Color.RED.mixColorWith(0.1f),
//    secondaryBackgroundColor = Color.BLUE.mixColorWith(0.1f),
//    systemBarsColor = Color.YELLOW.mixColorWith(0.8f),
//    backgroundDarkColor = Color.YELLOW.mixColorWith(0.8f),
//    secondaryBackgroundDarkColor = Color.BLUE.mixColorWith(0.8f),
//    primaryDarkColor = Color.RED.mixColorWith(0.8f),primaryColorAngle = (Math.PI / 4).toFloat(),
//    template = ColorSchemeTemplate(
//        type = ColorSchemeType.Analog,
//        typeAngle = ColorPickerView.ColorSchemeTypeUiModel.Analog.typeDefaultAngle,
//        direction = ColorSchemeDirection.Reversed
//    )
//)
//
//val DEFAULT_PALETTE = CurrentPalette(
//    lightTheme = DEFAULT_LIGHT_APP_THEME,
//    darkTheme = DEFAULT_DARK_APP_THEME,
//    mode = PaletteMode.Light
//)