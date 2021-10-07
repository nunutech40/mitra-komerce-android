package id.android.kmabsensi.presentation.komship.selectdestination

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.komship.KomDestinationResponse
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class DestinationViewModel(
    private val repository: KomShipRepository,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel(){
    val destinationState : MutableLiveData<UiState<KomDestinationResponse>> = MutableLiveData()

    fun getDestination(page: Int? = null, search: String?= "Purbalingga"){
        destinationState.value = UiState.Loading()
        compositeDisposable.add(
            repository.getDestination(page, search!!)
                .with(schedulerProvider)
                .subscribe({
                    destinationState.value = UiState.Success(it)
                },{
                    destinationState.value = UiState.Error(it)
                })
        )
    }
    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}