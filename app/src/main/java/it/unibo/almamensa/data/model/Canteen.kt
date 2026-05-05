package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Canteen(
    val id: Int? = null,

    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,

    @SerialName("created_at")
    val createdAt: String? = null
)