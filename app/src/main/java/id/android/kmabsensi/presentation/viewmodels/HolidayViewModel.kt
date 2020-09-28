package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.HolidayResponse
import id.android.kmabsensi.data.repository.HolidayRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class HolidayViewModel(
    private val holidayRepository: HolidayRepository,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val holidays by lazy {
        MutableLiveData<UiState<HolidayResponse>>()
    }

    fun getHoliday(){
        holidays.value = UiState.Loading()
        compositeDisposable.add(holidayRepository.getHoliday()
            .with(schedulerProvider)
            .subscribe({
                holidays.value = UiState.Success(it)
            },{
                holidays.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}