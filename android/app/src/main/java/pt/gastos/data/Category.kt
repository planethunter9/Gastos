package pt.gastos.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Categorias fixas. Cada uma tem nome em PT-PT, ícone e cor própria
// para ser reconhecível de relance no dashboard e na grelha.
enum class Category(val label: String, val icon: ImageVector, val color: Color) {
    ALIMENTACAO("Supermercado", Icons.Rounded.ShoppingBasket, Color(0xFF2E8B57)),
    RESTAURANTES("Restaurantes", Icons.Rounded.Restaurant, Color(0xFFE07A3F)),
    TRANSPORTES("Transportes", Icons.Rounded.DirectionsCar, Color(0xFF3B7DD8)),
    CASA("Casa", Icons.Rounded.Home, Color(0xFF8E6BBF)),
    COMPRAS("Compras", Icons.Rounded.ShoppingBag, Color(0xFFD8508A)),
    SAUDE("Saúde/Cosmética", Icons.Rounded.Spa, Color(0xFFD64545)),
    LAZER("Lazer", Icons.Rounded.SportsEsports, Color(0xFF1FA3A3)),
    SUBSCRICOES("Subscrições", Icons.Rounded.Subscriptions, Color(0xFF5C6BC0)),
    EDUCACAO("Educação", Icons.Rounded.School, Color(0xFFB08D2B)),
    OUTROS("Outros", Icons.Rounded.MoreHoriz, Color(0xFF7A8A99));
}
