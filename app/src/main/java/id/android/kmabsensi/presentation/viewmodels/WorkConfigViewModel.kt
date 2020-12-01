package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.android.kmabsensi.data.remote.body.WorkConfigParams
import id.android.kmabsensi.data.remote.response.WorkConfigResponse
import id.android.kmabsensi.data.repository.WorkConfigRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class WorkConfigViewModel(
    val repository: WorkConfigRepository,
    val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val workConfigUpdateResult by lazy {
        MutableLiveData<UiState<WorkConfigResponse>>()
    }

    val workConfigResult by lazy {
        MutableLiveData<UiState<WorkConfigResponse>>()
    }

    fun updateWorkConfig(workConfigParams: WorkConfigParams){
        workConfigUpdateResult.value = UiState.Loading()
        compositeDisposable.add(repository.updateWorkConfig(workConfigParams)
            .with(schedulerProvider)
            .subscribe({
                workConfigUpdateResult.value = UiState.Success(it)
            },{
                workConfigUpdateResult.value = UiState.Error(it)
            }))
    }


    fun getWorkConfig(){
        workConfigResult.value = UiState.Loading()
        compositeDisposable.add(repository.getWorkConfig()
            .with(schedulerProvider)
            .subscribe({
                workConfigResult.value = UiState.Success(it)
            },{
                workConfigResult.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}