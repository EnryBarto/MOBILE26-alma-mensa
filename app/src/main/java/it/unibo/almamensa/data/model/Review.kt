package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: Long? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    val score: Int,
    val description: String? = null,
    val title: String? = null,

    @SerialName("canteen_id")
    val canteenId: Long? = null,
    @SerialName("user_id")
    val userId: Long? = null
)
