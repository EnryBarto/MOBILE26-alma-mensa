package it.unibo.almamensa.data.repositories

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import it.unibo.almamensa.data.model.User

interface UserRepository {
    suspend fun getProfile(userId: String): User?
}

class ProfileRepositoryImpl(private val supabase: SupabaseClient) : UserRepository {

    override suspend fun getProfile(userId: String): User? =
        supabase.from("user")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<User>()
}