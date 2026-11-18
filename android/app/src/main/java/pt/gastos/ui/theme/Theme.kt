package pt.gastos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Green = Color(0xFF1F6F5B)
val GreenSoft = Color(0xFFDDEFE6)
val Ink = Color(0xFF17201C)
val InkMuted = Color(0xFF6B7772)
val Paper = Color(0xFFF5F7F4)
val CardWhite = Color(0xFFFFFFFF)
val Line = Color(0xFFE3E8E4)
val Danger = Color(0xFFC63D3D)

// Só existe esquema claro: ignoramos deliberadamente isSystemInDarkTheme().
private val LightScheme = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    primaryContainer = GreenSoft,
    onPrimaryContainer = Green,
    secondary = Green,
    background = Paper,
    onBackground = Ink,
    surface = CardWhite,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEEF2EF),
    onSurfaceVariant = InkMuted,
    outline = Line,
    outlineVariant = Line,
    error = Danger,
    onError = Color.White,
)

private val GastosTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontWeight = FontWeight.Bold, fontSize = 40.sp, letterSpacing = (-1).sp),
    headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
)

@Composable
fun GastosTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightScheme, typography = GastosTypography, content = content)
}
