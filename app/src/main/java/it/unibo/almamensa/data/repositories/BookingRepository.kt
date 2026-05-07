package it.unibo.almamensa.data.repositories

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import it.unibo.almamensa.data.model.Booking

interface BookingRepository {
    suspend fun getAllBookings(): List<Booking>
    suspend fun getBookingById(id: Long): Booking
    suspend fun getBookingsByUserId(userId: Long): List<Booking>
    suspend fun getBookingsByCanteenId(canteenId: Long): List<Booking>
    suspend fun insertBooking(booking: Booking): Booking
    suspend fun updateBooking(id: Long, booking: Booking): Booking
    suspend fun deleteBooking(id: Long): Boolean
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

    override suspend fun insertBooking(booking: Booking): Booking {
        return supabase.from("bookings")
            .insert(booking) {
                select()
            }
            .decodeSingle<Booking>()
    }

    override suspend fun updateBooking(id: Long, booking: Booking): Booking =
        supabase.from("booking")
            .update(booking) {
                filter {
                    eq("id", id)
                }
                select()
            }
            .decodeSingle<Booking>()

    override suspend fun deleteBooking(id: Long): Boolean {
        return try {
            supabase.from("bookings").delete {
                filter { eq("id", id) }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
