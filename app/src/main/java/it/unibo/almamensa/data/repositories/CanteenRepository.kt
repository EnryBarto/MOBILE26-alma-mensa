package it.unibo.almamensa.data.repositories

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.result.PostgrestResult
import it.unibo.almamensa.data.model.Canteen

interface CanteenRepository {
    suspend fun getAllCanteen() : List<Canteen>
    suspend fun getCanteenById(id: Long): Canteen
    suspend fun insertCanteen(canteen: Canteen) : PostgrestResult
    suspend fun updateCanteen(id: Long, canteen: Canteen) : PostgrestResult
    suspend fun deleteCanteen(id: Long) : PostgrestResult
}

class MensaRepositoryImpl(private val supabase: SupabaseClient) : CanteenRepository {
    override suspend fun getAllCanteen() =
        supabase.from("canteen").select().decodeList<Canteen>()

    override suspend fun getCanteenById(id: Long) =
        supabase.from("canteen")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Canteen>()

    override suspend fun insertCanteen(canteen: Canteen) =
        supabase.from("canteen").insert(canteen)

    override suspend fun updateCanteen(id: Long, canteen: Canteen) =
        supabase.from("canteen")
            .update(canteen) {
                filter {
                    eq("id", id)
                }
            }

    override suspend fun deleteCanteen(id: Long) =
        supabase.from("canteen")
            .delete {
                filter {
                    eq("id", id)
                }
            }
}