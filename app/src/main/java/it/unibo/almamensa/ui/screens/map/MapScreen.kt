package it.unibo.almamensa.ui.screens.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.unibo.almamensa.ui.composables.CanteensMapView

@Composable
fun MapScreen(
    state: MapState,
    modifier: Modifier = Modifier
) {
    CanteensMapView(state.canteens)
}