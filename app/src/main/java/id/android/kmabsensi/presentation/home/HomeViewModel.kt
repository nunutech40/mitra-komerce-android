package id.android.kmabsensi.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.hadilq.liveevent.LiveEvent
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.*
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.ROLE_SDM
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class HomeViewModel(
    private val preferencesHelper: PreferencesHelper,
    private val dashboardRepository: DashboardRepository,
    private val presenceRepository: PresenceRepository,
    private val jadwalShalatRepository: JadwalShalatRepository,
    private val coworkingSpaceRepository: CoworkingSpaceRepository,
    private val kmPoinRepository: KmPoinRepository,
    val authRepository: AuthRepository,
    val userRepository: UserRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {


    val jadwalShalatData = MutableLiveData<UiState<JadwalShalatResponse>>()
    val dashboardData = MutableLiveData<UiState<DashboardResponse>>()
    val userdData = MutableLiveData<UiState<UserResponse>>()
    val presenceCheckResponse = LiveEvent<UiState<PresenceCheckResponse>>()
    val checkoutResponse = LiveEvent<UiState<CheckinResponse>>()

    val presenceCheckState: LiveData<UiState<PresenceCheckResponse>> = presenceCheckResponse
    val checkoutState: LiveData<UiState<CheckinResponse>> = checkoutResponse

    val checkInCoworkingSpace = LiveEvent<UiState<BaseResponse>>()

    val coworkUserData = LiveEvent<UiState<UserCoworkDataResponse>>()

    val logoutState = MutableLiveData<UiState<BaseResponse>>()

    val redeemPoin = LiveEvent<UiState<BaseResponse>>()


    fun getDashboardInfo(userId: Int) {
        try {
            dashboardData.value = UiState.Loading()
            compositeDisposable.add(
                dashboardRepository.getDashboardInfo(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        dashboardData.value = UiState.Success(it)
                    }, {
                        dashboardData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            dashboardData.value = UiState.Error(e)
        }
    }

    fun presenceCheck(userId: Int) {
        try {
            presenceCheckResponse.value = UiState.Loading()
            compositeDisposable.add(
                presenceRepository.presenceCheck(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        presenceCheckResponse.value = UiState.Success(it)
                    }, {
                        presenceCheckResponse.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            presenceCheckResponse.value = UiState.Error(e)
        }
    }

    fun getJadwalShalat() {
        try {
            jadwalShalatData.value = UiState.Loading()
            compositeDisposable.add(
                jadwalShalatRepository.getJadwalShalat()
                    .with(schedulerProvider)
                    .subscribe({
                        jadwalShalatData.value = UiState.Success(it)
                    }, {
                        jadwalShalatData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            jadwalShalatData.value = UiState.Error(e)
        }
    }

    fun getCoworkUserData(userId: Int) {
        try {
            coworkUserData.value = UiState.Loading()
            compositeDisposable.add(
                coworkingSpaceRepository.getCoworkUserData(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        coworkUserData.value = UiState.Success(it)
                    }, {
                        coworkUserData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            coworkUserData.value = UiState.Error(e)
        }
    }

    fun checkInCoworkingSpace(coworkId: Int) {
        try {
            checkInCoworkingSpace.value = UiState.Loading()
            compositeDisposable.add(
                coworkingSpaceRepository.checkInCoworkingSpace(coworkId)
                    .with(schedulerProvider)
                    .subscribe({
                        checkInCoworkingSpace.value = UiState.Success(it)
                    }, {
                        checkInCoworkingSpace.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            checkInCoworkingSpace.value = UiState.Error(e)
        }
    }

    fun checkOutCoworkingSpace(coworkPresenceId: Int) {
        try {
            checkInCoworkingSpace.value = UiState.Loading()
            compositeDisposable.add(
                coworkingSpaceRepository.checkOutCoworkingSpace(coworkPresenceId)
                    .with(schedulerProvider)
                    .subscribe({
                        checkInCoworkingSpace.value = UiState.Success(it)
                    }, {
                        checkInCoworkingSpace.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            checkInCoworkingSpace.value = UiState.Error(e)
        }
    }

    fun logout() {
        try {
            logoutState.value = UiState.Loading()
            compositeDisposable.add(
                authRepository.logout(preferencesHelper.getString(PreferencesHelper.FCM_TOKEN))
                    .with(schedulerProvider)
                    .subscribe({
                        logoutState.value = UiState.Success(it)
                    }, {
                        logoutState.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            logoutState.value = UiState.Error(e)
        }
    }

    fun getProfileUserData(userId: Int) {
        try {
            userdData.value = UiState.Loading()
            compositeDisposable.add(
                userRepository.getProfileUser(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        preferencesHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data[0]))
                        userdData.value = UiState.Success(it)
                    }, {
                        userdData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            userdData.value = UiState.Error(e)
        }
    }

    fun getUserData(): User {
        val userDara = preferencesHelper.getString(PreferencesHelper.PROFILE_KEY)
        return Gson().fromJson(
            userDara,
            User::class.java
        )
    }

    fun redeemPoin(userId: Int, totalRequestRedeemPoin: Int) {
        try {
            redeemPoin.value = UiState.Loading()
            compositeDisposable.add(
                kmPoinRepository.redeemPoin(userId, totalRequestRedeemPoin)
                    .with(schedulerProvider)
                    .subscribe({
                        redeemPoin.value = UiState.Success(it)
                    }, {
                        redeemPoin.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            redeemPoin.value = UiState.Error(e)
        }
    }

    fun clearPref() {
        preferencesHelper.clear()
    }

    fun isNormal(user: User): Boolean{
        return user.role_name == ROLE_SDM || user.position_name.lowercase().contains("team lead") || user.position_name.lowercase().contains("partner acquisition")
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}