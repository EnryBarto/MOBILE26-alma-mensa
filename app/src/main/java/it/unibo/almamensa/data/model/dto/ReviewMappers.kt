package it.unibo.almamensa.data.model.dto

import it.unibo.almamensa.data.model.Review

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