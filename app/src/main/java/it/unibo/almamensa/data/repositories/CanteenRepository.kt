package it.unibo.almamensa.data.repositories

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.result.PostgrestResult
import it.unibo.almamensa.data.model.Canteen

interface CanteenRepository {
    suspend fun getAllCanteen() : List<Canteen>
    suspend fun getCanteenById(id: Int): Canteen
    suspend fun insertCanteen(canteen: Canteen) : PostgrestResult
    suspend fun updateCanteen(id: Int, canteen: Canteen) : PostgrestResult
    suspend fun deleteCanteen(id: Int) : PostgrestResult
}

class MensaRepositoryImpl(private val supabase: SupabaseClient) : CanteenRepository {
    override suspend fun getAllCanteen() =
        supabase.postgrest.from("canteen").select().decodeList<Canteen>()

    override suspend fun getCanteenById(id: Int) =
        supabase.postgrest.from("canteen")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Canteen>()

    override suspend fun insertCanteen(canteen: Canteen) =
        supabase.postgrest.from("canteen").insert(canteen)

    override suspend fun updateCanteen(id: Int, canteen: Canteen) =
        supabase.postgrest.from("canteen")
            .update(canteen) {
                filter {
                    eq("id", id)
                }
            }

    override suspend fun deleteCanteen(id: Int) =
        supabase.postgrest.from("canteen")
            .delete {
                filter {
                    eq("id", id)
                }
            }
}