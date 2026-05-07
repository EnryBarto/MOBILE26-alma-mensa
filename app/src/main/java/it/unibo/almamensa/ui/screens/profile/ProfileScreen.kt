package it.unibo.almamensa.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.User
import it.unibo.almamensa.ui.composables.ProfilePhoto
import it.unibo.almamensa.utils.Dimensions

@Composable
fun ProfileScreen(
    state: ProfileState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.screenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.errorMessage != null -> Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            state.user != null -> ProfileContent(state.user)

            else -> Text("ERRORE: Nessun profilo trovato")
        }
    }
}

@Composable
private fun ProfileContent(user: User) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        ProfilePhoto(user)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${user.name} ${user.surname}",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = user.email,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
