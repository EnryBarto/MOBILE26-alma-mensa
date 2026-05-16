package it.unibo.almamensa.ui.screens.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.composables.CanteensMapView
import it.unibo.almamensa.ui.composables.SingleButtonBar

@Composable
fun MapScreen(
    state: MapState,
    onCanteenClick: (Canteen) -> Unit,
    modifier: Modifier = Modifier,
    onOpenNearMeClick: () -> Unit,
    isDarkTheme: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CanteensMapView(
            canteens = state.canteens,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onCanteenClick = onCanteenClick,
            isDarkTheme = isDarkTheme
        )
        SingleButtonBar(
            text = "Più vicine a me",
            icon = Icons.Default.NearMe,
            onClick = onOpenNearMeClick
        )
    }
}