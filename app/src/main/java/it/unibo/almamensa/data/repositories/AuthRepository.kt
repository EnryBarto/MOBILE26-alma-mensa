package it.unibo.almamensa.data.repositories

import androidx.compose.material3.rememberSwipeToDismissBoxState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import it.unibo.almamensa.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String, name: String, surname: String)
    suspend fun signOut()
    suspend fun updatePassword(newPsw: String)
    suspend fun signInWithGitHub()
    fun sessionStatus(): Flow<SessionStatus>
}

class AuthRepositoryImpl(private val supabase: SupabaseClient) : AuthRepository {
    override suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUp(email: String, password: String, name: String, surname: String) {
        val userInfo = supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        
        // Retrieve the UUID from the auth registration
        val userId = userInfo?.id ?: throw Exception("Errore durante la creazione dell'utente")

        // After auth signup, we create the user entry in the user table
        val user = User(
            id = userId,
            email = email,
            name = name,
            surname = surname,
            profilePhotoUrl = null
        )
        
        supabase.postgrest["user"].insert(user)
    }

    override suspend fun signOut() {
        supabase.auth.signOut()
    }

    override suspend fun updatePassword(newPsw: String) {
        try {
            supabase.auth.updateUser {
                password = newPsw
            }
        } catch (e: Exception) {
            throw Exception("Errore durante la modifica della password")
        }
    }

    override suspend fun signInWithGitHub() {
        try {
            supabase.auth.signInWith(Github, "almamensa://auth-callback")
        } catch (e: Exception) {
            throw Exception("Errore durante il login con GitHub")
        }
    }

    override fun sessionStatus() = supabase.auth.sessionStatus
}
