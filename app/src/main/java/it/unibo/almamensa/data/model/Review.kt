package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: Long,

    @SerialName("created_at")
    val createdAt: String,

    val score: Int,
    val description: String? = null,
    val title: String,

    @SerialName("canteen_id")
    val canteenId: Long,
    @SerialName("user_id")
    val userId: String
)
