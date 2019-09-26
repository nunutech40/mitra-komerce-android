package id.android.kmabsensi.presentation.kantor

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.OfficeResponse
import id.android.kmabsensi.data.repository.OfficeRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class OfficeViewModel(val officeRepository: OfficeRepository,
                      val schedulerProvider: SchedulerProvider) : BaseViewModel()  {

    val officeState = MutableLiveData<UiState<OfficeResponse>>()

    fun getOffices() {
        officeState.value = UiState.Loading()
        compositeDisposable.add(officeRepository.getOffices()
            .with(schedulerProvider)
            .subscribe({
                officeState.value = UiState.Success(it)
            },{
                officeState.value = UiState.Error(it)
            }))
    }


    override fun onError(error: Throwable) {

    }
}