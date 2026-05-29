package it.unibo.almamensa.ui.model

import it.unibo.almamensa.data.model.Canteen

data class CanteenListItem(
    val canteen: Canteen,
    val distanceInfo: String? = null
)