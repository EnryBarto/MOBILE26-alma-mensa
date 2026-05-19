package it.unibo.almamensa.ui.screens.profile.review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import it.unibo.almamensa.ui.composables.UserReviews

@Composable
fun PersonalReviewScreen(
    state: PersonalReviewState,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onReviewClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onRefresh()
    }

    Scaffold(
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading && state.reviews.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.reviews.isEmpty() -> {
                    Text(
                        text = "Non hai ancora scritto nessuna recensione.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    UserReviews(
                        reviews = state.reviews,
                        onReviewClick = onReviewClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }

            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}