package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    // the Id of the user is the UUID from Supabase Auth
    val id: String,

    val name: String,
    val surname: String,
    @SerialName("profile_photo_url")
    val profilePhotoUrl: String? = null
)
