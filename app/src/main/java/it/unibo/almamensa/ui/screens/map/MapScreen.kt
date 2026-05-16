package it.unibo.almamensa.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.unibo.almamensa.ui.composables.CanteensMapView

@Composable
fun MapScreen(
    state: MapState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        CanteensMapView(canteens = state.canteens)
    }
}