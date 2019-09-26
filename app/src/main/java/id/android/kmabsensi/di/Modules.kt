package id.android.kmabsensi.di

import androidx.room.Room
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import id.android.kmabsensi.data.db.AppDatabase
import id.android.kmabsensi.data.remote.createWebService
import id.android.kmabsensi.data.remote.provideOkHttpClient
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.data.repository.TeamRepository
import id.android.kmabsensi.data.repository.TeamRepositoryImpl
import id.android.kmabsensi.data.repository.AuthRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.data.remote.service.TeamService
import id.android.kmabsensi.presentation.main.MainViewModel
import id.android.momakan.utils.scheduler.AppSchedulerProvider
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.kmabsensi.BuildConfig
import id.android.kmabsensi.presentation.login.LoginViewModel
import id.android.kmabsensi.presentation.kantor.cabang.TambahCabangViewModel
import id.android.kmabsensi.data.repository.OfficeRepository
import id.android.kmabsensi.presentation.kantor.OfficeViewModel

val appModule = module {

    single { PreferencesHelper(androidContext()) }

    single { provideOkHttpClient(get()) }
    single { createWebService<TeamService>(get(), BuildConfig.BASE_URL) }
    single { createWebService<ApiService>(get(), BuildConfig.BASE_URL_ABSENSI) }



    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "app-database").build()
    }

    single { AppSchedulerProvider() as SchedulerProvider }

    factory { GroupAdapter<ViewHolder>() }

}

val dataModule = module {
    single { get<AppDatabase>().teamDao() }
    single { TeamRepositoryImpl(get(), get()) as TeamRepository }

    single { AuthRepository(get()) }
    single { UserRepository(get()) }
    single { OfficeRepository(get()) }

    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { TambahCabangViewModel(get(), get(), get()) }
    viewModel { OfficeViewModel(get(), get()) }
}

val myAppModule = listOf(appModule, dataModule)