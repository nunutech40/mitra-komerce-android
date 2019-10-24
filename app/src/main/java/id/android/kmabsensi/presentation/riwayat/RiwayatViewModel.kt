package id.android.kmabsensi.presentation.riwayat

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.data.remote.response.PresenceHistoryResponse
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class RiwayatViewModel(val presenceRepository: PresenceRepository,
                       val schedulerProvider: SchedulerProvider) : BaseViewModel() {


    val riwayatResponse = MutableLiveData<UiState<PresenceHistoryResponse>>()

    fun getPresenceHistory(userId: Int){
        riwayatResponse.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.presenceHistory(userId)
            .with(schedulerProvider)
            .subscribe({
                riwayatResponse.value = UiState.Success(it)
            },{
                riwayatResponse.value = UiState.Error(it)
            }))
    }


    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
    }
}