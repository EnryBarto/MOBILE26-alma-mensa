package it.unibo.almamensa

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import it.unibo.almamensa.data.repositories.AuthRepository
import it.unibo.almamensa.data.repositories.AuthRepositoryImpl
import it.unibo.almamensa.data.repositories.CanteenRepository
import it.unibo.almamensa.data.repositories.MensaRepositoryImpl
import it.unibo.almamensa.data.repositories.ProfileRepositoryImpl
import it.unibo.almamensa.data.repositories.UserRepository
import it.unibo.almamensa.ui.screens.auth.AuthViewModel
import it.unibo.almamensa.ui.screens.canteenDisplay.CanteenViewModel
import it.unibo.almamensa.ui.screens.home.HomeViewModel
import it.unibo.almamensa.ui.screens.profile.ProfileViewModel
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
            install(Storage)
        }
    }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<CanteenRepository> { MensaRepositoryImpl(get()) }
    single<UserRepository> { ProfileRepositoryImpl(get()) }

    viewModel { HomeViewModel() }
    viewModel { AuthViewModel(get()) }
    viewModel { CanteenViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
}
