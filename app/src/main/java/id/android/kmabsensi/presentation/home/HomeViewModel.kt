package id.android.kmabsensi.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.github.ajalt.timberkt.Timber.d
import com.google.gson.Gson
import com.hadilq.liveevent.LiveEvent
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.DashboardRepository
import id.android.kmabsensi.data.repository.JadwalShalatRepository
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class HomeViewModel(private val preferencesHelper: PreferencesHelper,
                    private val dashboardRepository: DashboardRepository,
                    private val presenceRepository: PresenceRepository,
                    private val jadwalShalatRepository: JadwalShalatRepository,
                    val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val jadwalShalatData = MutableLiveData<UiState<JadwalShalatResponse>>()

    val dashboardData = MutableLiveData<UiState<DashboardResponse>>()
    val presenceCheckResponse = LiveEvent<UiState<PresenceCheckResponse>>()
    val checkoutResponse = LiveEvent<UiState<CheckinResponse>>()

    val presenceCheckState: LiveData<UiState<PresenceCheckResponse>> = presenceCheckResponse
    val checkoutState: LiveData<UiState<CheckinResponse>> = checkoutResponse

    fun getDashboardInfo(userId: Int){
        dashboardData.value = UiState.Loading()
        compositeDisposable.add(dashboardRepository.getDashboardInfo(userId)
            .with(schedulerProvider)
            .subscribe({
                dashboardData.value = UiState.Success(it)
            },{
                dashboardData.value = UiState.Error(it)
            }))
    }

    fun presenceCheck(userId: Int){
        presenceCheckResponse.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.presenceCheck(userId)
            .with(schedulerProvider)
            .subscribe({
                presenceCheckResponse.value = UiState.Success(it)
            },{
                presenceCheckResponse.value = UiState.Error(it)
            }))
    }

    fun getJadwalShalat(){
        if (jadwalShalatData.value !is UiState.Success){
            jadwalShalatData.value = UiState.Loading()
            compositeDisposable.add(jadwalShalatRepository.getJadwalShalat()
                .with(schedulerProvider)
                .subscribe({
                    jadwalShalatData.value = UiState.Success(it)
                },{
                    jadwalShalatData.value = UiState.Error(it)
                }))
        }


    }


    fun getUserData() : User {
        return Gson().fromJson<User>(preferencesHelper.getString(PreferencesHelper.PROFILE_KEY), User::class.java)
    }

    fun clearPref(){
        preferencesHelper.clear()
    }

    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
    }
}