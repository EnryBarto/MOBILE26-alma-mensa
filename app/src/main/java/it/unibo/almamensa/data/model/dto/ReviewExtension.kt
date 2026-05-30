package it.unibo.almamensa.data.model.dto

import it.unibo.almamensa.data.model.Review
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewWithCanteenDto(
    val id: Long,

    @SerialName("created_at")
    val createdAt: Instant,
    val score: Int,
    val description: String? = null,
    val title: String,
    @SerialName("canteen_id")
    val canteenId: Long,
    @SerialName("user_id")
    val userId: String,
    val canteen: CanteenNameDto
)

@Serializable
data class CanteenNameDto(
    val name: String
)

@Serializable
data class ReviewWithUserDto(
    val id: Long,

    @SerialName("created_at")
    val createdAt: Instant,
    val score: Int,
    val description: String? = null,
    val title: String,

    @SerialName("canteen_id")
    val canteenId: Long,

    @SerialName("user_id")
    val userId: String,
    // This maps the "user" nested in the JOIN
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val surname: String
)

fun ReviewWithCanteenDto.toReview() = Review(
    id = id,
    createdAt = createdAt,
    score = score,
    description = description,
    title = title,
    canteenId = canteenId,
    userId = userId
)

fun ReviewWithUserDto.toReview() = Review(
    id = id,
    createdAt = createdAt,
    score = score,
    description = description,
    title = title,
    canteenId = canteenId,
    userId = userId
)