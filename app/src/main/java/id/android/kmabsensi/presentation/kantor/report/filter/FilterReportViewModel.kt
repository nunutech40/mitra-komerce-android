package id.android.kmabsensi.presentation.kantor.report.filter

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.OfficeResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.OfficeRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class FilterReportViewModel(val officeRepository: OfficeRepository,
                            val userRepository: UserRepository,
                            val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val officeData = MutableLiveData<UiState<OfficeResponse>>()
    val userManagementData = MutableLiveData<UiState<UserResponse>>()

    fun getDataOffice(){
        compositeDisposable.add(officeRepository.getOffices()
            .with(schedulerProvider)
            .subscribe({
                officeData.value = UiState.Success(it)
            },{
                officeData.value = UiState.Error(it)
            })
        )
    }

    fun getUserManagement(roleId: Int){
        compositeDisposable.add(userRepository.getUserByRole(roleId)
            .with(schedulerProvider)
            .subscribe({
                userManagementData.value = UiState.Success(it)
            },{
                userManagementData.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}