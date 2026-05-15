package it.unibo.almamensa

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import it.unibo.almamensa.data.repositories.AuthRepository
import it.unibo.almamensa.data.repositories.AuthRepositoryImpl
import it.unibo.almamensa.data.repositories.BookingRepository
import it.unibo.almamensa.data.repositories.BookingRepositoryImpl
import it.unibo.almamensa.data.repositories.CanteenRepository
import it.unibo.almamensa.data.repositories.MensaRepositoryImpl
import it.unibo.almamensa.data.repositories.ProfileRepositoryImpl
import it.unibo.almamensa.data.repositories.ReviewRepository
import it.unibo.almamensa.data.repositories.ReviewRepositoryImpl
import it.unibo.almamensa.data.repositories.SettingsRepository
import it.unibo.almamensa.data.repositories.SettingsRepositoryImpl
import it.unibo.almamensa.data.repositories.UserRepository
import it.unibo.almamensa.ui.screens.auth.AuthViewModel
import it.unibo.almamensa.ui.screens.canteen.CanteenViewModel
import it.unibo.almamensa.ui.screens.explore.ExploreViewModel
import it.unibo.almamensa.ui.screens.home.HomeViewModel
import it.unibo.almamensa.ui.screens.map.MapViewModel
import it.unibo.almamensa.ui.screens.profile.edit.EditProfileViewModel
import it.unibo.almamensa.ui.screens.profile.view.ProfileViewModel
import it.unibo.almamensa.ui.screens.review.ReviewViewModel
import it.unibo.almamensa.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {

    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth) {
                flowType = FlowType.PKCE

                scheme = "almamensa"
                host = "auth-callback"
            }
            install(Storage)
        }
    }

    single { get<Context>().dataStore }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<CanteenRepository> { MensaRepositoryImpl(get()) }
    single<UserRepository> { ProfileRepositoryImpl(get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get()) }
    single<BookingRepository> { BookingRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    viewModel { HomeViewModel(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { ExploreViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { (canteenId: Long) -> CanteenViewModel(canteenId, get(), get(), get() ) }
    viewModel { (canteenId: Long) -> ReviewViewModel(canteenId, get(), get()) }
    viewModel { MapViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
}
