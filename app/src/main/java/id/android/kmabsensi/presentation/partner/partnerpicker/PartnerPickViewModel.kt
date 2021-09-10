package id.android.kmabsensi.presentation.partner.partnerpicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.ListPartnerResponse
import id.android.kmabsensi.data.repository.PartnerRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class PartnerPickViewModel (
    val partnerRepository: PartnerRepository,
    val schedulerProvider: SchedulerProvider
        ): BaseViewModel(){

    val getPartnerState : MutableLiveData<UiState<ListPartnerResponse>> = MutableLiveData()

    fun getDataPartners(){
        getPartnerState.value = UiState.Loading()
        compositeDisposable.add(
            partnerRepository.getListPartner()
                .with(schedulerProvider)
                .subscribe({
                    getPartnerState.value = UiState.Success(it)
                },{
                    getPartnerState.value = UiState.Error(it)
                })
        )
    }




    override fun onError(error: Throwable) {
        Timber.e((error))
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}