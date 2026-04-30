package it.unibo.almamensa

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import it.unibo.almamensa.data.repositories.AuthRepository
import it.unibo.almamensa.data.repositories.AuthRepositoryImpl
import it.unibo.almamensa.ui.screens.auth.AuthViewModel
import it.unibo.almamensa.ui.screens.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)
        }
    }

    single<AuthRepository> { AuthRepositoryImpl(get()) }

    viewModel { HomeViewModel() }

    viewModel { AuthViewModel(get()) }

}
