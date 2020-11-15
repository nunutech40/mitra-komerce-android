package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.AddSdmReportParams
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

    val crudResponse by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    fun getCsPerformances(){
        csPerformances.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.getCsPerformances()
            .with(schedulerProvider)
            .subscribe({
                csPerformances.value = UiState.Success(it)
            },{
                csPerformances.value = UiState.Error(it)
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