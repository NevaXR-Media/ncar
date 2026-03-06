package com.nevaxr.foundation.car.demo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nevaxr.foundation.car.demo.R

val DefaultFontFamily = FontFamily(
  Font(R.font.circularxx_black, FontWeight.Black, FontStyle.Normal),
  Font(R.font.circularxx_blackitalic, FontWeight.Black, FontStyle.Italic),
  Font(R.font.circularxx_bold, FontWeight.Bold, FontStyle.Normal),
  Font(R.font.circularxx_bolditalic, FontWeight.Bold, FontStyle.Italic),
  Font(R.font.circularxx_extrablack, FontWeight.ExtraBold, FontStyle.Normal),
  Font(R.font.circularxx_extrablackitalic, FontWeight.ExtraBold, FontStyle.Italic),
  Font(R.font.circularxx_italic, FontWeight.Normal, FontStyle.Italic),
  Font(R.font.circularxx_light, FontWeight.Light, FontStyle.Normal),
  Font(R.font.circularxx_light_italic, FontWeight.Light, FontStyle.Italic),
  Font(R.font.circularxx_medium, FontWeight.Medium, FontStyle.Normal),
  Font(R.font.circularxx_medium_italic, FontWeight.Medium, FontStyle.Italic),
  Font(R.font.circularxx_regular, FontWeight.Normal, FontStyle.Normal),
  Font(R.font.circularxx_thin, FontWeight.Thin, FontStyle.Normal),
  Font(R.font.circularxx_thin_italic, FontWeight.Thin, FontStyle.Italic),
)

private fun textStyle(
  size: Float,
  lineHeight: Float,
  weight: FontWeight = FontWeight.Normal,
  family: FontFamily = DefaultFontFamily,
) = TextStyle(
  fontFamily = family,
  fontWeight = weight,
  fontSize = size.sp,
  lineHeight = lineHeight.sp,
)

val Typography = Typography(
  displayLarge = textStyle(34f, 42f, FontWeight.ExtraBold),
  displayMedium = textStyle(28f, 34f, FontWeight.Bold),
  displaySmall = textStyle(22f, 28f, FontWeight.Bold),
  headlineLarge = textStyle(20f, 25f, FontWeight.Bold),
  headlineMedium = textStyle(17f, 22f, FontWeight.Medium),
  headlineSmall = textStyle(17f, 22f, FontWeight.Medium),
  titleLarge = textStyle(20f, 25f, FontWeight.Medium),
  titleMedium = textStyle(17f, 22f, FontWeight.Medium),
  titleSmall = textStyle(15f, 21f, FontWeight.Medium),
  bodyLarge = textStyle(17f, 22f, FontWeight.Normal),
  bodyMedium = textStyle(16f, 21f, FontWeight.Normal),
  bodySmall = textStyle(15f, 20f, FontWeight.Normal),
  labelLarge = textStyle(12f, 16f, FontWeight.Medium),
  // The provided line height was 3f; using 13f avoids broken rendering while preserving the scale.
  labelMedium = textStyle(11f, 13f, FontWeight.Medium),
  labelSmall = textStyle(11f, 13f, FontWeight.Normal),
)

