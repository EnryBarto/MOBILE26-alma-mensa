package it.unibo.almamensa.ui.screens.canteen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.composables.InfoItem
import it.unibo.almamensa.utils.openDialer
import it.unibo.almamensa.utils.openMaps
import org.osmdroid.config.Configuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenScreen(
    loggedIn: Boolean,
    viewModel: CanteenViewModel,
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
            if (loggedIn) {
                CanteenDetailsBottomBar(
                    onReview = onReview,
                    onBook = onBook
                )
            }
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
    val context = LocalContext.current

    // Initialize OsmDroid configuration (required)
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", 0))
    }

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
            value = canteen.address,
            onClick = { openMaps(context, canteen) }
        )

        if (!canteen.phone.isNullOrBlank()) {
            InfoItem(
                icon = Icons.Default.Phone,
                label = "Telefono",
                value = canteen.phone,
                onClick = { openDialer(context, canteen.phone) }
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
                Text("Recensisci")
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
