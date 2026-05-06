package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Canteen(
    @SerialName("id")
    val id: Long,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("name")
    val name: String,

    @SerialName("address")
    val address: String,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("description")
    val description: String? = "",

    @SerialName("phone")
    val phone: String? = ""
)
