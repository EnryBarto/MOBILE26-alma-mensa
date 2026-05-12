package it.unibo.almamensa.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Theme
import it.unibo.almamensa.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsState (
    val theme: Theme,
    val dynamicColor: Boolean
)

data class SettingsAction(
    val setTheme: (Theme) -> Unit,
    val setDynamicColor: (Boolean) -> Unit
)

class SettingsViewModel(repository: SettingsRepository) : ViewModel() {

    val state = combine(
        repository.theme,
        repository.dynamicColor
    ) { theme, dynamicColor -> SettingsState(theme, dynamicColor) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SettingsState(Theme.Sistema, false)
        )

    val actions = SettingsAction(
        setTheme = { theme ->
            viewModelScope.launch { repository.setTheme(theme)  }
        },
        setDynamicColor = { enabled ->
            viewModelScope.launch { repository.setDynamicColor(enabled) }
        }
    )
}
