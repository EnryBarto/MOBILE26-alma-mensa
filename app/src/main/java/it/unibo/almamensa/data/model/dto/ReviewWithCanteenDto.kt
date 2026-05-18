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
    val canteens: CanteenNameDto // Nome della tabella su Supabase
)

@Serializable
data class CanteenNameDto(
    val name: String
)