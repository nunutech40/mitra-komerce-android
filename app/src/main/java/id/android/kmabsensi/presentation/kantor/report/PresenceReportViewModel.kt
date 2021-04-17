package id.android.kmabsensi.presentation.kantor.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import id.android.kmabsensi.data.remote.body.ListAlphaParams
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.OfficeRepository
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class PresenceReportViewModel(val presenceRepository: PresenceRepository,
                              val userRepository: UserRepository,
                              val officeRepository: OfficeRepository,
                              val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val officeData = MutableLiveData<UiState<OfficeResponse>>()
    val presenceReportData = MutableLiveData<UiState<PresenceReportResponse>>()
    val userManagementData = MutableLiveData<UiState<UserResponse>>()
    val alphaAttendances = MutableLiveData<UiState<ListAlphaResponse>>()

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

    fun getPresenceReportFiltered(
        roleId: Int = 0,
        userManagementId: Int = 0,
        officeId: Int = 0,
        startDate: String,
        endDate: String){
        presenceReportData.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.getPresenceReportFiltered(
            roleId,
            userManagementId,
            officeId,
            startDate,
            endDate
        )
            .with(schedulerProvider)
            .subscribe({
                presenceReportData.value = UiState.Success(it)
            }, this::onError))
    }

    fun getPresenceReportFilteredPaging(
            roleId: Int = 0,
            userManagementId: Int = 0,
            officeId: Int = 0,
            startDate: String,
            endDate: String) : LiveData<PagedList<Presence>> = presenceRepository.getPresenceReportFilteredPaging(compositeDisposable,
                roleId, userManagementId,
                officeId, startDate,
                endDate)

    fun getReportFiltered() : LiveData<Report> = presenceRepository.getReportFilter()

    fun getStateFiltered() : LiveData<State> = presenceRepository.getStateFilter()


    fun getDataOffice(){
        userManagementData.value = UiState.Loading()
        compositeDisposable.add(officeRepository.getOffices()
            .with(schedulerProvider)
            .subscribe({
                officeData.value = UiState.Success(it)
            },{
                officeData.value = UiState.Error(it)
            })
        )
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

    fun getListAlpha(params: ListAlphaParams) {
        alphaAttendances.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.getListAlpha(params)
            .with(schedulerProvider)
            .subscribe({
                alphaAttendances.value = UiState.Success(it)
            },{
                alphaAttendances.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {
        presenceReportData.value = UiState.Error(error)
    }
}