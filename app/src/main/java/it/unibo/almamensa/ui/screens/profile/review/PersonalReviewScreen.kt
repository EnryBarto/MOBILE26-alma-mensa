package it.unibo.almamensa.ui.screens.profile.review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.unibo.almamensa.ui.composables.UserReviews
import org.koin.androidx.compose.koinViewModel

@Composable
fun PersonalReviewScreen(
    viewModel: PersonalReviewViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onEditReview: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.reviews.isEmpty()) {
                Text(
                    text = "Non hai ancora scritto nessuna recensione.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                UserReviews(
                    reviews = state.reviews,
                )
            }
        }
    }
}