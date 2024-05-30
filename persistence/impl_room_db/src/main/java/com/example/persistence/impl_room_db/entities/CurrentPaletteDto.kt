package com.example.persistence.impl_room_db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity
//data class CurrentPaletteDto(
//    @PrimaryKey val id: Int,
//    @ColumnInfo(name = "first_name") val firstName: String?,
//    @ColumnInfo(name = "last_name") val lastName: String?
//)

internal data class ColorDataDto(
    val baseColor: Int,
    val darkColor: Int,
    val lightColor: Int
)

internal data class ColorSchemeTemplateDto(
    val type: ColorSchemeTypeDto,
    val typeAngle: Float,
    val direction: ColorSchemeDirectionDto
)

internal enum class ColorSchemeTypeDto {
    Triangle, Analog
}

internal enum class ColorSchemeDirectionDto {
    Forward, Reversed
}

@Entity
internal data class CurrentPaletteDto(
    @PrimaryKey val id: Int,
    val primaryColor: ColorDataDto,
    val rightColor: ColorDataDto,
    val leftColor: ColorDataDto,
    val primaryColorAngle: Float,
    val template: ColorSchemeTemplateDto
)
