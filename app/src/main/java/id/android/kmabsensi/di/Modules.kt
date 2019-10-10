package id.android.kmabsensi.di

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.BuildConfig
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.data.repository.*
import id.android.kmabsensi.presentation.checkin.CheckinViewModel
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kantor.OfficeViewModel
import id.android.kmabsensi.presentation.kantor.cabang.TambahCabangViewModel
import id.android.kmabsensi.presentation.login.LoginViewModel
import id.android.kmabsensi.presentation.lupapassword.LupaPasswordViewModel
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.presentation.riwayat.RiwayatViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordViewModel
import id.android.momakan.utils.scheduler.AppSchedulerProvider
import id.android.momakan.utils.scheduler.SchedulerProvider
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import id.android.kmabsensi.presentation.kantor.report.filter.FilterReportViewModel
import id.android.kmabsensi.presentation.kantor.report.PresenceReportViewModel
//import id.android.kmabsensi.data.remote.ApiClient
import id.android.kmabsensi.data.remote.AuthInterceptor
import id.android.kmabsensi.data.remote.createWebService
import id.android.kmabsensi.data.remote.provideOkHttpClient

val appModule = module {

    single { PreferencesHelper(androidContext()) }
    single { AuthInterceptor(get()) }
    single { provideOkHttpClient(get(), androidContext()) }
    single { createWebService<ApiService>(get(), BuildConfig.BASE_URL_ABSENSI) }

    single { AppSchedulerProvider() as SchedulerProvider }

    factory { GroupAdapter<ViewHolder>() }

}

val dataModule = module {
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
    viewModel { TambahCabangViewModel(get(), get(), get()) }
    viewModel { OfficeViewModel(get(), get()) }
    viewModel { KelolaDataSdmViewModel(get(), get(), get(), get()) }
    viewModel { EditPasswordViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { CheckinViewModel(get(), get()) }
    viewModel { RiwayatViewModel(get(), get()) }
    viewModel { PermissionViewModel(get(), get()) }
    viewModel { LupaPasswordViewModel(get(), get()) }
    viewModel { FilterReportViewModel(get(), get(), get()) }
    viewModel { PresenceReportViewModel(get(), get()) }
}

val myAppModule = listOf(appModule, dataModule, viewModelModule)