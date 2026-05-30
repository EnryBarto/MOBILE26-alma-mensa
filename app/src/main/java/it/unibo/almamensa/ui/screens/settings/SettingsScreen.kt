package it.unibo.almamensa.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import it.unibo.almamensa.data.model.Theme
import it.unibo.almamensa.ui.composables.RadioListItem
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.Dimensions.controlElementLabelPadding

@Composable
fun SettingsScreen(
    settingsState: SettingsState,
    onThemeChange: (Theme) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(Dimensions.verticalItemsSpacing)
    ) {
        Text(
            text = "Impostazioni",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider()

        Text(
            text = "Tema:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Column {
            Theme.entries.forEach { theme ->
                RadioListItem(
                    label = theme.toString(),
                    selected = (theme == settingsState.theme),
                    onClick = { onThemeChange(theme) },
                )
            }
        }

        HorizontalDivider()

        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Switch(
                checked = settingsState.dynamicColor,
                onCheckedChange = { checked -> onDynamicColorChange(checked) }
            )

            Text(
                text = "Colori di Sistema",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = controlElementLabelPadding)
            )
        }
    }
}