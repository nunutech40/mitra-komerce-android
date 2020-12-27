package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.*
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListAdvertiserReportResponse
import id.android.kmabsensi.data.remote.response.ListCsPerformanceResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class SdmViewModel(
    private val sdmRepository: SdmRepository,
    private val schedulerProvider: SchedulerProvider,
    private val preferencesHelper: PreferencesHelper
) : BaseViewModel() {

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

    override fun onError(error: Throwable) {

    }
}