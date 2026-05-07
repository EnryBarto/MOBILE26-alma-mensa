package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User (
    val id: String,
    val email: String,
    val name: String,
    val surname: String,
    @SerialName("profile_photo_url") val profilePhotoUrl: String?
)