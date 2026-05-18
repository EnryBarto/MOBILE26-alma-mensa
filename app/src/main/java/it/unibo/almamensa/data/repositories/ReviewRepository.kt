package it.unibo.almamensa.data.repositories

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.result.PostgrestResult
import it.unibo.almamensa.data.model.Review
import it.unibo.almamensa.data.model.dto.ReviewWithCanteenDto
import it.unibo.almamensa.data.model.dto.ReviewWithUserDto

interface ReviewRepository {
    suspend fun getAllReviews(): List<Review>
    suspend fun getReviewById(id: Long): Review
    suspend fun getReviewsByCanteenId(canteenId: Long): List<Review>
    suspend fun getReviewsByUserId(userId: String): List<Review>
    suspend fun getReviewsByUserWithCanteen(userId: String): List<ReviewWithCanteenDto>
    suspend fun getReviewsWithUser(canteenId: Long): List<ReviewWithUserDto>
    suspend fun insertReview(review: Review): PostgrestResult
    suspend fun updateReview(id: Long, review: Review): PostgrestResult
    suspend fun deleteReview(id: Long): PostgrestResult
}

class ReviewRepositoryImpl(private val supabase: SupabaseClient) : ReviewRepository {
    override suspend fun getAllReviews(): List<Review> =
        supabase.from("review").select().decodeList<Review>()

    override suspend fun getReviewById(id: Long): Review =
        supabase.from("review")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Review>()

    override suspend fun getReviewsByCanteenId(canteenId: Long): List<Review> =
        supabase.from("review")
            .select {
                filter {
                    eq("canteen_id", canteenId)
                }
            }
            .decodeList<Review>()

    override suspend fun getReviewsByUserId(userId: String): List<Review> =
        supabase.from("review")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Review>()

    override suspend fun getReviewsByUserWithCanteen(userId: String): List<ReviewWithCanteenDto> {
        return supabase.from("review")
            .select(Columns.raw("*, canteen(name)")) {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<ReviewWithCanteenDto>()
    }

    override suspend fun getReviewsWithUser(canteenId: Long): List<ReviewWithUserDto> {
        return supabase.from("review")
            .select(Columns.raw("*, user(id, name, surname)")) {
                filter {
                    eq("canteen_id", canteenId)
                }
            }
            .decodeList<ReviewWithUserDto>()
    }

    override suspend fun insertReview(review: Review): PostgrestResult =
        supabase.from("review").insert(review)

    override suspend fun updateReview(id: Long, review: Review): PostgrestResult =
        supabase.from("review")
            .update(review) {
                filter {
                    eq("id", id)
                }
            }

    override suspend fun deleteReview(id: Long): PostgrestResult =
        supabase.from("review")
            .delete {
                filter {
                    eq("id", id)
                }
            }
}
