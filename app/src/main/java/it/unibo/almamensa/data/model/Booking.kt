package it.unibo.almamensa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Booking(
    val id: Long,

    @SerialName("created_at")
    val createdAt: Instant,

    val numPeople: Int,
    @SerialName("booking_date")
    val bookingDate: Instant,
)
