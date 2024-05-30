package com.example.design.provider.api

import kotlinx.coroutines.flow.StateFlow

interface ResourceProvider {

    var cornerRadius: Float?
    val cornerRadiusState: StateFlow<Float?>

    var textSize: Float?
    val textSizeState: StateFlow<Float?>

    var currentPalette: CurrentPalette?
    val currentPaletteState: StateFlow<CurrentPalette?>

    var barsHeightsHolder: BarsHeightsHolder
}