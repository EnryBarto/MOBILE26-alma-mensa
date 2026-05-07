package it.unibo.almamensa.ui.screens.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val title: String = "Benvenuto!",
    val subtitle: String = "Da universitari per universitari",
    val description: String = "Benvenuto su AlmaMensa, la piattaforma dedicata agli studenti universitari per scoprire, recensire e prenotare posti nelle mense universitarie. La nostra missione è migliorare l'esperienza culinaria degli studenti, offrendo un accesso facile e veloce alle informazioni sulle mense, permettendo di condividere opinioni e valutazioni, e semplificando il processo di prenotazione dei pasti. Unisciti a noi per rendere ogni pasto un momento piacevole e senza stress!"
)
class HomeViewModel : ViewModel() {
    val state = MutableStateFlow(HomeState()).asStateFlow()
}