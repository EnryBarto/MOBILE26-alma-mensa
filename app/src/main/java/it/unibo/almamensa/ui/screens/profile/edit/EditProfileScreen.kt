package it.unibo.almamensa.ui.screens.profile.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.ui.composables.ProfilePhoto
import it.unibo.almamensa.ui.composables.SingleButtonBar
import it.unibo.almamensa.utils.Dimensions

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    state: EditProfileState,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteImageClick: () -> Unit = {},
    onAvatarPicked: (Uri) -> Unit,
    onSaveSuccess: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaveSuccess()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAvatarPicked(it) }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.name.isNotEmpty() || state.surname.isNotEmpty() -> {
                EditProfileContent(
                    state = state,
                    onNameChange = onNameChange,
                    onSurnameChange = onSurnameChange,
                    onDeleteImageClick = onDeleteImageClick,
                    onPickImageClick = { imagePickerLauncher.launch("image/*") }
                )

                SingleButtonBar(
                    text = "Salva modifiche",
                    icon = Icons.Default.Save,
                    onClick = onSaveClick,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            state.isLoading -> CircularProgressIndicator()

            else -> CircularProgressIndicator()
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun EditProfileContent(
    state: EditProfileState,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onPickImageClick: () -> Unit,
    onDeleteImageClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.screenHorizontalPadding)
            .padding(bottom = Dimensions.bottomPaddingButtonBar),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(contentAlignment = Alignment.Center) {

            // Use the image versioning system to keep the photo updated
            val profilePhotoUrl = remember(state.profilePhotoUrl, state.imageVersion) {
                if (state.profilePhotoUrl != null && state.imageVersion > 0) {
                    "${state.profilePhotoUrl}?v=${state.imageVersion}"
                } else {
                    state.profilePhotoUrl
                }
            }

            ProfilePhoto(profilePhotoUrl = profilePhotoUrl)

            if (state.profilePhotoUrl != null) {
                IconButton(
                    onClick = onDeleteImageClick,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Rimuovi foto profilo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(
                onClick = onPickImageClick,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Cambia foto profilo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.surname,
            onValueChange = onSurnameChange,
            label = { Text("Cognome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}