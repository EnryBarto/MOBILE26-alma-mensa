package it.unibo.almamensa.ui.screens.canteen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import it.unibo.almamensa.ui.composables.CanteenBottomBar
import it.unibo.almamensa.ui.composables.CanteenReviews
import it.unibo.almamensa.ui.composables.InfoItem
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.openDialer
import it.unibo.almamensa.utils.openMaps
import org.osmdroid.config.Configuration

@Composable
fun CanteenScreen(
    loggedIn: Boolean,
    viewModel: CanteenViewModel,
    onReview: () -> Unit,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    // Show error as snackbar
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.canteen != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = if (loggedIn) 100.dp else 16.dp)
                ) {
                    CanteenDetailsContent(canteen = state.canteen!!)
                    CanteenReviews(reviews = state.reviews)
                }

                if (loggedIn) {
                    CanteenBottomBar(
                        onReview = onReview,
                        onBook = onBook,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (loggedIn) 80.dp else 16.dp)
        )
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
            .padding(horizontal = Dimensions.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = canteen.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Description
        if (!canteen.description.isNullOrBlank()) {
            Text(
                text = canteen.description,
                style = MaterialTheme.typography.labelMedium
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
