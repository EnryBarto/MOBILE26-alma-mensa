package it.unibo.almamensa.data.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewWithCanteenDto(
    val id: Long? = null,

    @SerialName("created_at")
    val createdAt: Instant,
    val score: Int,
    val description: String? = null,
    val title: String,
    @SerialName("canteen_id")
    val canteenId: Long,
    @SerialName("user_id")
    val userId: String,
    val canteen: CanteenNameDto // Rinomato da canteens a canteen per matchare il nome tabella
)

@Serializable
data class CanteenNameDto(
    val name: String
)