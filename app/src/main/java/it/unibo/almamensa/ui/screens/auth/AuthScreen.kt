package it.unibo.almamensa.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.R
import it.unibo.almamensa.utils.Dimensions

@Composable
fun AuthScreen(
    state: AuthState,
    isModifyingPassword: Boolean = false,
    onEmailChange: (String) -> Unit,
    onSignIn: (String) -> Unit,
    onSignUp: (String, String, String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onGitHubSignIn: () -> Unit,
    onAuthSuccess: () -> Unit,
    onUpdateSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    // Auto-redirect to home when the user is authenticated (only if not modifying password)
    LaunchedEffect(state.sessionStatus) {
        if (!isModifyingPassword && state.sessionStatus is SessionStatus.Authenticated) {
            onAuthSuccess()
        }
    }

    // Call onUpdateSuccess when the password update is successful
    LaunchedEffect(state.isUpdateSuccess) {
        if (state.isUpdateSuccess) {
            onUpdateSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimensions.screenHorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                isModifyingPassword -> "Modifica Password"
                isRegistering -> "Crea Account"
                else -> "Bentornato"
            },
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isModifyingPassword) {
            if (isRegistering) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Cognome") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (isModifyingPassword) "Nuova Password" else "Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    when {
                        isModifyingPassword -> onUpdatePassword(password)
                        isRegistering -> onSignUp(password, name, surname)
                        else -> onSignIn(password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = password.isNotBlank() && (isModifyingPassword || (state.email.isNotBlank() && (!isRegistering || (name.isNotBlank() && surname.isNotBlank()))))
            ) {
                Text(
                    text = when {
                        isModifyingPassword -> "Aggiorna Password"
                        isRegistering -> "Registrati"
                        else -> "Accedi"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (!isModifyingPassword) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "oppure",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onGitHubSignIn,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Assuming you have a github icon in res/drawable
                        // If not, I'll just use a generic icon for now or Text
                        // Icon(painter = painterResource(id = R.drawable.ic_github), contentDescription = null, modifier = Modifier.size(20.dp))
                        // Spacer(modifier = Modifier.width(8.dp))
                        Text("Accedi con GitHub")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { isRegistering = !isRegistering }) {
                    Text(
                        if (isRegistering) "Hai già un account? Accedi"
                        else "Non hai un account? Registrati",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
