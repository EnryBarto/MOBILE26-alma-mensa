package it.unibo.almamensa.ui.screens.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.ui.composables.SingleButtonBar
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing

@Composable
fun ReviewScreen(
    state: ReviewState,
    onTitleChange: (String) -> Unit,
    onScoreChange: (Int) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBack()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.screenHorizontalPadding)
                .padding(bottom = Dimensions.bottomPaddingButtonBar),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
        ) {

            Text(
                text = if (state.isEditing) "Modifica recensione" else "Lascia una recensione",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isLoading
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Valutazione: ${state.score}/5",
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        Icon(
                            imageVector = if (starIndex <= state.score) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(enabled = !state.isLoading) {
                                    onScoreChange(starIndex)
                                }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Descrizione (opzionale)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                enabled = !state.isLoading
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }

        if (!state.isLoading) {
            SingleButtonBar(
                text = if (state.isEditing) "Salva modifiche" else "Invia recensione",
                icon = if (state.isEditing) Icons.Default.Edit else Icons.Default.Star,
                onClick = onSubmit,
                modifier = Modifier.align(Alignment.BottomCenter),
                enabled = state.title.isNotBlank()
            )
        }
    }
}
