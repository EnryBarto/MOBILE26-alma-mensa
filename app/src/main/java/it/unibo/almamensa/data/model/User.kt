package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    // the Id of the user is taken from the auth db handled by supabase
    val id: Long,

    val name: String,
    val surname: String,
    val profilePhotoUrl: String? = null
)
