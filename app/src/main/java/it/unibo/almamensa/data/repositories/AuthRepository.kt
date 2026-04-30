package it.unibo.almamensa.data.repositories

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

interface AuthRepository {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    fun getCurrentUserEmail(): String?
}

class AuthRepositoryImpl(private val supabase: SupabaseClient) : AuthRepository {
    override suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUp(email: String, password: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override fun getCurrentUserEmail(): String? = supabase.auth.currentSessionOrNull()?.user?.email
}
