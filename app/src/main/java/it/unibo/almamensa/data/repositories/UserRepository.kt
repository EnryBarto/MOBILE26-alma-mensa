package it.unibo.almamensa.data.repositories

import android.net.Uri
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import it.unibo.almamensa.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.Serializable

// Associate the image version to the User to know when it needs to be updated in the UI
data class UserWithVersion(
    val user: User?,
    val imageVersion: Long = 0
)

interface UserRepository {
    val myProfile: Flow<UserWithVersion>

    suspend fun getUserById(userId: String): User?
    suspend fun updateUser(name: String, surname: String)
    suspend fun uploadProfilePicture(uri: Uri)
    suspend fun deleteProfilePicture()
    suspend fun getMyProfile(): User?
    suspend fun clearProfile()
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

    private val _myProfile = MutableSharedFlow<UserWithVersion>(replay = 1)
    override val myProfile: Flow<UserWithVersion> = _myProfile

    override suspend fun getUserById(userId: String): User? =
        try {
            supabase.from("user")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<User>()
        } catch (e: HttpRequestException) {
            null
        }

    override suspend fun updateUser(name: String, surname: String) {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")

        supabase.from("user")
            .update(ProfileUpdate(name = name, surname = surname)) {
                filter {
                    eq("id", userId)
                }
            }
        getMyProfile()
    }

    override suspend fun uploadProfilePicture(uri: Uri) {
        supabase.auth.awaitInitialization() // Wait for the supabase client to be initialized, sometimes it can be killed, for example when the camera is closed after taking a photo

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
        getMyProfile()
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
        getMyProfile()
    }

    override suspend fun getMyProfile(): User? {
        val user = supabase.auth.currentUserOrNull()?.id?.let { getUserById(it) }
        _myProfile.emit(UserWithVersion(user, System.currentTimeMillis()))
        return user
    }

    override suspend fun clearProfile() {
        _myProfile.emit(UserWithVersion(null))
    }
}