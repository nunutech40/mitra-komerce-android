package id.android.kmabsensi.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import id.android.kmabsensi.data.db.AppDatabase
import id.android.kmabsensi.data.remote.createWebService
import id.android.kmabsensi.data.remote.provideOkHttpClient
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.repository.TeamRepository
import id.android.kmabsensi.data.repository.TeamRepositoryImpl
import id.android.kmabsensi.data.remote.service.TeamService
import id.android.kmabsensi.presentation.main.MainViewModel
import id.android.momakan.utils.scheduler.AppSchedulerProvider
import id.android.momakan.utils.scheduler.SchedulerProvider

val appModule = module {
    single { provideOkHttpClient() }
    single { createWebService<TeamService>(get()) }

    single { PreferencesHelper(androidContext()) }

    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "app-database").build()
    }

    single { AppSchedulerProvider() as SchedulerProvider }

}

val dataModule = module {
    single { get<AppDatabase>().teamDao() }
    single { TeamRepositoryImpl(get(), get()) as TeamRepository }

    viewModel { MainViewModel(get(), get()) }
}

val myAppModule = listOf(appModule, dataModule)