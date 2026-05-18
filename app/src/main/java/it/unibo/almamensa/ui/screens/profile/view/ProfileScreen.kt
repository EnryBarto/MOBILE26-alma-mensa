package it.unibo.almamensa.ui.screens.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.data.model.User
import it.unibo.almamensa.ui.composables.DoubleButtonBar
import it.unibo.almamensa.ui.composables.ProfilePhoto
import it.unibo.almamensa.ui.screens.auth.AuthState
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    profileState: ProfileState,
    onModifyPassword: () -> Unit,
    authState: AuthState,
    modifier: Modifier = Modifier,
    onLogoutSuccess: () -> Unit,
    onEditClick: () -> Unit,
    onShowReviews: () -> Unit
) {
    // Auto-redirect to home when the user is authenticated
    LaunchedEffect(authState.sessionStatus) {
        if (authState.sessionStatus is SessionStatus.NotAuthenticated) {
            onLogoutSuccess()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            profileState.errorMessage != null -> Text(
                text = profileState.errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            profileState.user != null -> {
                ProfileContent(
                    profileState.user,
                    imageVersion = profileState.imageVersion,
                    onModifyPassword = onModifyPassword
                )

                DoubleButtonBar(
                    textPrimary = "Modifica",
                    iconPrimary = Icons.Default.Edit,
                    onClickPrimary = onEditClick,
                    textSecondary = "Logout",
                    iconSecondary = Icons.AutoMirrored.Filled.Logout,
                    onClickSecondary = onLogout,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            profileState.isLoading -> CircularProgressIndicator()

            else -> Text("ERRORE: Nessun profilo trovato")
        }
    }
}

@Composable
private fun ProfileContent(user: User, imageVersion: Long = 0, onModifyPassword: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.screenHorizontalPadding)
            .padding(bottom = Dimensions.bottomPaddingButtonBar),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {

        val profilePhotoUrl = remember(user.profilePhotoUrl, imageVersion) {
            if (user.profilePhotoUrl != null && imageVersion > 0) {
                "${user.profilePhotoUrl}?v=$imageVersion"
            } else {
                user.profilePhotoUrl
            }
        }

        ProfilePhoto(profilePhotoUrl)

        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${user.name} ${user.surname}",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.labelMedium
            )
        }

        TextButton(onClick = onModifyPassword) {
            Text(
                text = "Modifica Password",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
