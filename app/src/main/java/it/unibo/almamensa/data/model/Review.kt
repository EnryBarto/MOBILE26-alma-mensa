package it.unibo.almamensa.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: Long? = null,

    @SerialName("created_at")
    val createdAt: Instant? = null,

    val score: Int,
    val description: String? = null,
    val title: String,

    @SerialName("canteen_id")
    val canteenId: Long,
    @SerialName("user_id")
    val userId: String
)
