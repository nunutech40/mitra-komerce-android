package id.android.kmabsensi.presentation.komship.delivery

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.komship.AddOrderParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.KomCalculateResponse
import id.android.kmabsensi.data.remote.response.komship.KomDestinationResponse
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.di.repositoryModule
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import id.android.kmabsensi.utils.visible

class DeliveryViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val destinationState : MutableLiveData<UiState<KomDestinationResponse>> = MutableLiveData()

    val calculateState : MutableLiveData<UiState<KomCalculateResponse>> = MutableLiveData()

    val addOrderState : MutableLiveData<UiState<BaseResponse>> = MutableLiveData()

    fun getDestination(page: Int? = null, search: String?= "Purbalingga"){
        destinationState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getDestination(page, search!!)
                .with(schedulerProvider)
                .subscribe({
                    destinationState.value = UiState.Success(it)
                },{
                    destinationState.value = UiState.Error(it)
                })
        )
    }

    fun getCalculate(
        discount: Int? = null,
        shipping: String,
        tariffCode: String,
        paymentMethod: String
    ){
        calculateState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getCalculate(discount, shipping, tariffCode, paymentMethod)
                .with(schedulerProvider)
                .subscribe({
                    calculateState.value = UiState.Success(it)
                },{
                    calculateState.value = UiState.Error(it)
                })
        )
    }

    fun addOrder(
        idPartner: Int,
        params: AddOrderParams
    ){
        addOrderState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.addOrder(idPartner, params)
                .with(schedulerProvider)
                .subscribe({
                    addOrderState.value = UiState.Success(it)
                },{
                    addOrderState.value = UiState.Error(it)
                })
        )

    }

    fun setupBank(v: View, type: Int){
        when(type){
            0 -> v.gone()
            1 -> v.gone()
            2 -> v.visible()
        }
    }

    fun paymentMethode(type: Int): String{
        return when(type){
            1 -> "COD"
            2 -> "BANK TRANSFER"
            else -> ""
        }
    }
    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}