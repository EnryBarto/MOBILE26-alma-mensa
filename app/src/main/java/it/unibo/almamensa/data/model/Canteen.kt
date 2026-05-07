package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Canteen(
    val id: Long,
    @SerialName("created_at") val createdAt: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val description: String? = "",
    val phone: String? = ""
)
