package id.android.kmabsensi.presentation.home

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.DashboardResponse
import id.android.kmabsensi.data.remote.response.PresenceCheckResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.repository.DashboardRepository
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class HomeViewModel(private val preferencesHelper: PreferencesHelper,
                    private val dashboardRepository: DashboardRepository,
                    private val presenceRepository: PresenceRepository,
                    val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val dashboardData = MutableLiveData<UiState<DashboardResponse>>()
    val presenceCheckResponse = MutableLiveData<UiState<PresenceCheckResponse>>()

    fun getDashboardInfo(userId: Int){
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

    fun getUserData() : User {
        return Gson().fromJson<User>(preferencesHelper.getString(PreferencesHelper.PROFILE_KEY), User::class.java)
    }

    fun clearPref(){
        preferencesHelper.clear()
    }

    override fun onError(error: Throwable) {

    }
}