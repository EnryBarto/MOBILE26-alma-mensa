package it.unibo.almamensa.data.repositories

import android.net.Uri
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import it.unibo.almamensa.data.model.User
import kotlinx.serialization.Serializable

interface UserRepository {
    suspend fun getProfile(userId: String): User?
    suspend fun updateProfile(name: String, surname: String)
    suspend fun uploadProfilePicture(uri: Uri)
    suspend fun deleteProfilePicture()
    suspend fun getMyProfile(): User?
}

@Serializable
private data class ProfileUpdate(
    val name: String,
    val surname: String
)

class ProfileRepositoryImpl(
    private val supabase: SupabaseClient,
    private val context: android.content.Context
) : UserRepository {

    override suspend fun getProfile(userId: String): User? =
        supabase.from("user")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<User>()

    override suspend fun updateProfile(name: String, surname: String) {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")

        supabase.from("user")
            .update(ProfileUpdate(name = name, surname = surname)) {
                filter {
                    eq("id", userId)
                }
            }
    }

    override suspend fun uploadProfilePicture(uri: Uri) {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")

        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Cannot read image from URI")

        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val extension = when (mimeType) {
            "image/png"  -> "png"
            "image/webp" -> "webp"
            else         -> "jpg"
        }
        val path = "$userId/photo.$extension"

        supabase.storage
            .from("profile_photos")
            .upload(path, bytes) {
                upsert = true
            }

        val publicUrl = supabase.storage
            .from("profile_photos")
            .publicUrl(path)

        supabase.from("user")
            .update(mapOf("profile_photo_url" to publicUrl)) {
                filter {
                    eq("id", userId)
                }
            }
    }

    override suspend fun deleteProfilePicture() {
        val userId = supabase.auth.currentUserOrNull()?.id?: error("User not authenticated")

        val files = supabase.storage.from("profile_photos").list(userId)
        val toDelete = files.map { "$userId/${it.name}" }

        if (toDelete.isNotEmpty()) {
            supabase.storage.from("profile_photos").delete(toDelete)
        }

        supabase.from("user")
            .update(mapOf("profile_photo_url" to null)) {
                filter { eq("id", userId) }
            }
    }

    override suspend fun getMyProfile(): User? {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return null
        return getProfile(userId)
    }
}