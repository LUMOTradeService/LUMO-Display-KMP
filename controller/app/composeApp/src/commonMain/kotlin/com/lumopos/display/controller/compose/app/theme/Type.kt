package com.lumopos.display.android.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import lumodisplay.screen.app.composeapp.generated.resources.Res
import lumodisplay.screen.app.composeapp.generated.resources.comfortaa_bold
import lumodisplay.screen.app.composeapp.generated.resources.comfortaa_light
import lumodisplay.screen.app.composeapp.generated.resources.comfortaa_medium
import lumodisplay.screen.app.composeapp.generated.resources.comfortaa_regular
import lumodisplay.screen.app.composeapp.generated.resources.comfortaa_semibold
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_black
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_bold
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_extrabold
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_extralight
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_light
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_medium
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_regular
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_semibold
import lumodisplay.screen.app.composeapp.generated.resources.montserrat_thin
import org.jetbrains.compose.resources.Font

@Composable
fun bodyFontFamily() = FontFamily(
    Font(Res.font.comfortaa_light, FontWeight.Light),
    Font(Res.font.comfortaa_regular, FontWeight.Normal),
    Font(Res.font.comfortaa_medium, FontWeight.Medium),
    Font(Res.font.comfortaa_semibold, FontWeight.SemiBold),
    Font(Res.font.comfortaa_bold, FontWeight.Bold)
)

@Composable
fun displayFontFamily() = FontFamily(
    Font(
        Res.font.montserrat_thin,
        FontWeight.Thin
    ),
    Font(
        Res.font.montserrat_extralight,
        FontWeight.ExtraLight
    ),
    Font(
        Res.font.montserrat_light,
        FontWeight.Light
    ),
    Font(
        Res.font.montserrat_regular,
        FontWeight.Normal
    ),
    Font(
        Res.font.montserrat_medium,
        FontWeight.Medium
    ),
    Font(
        Res.font.montserrat_semibold,
        FontWeight.SemiBold
    ),
    Font(
        Res.font.montserrat_bold,
        FontWeight.Bold
    ),
    Font(
        Res.font.montserrat_extrabold,
        FontWeight.ExtraBold
    ),
    Font(
        Res.font.montserrat_black,
        FontWeight.Black
    ),
)

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun appTypography() = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily()),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily()),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily()),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily()),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily()),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily()),
    titleLarge = baseline.titleLarge.copy(fontFamily = bodyFontFamily()),
    titleMedium = baseline.titleMedium.copy(fontFamily = bodyFontFamily()),
    titleSmall = baseline.titleSmall.copy(fontFamily = bodyFontFamily()),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily()),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily()),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily()),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily()),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily()),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily()),
    displayLargeEmphasized = baseline.displayLargeEmphasized.copy(fontFamily = displayFontFamily()),
    displayMediumEmphasized = baseline.displayMediumEmphasized.copy(fontFamily = displayFontFamily()),
    displaySmallEmphasized = baseline.displaySmallEmphasized.copy(fontFamily = displayFontFamily()),
    headlineLargeEmphasized = baseline.headlineLargeEmphasized.copy(fontFamily = displayFontFamily()),
    headlineMediumEmphasized = baseline.headlineMediumEmphasized.copy(fontFamily = displayFontFamily()),
    headlineSmallEmphasized = baseline.headlineSmallEmphasized.copy(fontFamily = displayFontFamily()),
    titleLargeEmphasized = baseline.titleLargeEmphasized.copy(fontFamily = bodyFontFamily()),
    titleMediumEmphasized = baseline.titleMediumEmphasized.copy(fontFamily = bodyFontFamily()),
    titleSmallEmphasized = baseline.titleSmallEmphasized.copy(fontFamily = bodyFontFamily()),
    bodyLargeEmphasized = baseline.bodyLargeEmphasized.copy(fontFamily = bodyFontFamily()),
    bodyMediumEmphasized = baseline.bodyMediumEmphasized.copy(fontFamily = bodyFontFamily()),
    bodySmallEmphasized = baseline.bodySmallEmphasized.copy(fontFamily = bodyFontFamily()),
    labelLargeEmphasized = baseline.labelLargeEmphasized.copy(fontFamily = bodyFontFamily()),
    labelMediumEmphasized = baseline.labelMediumEmphasized.copy(fontFamily = bodyFontFamily()),
    labelSmallEmphasized = baseline.labelSmallEmphasized.copy(fontFamily = bodyFontFamily()),
)