package id.android.kmabsensi.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.hadilq.liveevent.LiveEvent
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.CoworkingSpaceRepository
import id.android.kmabsensi.data.repository.DashboardRepository
import id.android.kmabsensi.data.repository.JadwalShalatRepository
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class HomeViewModel(
    private val preferencesHelper: PreferencesHelper,
    private val dashboardRepository: DashboardRepository,
    private val presenceRepository: PresenceRepository,
    private val jadwalShalatRepository: JadwalShalatRepository,
    private val coworkingSpaceRepository: CoworkingSpaceRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val jadwalShalatData = MutableLiveData<UiState<JadwalShalatResponse>>()
    val dashboardData = MutableLiveData<UiState<DashboardResponse>>()
    val presenceCheckResponse = LiveEvent<UiState<PresenceCheckResponse>>()
    val checkoutResponse = LiveEvent<UiState<CheckinResponse>>()

    val presenceCheckState: LiveData<UiState<PresenceCheckResponse>> = presenceCheckResponse
    val checkoutState: LiveData<UiState<CheckinResponse>> = checkoutResponse

    val checkInCoworkingSpace = LiveEvent<UiState<BaseResponse>>()

    val coworkUserData = LiveEvent<UiState<UserCoworkDataResponse>>()

    fun getDashboardInfo(userId: Int) {
        dashboardData.value = UiState.Loading()
        compositeDisposable.add(dashboardRepository.getDashboardInfo(userId)
            .with(schedulerProvider)
            .subscribe({
                dashboardData.value = UiState.Success(it)
            }, {
                dashboardData.value = UiState.Error(it)
            })
        )
    }

    fun presenceCheck(userId: Int) {
        presenceCheckResponse.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.presenceCheck(userId)
            .with(schedulerProvider)
            .subscribe({
                presenceCheckResponse.value = UiState.Success(it)
            }, {
                presenceCheckResponse.value = UiState.Error(it)
            })
        )
    }

    fun getJadwalShalat() {
        jadwalShalatData.value = UiState.Loading()
        compositeDisposable.add(jadwalShalatRepository.getJadwalShalat()
            .with(schedulerProvider)
            .subscribe({
                jadwalShalatData.value = UiState.Success(it)
            }, {
                jadwalShalatData.value = UiState.Error(it)
            })
        )
    }

    fun getCoworkUserData(userId: Int){
        coworkUserData.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.getCoworkUserData(userId)
            .with(schedulerProvider)
            .subscribe({
                coworkUserData.value = UiState.Success(it)
            },{
                coworkUserData.value = UiState.Error(it)
            }))
    }

    fun checkInCoworkingSpace(coworkId: Int){
        checkInCoworkingSpace.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.checkInCoworkingSpace(coworkId)
            .with(schedulerProvider)
            .subscribe({
                checkInCoworkingSpace.value = UiState.Success(it)
            },{
                checkInCoworkingSpace.value = UiState.Error(it)
            }))
    }

    fun checkOutCoworkingSpace(coworkPresenceId: Int){
        checkInCoworkingSpace.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.checkOutCoworkingSpace(coworkPresenceId)
            .with(schedulerProvider)
            .subscribe({
                checkInCoworkingSpace.value = UiState.Success(it)
            },{
                checkInCoworkingSpace.value = UiState.Error(it)
            }))
    }

    fun getUserData(): User {
        return Gson().fromJson<User>(
            preferencesHelper.getString(PreferencesHelper.PROFILE_KEY),
            User::class.java
        )
    }

    fun clearPref() {
        preferencesHelper.clear()
    }

    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
    }
}