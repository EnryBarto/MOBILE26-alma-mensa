package it.unibo.almamensa.ui.screens.canteenDetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.Canteen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenDetailsScreen(
    viewModel: CanteenDetailsViewModel,
    onReview: () -> Unit,
    onBook: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Show error as snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                        state.canteen?.name ?: "Dettagli Mensa",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            CanteenDetailsBottomBar(
                onReview = onReview,
                onBook = onBook
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.canteen != null -> {
                    CanteenDetailsContent(canteen = state.canteen!!)
                }
            }
        }
    }
}

@Composable
private fun CanteenDetailsContent(canteen: Canteen) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Description
        if (!canteen.description.isNullOrBlank()) {
            Text(
                text = canteen.description,
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider()
        }

        // Info section
        Text(
            text = "Informazioni",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        InfoItem(
            icon = Icons.Default.LocationOn,
            label = "Indirizzo",
            value = canteen.address
        )

        if (!canteen.phone.isNullOrBlank()) {
            InfoItem(
                icon = Icons.Default.Phone,
                label = "Telefono",
                value = canteen.phone
            )
        }

        HorizontalDivider()

        // Map section (Coordinates for now)
        Text(
            text = "Posizione",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Latitudine:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = canteen.latitude.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Longitudine:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = canteen.longitude.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CanteenDetailsBottomBar(
    onReview: () -> Unit,
    onBook: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onReview,
                modifier = Modifier.weight(1f)
            ) {
                Text("Fai una Recensione")
            }
            Button(
                onClick = onBook,
                modifier = Modifier.weight(1f)
            ) {
                Text("Prenota")
            }
        }
    }
}
