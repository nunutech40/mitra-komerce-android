package id.android.kmabsensi.presentation.point.tambahdaftarbelanja

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPartnerResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.PartnerRepository
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class AddShoppingViewModel(
        val partnerRepository: PartnerRepository,
        val sdmRepository: SdmRepository,
        val schedulerProvider: SchedulerProvider
) : BaseViewModel(){

    val crudResponse by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    val sdm by lazy {
        MutableLiveData<UiState<UserResponse>>()
    }

    fun getDataPartners(): LiveData<UiState<ListPartnerResponse>> =
            partnerRepository.getPartnersCoba(compositeDisposable)

    fun getAllSdm(){
        sdm.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.getSdm()
                .with(schedulerProvider)
                .subscribe({
                    sdm.value = UiState.Success(it)
                },{
                    sdm.value = UiState.Error(it)
                }))
    }

    override fun onError(error: Throwable) {
        crudResponse.value = UiState.Error(error)
        Timber.e((error))
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}