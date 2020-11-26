package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.AddSdmReportParams
import id.android.kmabsensi.data.remote.body.EditSdmReportParams
import id.android.kmabsensi.data.remote.body.FilterSdmReportParams
import id.android.kmabsensi.data.remote.response.BaseResponse
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

//    fun getSdmReports(){
//        csPerformances.value = UiState.Loading()
//        compositeDisposable.add(sdmRepository.getSdmReports()
//            .with(schedulerProvider)
//            .subscribe({
//                csPerformances.value = UiState.Success(it)
//            },{
//                csPerformances.value = UiState.Error(it)
//            }))
//    }

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

    override fun onError(error: Throwable) {

    }
}