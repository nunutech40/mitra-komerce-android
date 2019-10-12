package id.android.kmabsensi.presentation.kantor.report

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.PresenceReportResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class PresenceReportViewModel(val presenceRepository: PresenceRepository,
                              val userRepository: UserRepository,
                              val schedulerProvider: SchedulerProvider): BaseViewModel() {

    val presenceReportData = MutableLiveData<UiState<PresenceReportResponse>>()
    val userManagementData = MutableLiveData<UiState<UserResponse>>()

    fun getPresenceReport(
        roleId: Int = 0,
        userManagementId: Int = 0,
        officeId: Int = 0,
        date: String){
        presenceReportData.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.presenceReport(
            roleId,
            userManagementId,
            officeId,
            date
        )
            .with(schedulerProvider)
            .subscribe({
                presenceReportData.value = UiState.Success(it)
            }, this::onError))
    }

    fun getUserManagement(roleId: Int = 2){
        userManagementData.value = UiState.Loading()
        compositeDisposable.add(userRepository.getUserByRole(roleId)
            .with(schedulerProvider)
            .subscribe({
                userManagementData.value = UiState.Success(it)
            },{
                userManagementData.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {
        presenceReportData.value = UiState.Error(error)
    }
}