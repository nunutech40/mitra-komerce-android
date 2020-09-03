package id.android.kmabsensi.di

//import id.android.kmabsensi.data.remote.ApiClient
import androidx.room.Room
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.BuildConfig
import id.android.kmabsensi.data.db.AppDatabase
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.AuthInterceptor
import id.android.kmabsensi.data.remote.createWebService
import id.android.kmabsensi.data.remote.provideOkHttpClient
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.data.repository.*
import id.android.kmabsensi.presentation.checkin.CheckinViewModel
import id.android.kmabsensi.presentation.coworking.CoworkingSpaceViewModel
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.invoice.InvoiceViewModel
import id.android.kmabsensi.presentation.jabatan.JabatanViewModel
import id.android.kmabsensi.presentation.kantor.OfficeViewModel
import id.android.kmabsensi.presentation.kantor.cabang.TambahCabangViewModel
import id.android.kmabsensi.presentation.kantor.report.PresenceReportViewModel
import id.android.kmabsensi.presentation.kantor.report.filter.FilterReportViewModel
import id.android.kmabsensi.presentation.login.LoginViewModel
import id.android.kmabsensi.presentation.lupapassword.LupaPasswordViewModel
import id.android.kmabsensi.presentation.myevaluation.EvaluationViewModel
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.kategori.PartnerCategoryViewModel
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.presentation.riwayat.RiwayatViewModel
import id.android.kmabsensi.presentation.role.RoleViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordViewModel
import id.android.kmabsensi.presentation.splash.SplashViewModel
import id.android.kmabsensi.presentation.ubahprofile.UbahProfileViewModel
import id.android.kmabsensi.presentation.viewmodels.*
import id.android.kmabsensi.utils.rx.AppSchedulerProvider
import id.android.kmabsensi.utils.rx.SchedulerProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import kotlin.math.sin

val appModule = module {

    single { PreferencesHelper(androidContext()) }
    single { AuthInterceptor(get()) }
    single { provideOkHttpClient(get(), androidContext()) }
    single { createWebService<ApiService>(get(), BuildConfig.BASE_URL_ABSENSI) }
    single { AppSchedulerProvider() as SchedulerProvider }

    factory { GroupAdapter<GroupieViewHolder>() }

    factory { Gson() }

}

val dataModule = module {
    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "mitrakm-db").build()
    }

    single { get<AppDatabase>().provinceDao() }
    single { get<AppDatabase>().cityDao() }
}

val repositoryModule = module {
    single { AuthRepository(get()) }
    single { UserRepository(get()) }
    single { OfficeRepository(get()) }
    single { SdmRepository(get()) }
    single { DashboardRepository(get()) }
    single { PresenceRepository(get()) }
    single { PermissionRepository(get()) }
    single { JabatanRepository(get()) }
    single { JadwalShalatRepository(get()) }
    single { CoworkingSpaceRepository(get()) }
    single { PartnerCategoryRepository(get()) }
    single { AreaRepository(get(), get(), get()) }
    single { PartnerRepository(get()) }
    single { InvoiceRepository(get()) }
    single { EvaluationRepository(get()) }
    single { WorkConfigRepository(get()) }
    single { RoleRepository(get()) }
    single { DeviceRepository(get()) }
    single { AttachmentRepository(get()) }
    single { AdministrationRepository(get()) }
    single { ProductKnowledgeRepository(get()) }
    single { KmPoinRepository(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModel { TambahCabangViewModel(get(), get(), get()) }
    viewModel { OfficeViewModel(get(), get()) }
    viewModel { KelolaDataSdmViewModel(get(), get(), get(), get(), get()) }
    viewModel { EditPasswordViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(),  get(), get(), get(), get(), get(), get()) }
    viewModel { CheckinViewModel(get(), get(), get()) }
    viewModel { RiwayatViewModel(get(), get()) }
    viewModel { PermissionViewModel(get(), get()) }
    viewModel { LupaPasswordViewModel(get(), get()) }
    viewModel { FilterReportViewModel(get(), get(), get()) }
    viewModel { PresenceReportViewModel(get(), get(), get(), get()) }
    viewModel { JabatanViewModel(get(), get()) }
    viewModel { UbahProfileViewModel(get(), get(), get()) }
    viewModel { CoworkingSpaceViewModel(get(), get()) }
    viewModel { SplashViewModel(get(), get(), get()) }
    viewModel { PartnerCategoryViewModel(get(), get()) }
    viewModel { PartnerViewModel(get(), get(), get(), get()) }
    viewModel { InvoiceViewModel(get(), get(), get()) }
    viewModel { EvaluationViewModel(get(), get(), get()) }
    viewModel { WorkConfigViewModel(get(), get()) }
    viewModel { RoleViewModel(get(), get()) }
    viewModel { DeviceViewModel(get(), get()) }
    viewModel { AttachmentViewModel(get(), get()) }
    viewModel { AdministrationViewModel(get(), get()) }
    viewModel { ProductKnowledgeViewModel(get(), get()) }
}

val myAppModule = listOf(appModule, dataModule, viewModelModule, repositoryModule)