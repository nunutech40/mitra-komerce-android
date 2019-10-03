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
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordViewModel
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.data.repository.DashboardRepository
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.presentation.checkin.CheckinViewModel
import id.android.kmabsensi.presentation.riwayat.RiwayatViewModel
import id.android.kmabsensi.data.repository.PermissionRepository
import id.android.kmabsensi.presentation.tidakhadir.PermissionViewModel

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
    single { SdmRepository(get()) }
    single { DashboardRepository(get()) }
    single { PresenceRepository(get()) }
    single { PermissionRepository(get()) }

}

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { TambahCabangViewModel(get(), get(), get()) }
    viewModel { OfficeViewModel(get(), get()) }
    viewModel { KelolaDataSdmViewModel(get(), get(), get(), get()) }
    viewModel { EditPasswordViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { CheckinViewModel(get(), get()) }
    viewModel { RiwayatViewModel(get(), get()) }
    viewModel { PermissionViewModel(get(), get()) }
}

val myAppModule = listOf(appModule, dataModule, viewModelModule)