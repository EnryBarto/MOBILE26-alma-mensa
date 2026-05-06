package it.unibo.almamensa.data.repositories

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.PostgrestResult
import it.unibo.almamensa.data.model.Booking

interface BookingRepository {
    suspend fun getAllBookings(): List<Booking>
    suspend fun getBookingById(id: Long): Booking
    suspend fun getBookingsByUserId(userId: Long): List<Booking>
    suspend fun getBookingsByCanteenId(canteenId: Long): List<Booking>
    suspend fun insertBooking(booking: Booking): PostgrestResult
    suspend fun updateBooking(id: Long, booking: Booking): PostgrestResult
    suspend fun deleteBooking(id: Long): PostgrestResult
}

class BookingRepositoryImpl(private val supabase: SupabaseClient) : BookingRepository {
    override suspend fun getAllBookings(): List<Booking> =
        supabase.from("booking").select().decodeList<Booking>()

    override suspend fun getBookingById(id: Long): Booking =
        supabase.from("booking")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Booking>()

    override suspend fun getBookingsByUserId(userId: Long): List<Booking> =
        supabase.from("booking")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Booking>()

    override suspend fun getBookingsByCanteenId(canteenId: Long): List<Booking> =
        supabase.from("booking")
            .select {
                filter {
                    eq("canteen_id", canteenId)
                }
            }
            .decodeList<Booking>()

    override suspend fun insertBooking(booking: Booking): PostgrestResult =
        supabase.from("booking").insert(booking)

    override suspend fun updateBooking(id: Long, booking: Booking): PostgrestResult =
        supabase.from("booking")
            .update(booking) {
                filter {
                    eq("id", id)
                }
            }

    override suspend fun deleteBooking(id: Long): PostgrestResult =
        supabase.from("booking")
            .delete {
                filter {
                    eq("id", id)
                }
            }
}
