package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.*
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.presentation.report.performa.advertiser.PerformaPeriode
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class SdmViewModel(
    private val sdmRepository: SdmRepository,
    private val schedulerProvider: SchedulerProvider,
    private val preferencesHelper: PreferencesHelper
) : BaseViewModel() {

    val sdm by lazy {
        MutableLiveData<UiState<UserResponse>>()
    }

    val csPerformances by lazy {
        MutableLiveData<UiState<ListCsPerformanceResponse>>()
    }

    val csReportSummary by lazy {
        MutableLiveData<UiState<ListCsPerformanceResponse>>()
    }

    val crudResponse by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    val advertiserReports by lazy {
        MutableLiveData<UiState<ListAdvertiserReportResponse>>()
    }

    val advertiserReportSummaryToday by lazy { MutableLiveData<ListAdvertiserReportResponse>() }
    val advertiserReportSummaryYesteday by lazy { MutableLiveData<ListAdvertiserReportResponse>() }
    val advertiserReportSummaryLast7Days by lazy { MutableLiveData<ListAdvertiserReportResponse>() }
    val advertiserReportSummaryThisMonth by lazy { MutableLiveData<ListAdvertiserReportResponse>() }
    val advertiserReportSummaryLastMonth by lazy { MutableLiveData<ListAdvertiserReportResponse>() }

    fun filterSdmReports(params: FilterSdmReportParams){
        csPerformances.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.filterSdmReports(params)
            .with(schedulerProvider)
            .subscribe({
                csPerformances.value = UiState.Success(it)
            },{
                csPerformances.value = UiState.Error(it)
            }))
    }

    fun filterCsReportSummary(params: FilterSdmReportParams){
        csReportSummary.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.filterCsReportSummary(params)
            .with(schedulerProvider)
            .subscribe({
                csReportSummary.value = UiState.Success(it)
            },{
                csReportSummary.value = UiState.Error(it)
            }))
    }

    fun addSdmReport(params: AddSdmReportParams){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.addSdmReport(params)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun editSdmReport(params: EditSdmReportParams){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.editSdmReport(params)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun deleteSdmReport(id: Int){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.deleteSdmReport(id)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun getUserData(): User {
        val userDara = preferencesHelper.getString(PreferencesHelper.PROFILE_KEY)
        return Gson().fromJson(
            userDara,
            User::class.java
        )
    }

    fun getAdvertiserReports(){
        advertiserReports.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.getAdvertiserReports()
            .with(schedulerProvider)
            .subscribe({
                advertiserReports.value = UiState.Success(it)
            },{
                advertiserReports.value = UiState.Error(it)
            }))
    }

    fun filterAdvertiserReports(params: FilterSdmReportParams){
        advertiserReports.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.filterAdvertiserReport(params)
            .with(schedulerProvider)
            .subscribe({
                advertiserReports.value = UiState.Success(it)
            },{
                advertiserReports.value = UiState.Error(it)
            }))
    }

    fun addAdvertiserReport(params: AddAdvertiserReportParams){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.addAdvertiserReport(params)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun editAdvertiserReport(params: EditAdvertiserReportParams){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.editAdvertiserReport(params)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun deleteAdvertiserReport(id: Int){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.deleteAdvertiserReport(id)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun filterAdvertiserReportSummary(params: FilterSdmReportParams, periode: PerformaPeriode){
//        advertiserReportSummary.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.filterAdvertiserReportSummary(params)
            .with(schedulerProvider)
            .subscribe({
                when(periode){
                    PerformaPeriode.TODAY -> advertiserReportSummaryToday.value = it
                    PerformaPeriode.YESTERDAY -> advertiserReportSummaryYesteday.value = it
                    PerformaPeriode.LAST7DAYS -> advertiserReportSummaryLast7Days.value = it
                    PerformaPeriode.THISMONTH -> advertiserReportSummaryThisMonth.value = it
                    PerformaPeriode.LASTMONTH -> advertiserReportSummaryLastMonth.value = it
                }
            },{
//                advertiserReportSummary.value = UiState.Error(it)
            }))
    }

    fun getSdm(
        userManagementId: Int = 0,
        officeId : Int = 0,
        positionId: Int = 0,
        noPartner : Int = 0
    ){
        compositeDisposable.add(sdmRepository.getSdm(
            userManagementId = userManagementId,
            officeId = officeId,
            positionId = positionId,
            noPartner = noPartner
        ).with(schedulerProvider)
            .subscribe({
                sdm.value = UiState.Success(it)
            },{
                sdm.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}