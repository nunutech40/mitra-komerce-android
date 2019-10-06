package id.android.kmabsensi.presentation.kantor.report

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.PresenceReportResponse
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class PresenceReportViewModel(val presenceRepository: PresenceRepository,
                              val schedulerProvider: SchedulerProvider): BaseViewModel() {

    val presenceReportData = MutableLiveData<UiState<PresenceReportResponse>>()

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

    override fun onError(error: Throwable) {
        presenceReportData.value = UiState.Error(error)
    }
}