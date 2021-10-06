package id.android.kmabsensi.presentation.komship

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.komship.AddCartParams
import id.android.kmabsensi.data.remote.body.komship.OrderByPartnerParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.KomOrderByPartnerResponse
import id.android.kmabsensi.data.remote.response.komship.KomPartnerResponse
import id.android.kmabsensi.data.remote.response.komship.KomProductByPartnerResponse
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class MyOrderViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
    ) : BaseViewModel(){

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }

    val partnerState : MutableLiveData<UiState<KomPartnerResponse>> = MutableLiveData()
    val productState : MutableLiveData<UiState<KomProductByPartnerResponse>> = MutableLiveData()
    val addCartState : MutableLiveData<UiState<BaseResponse>> = MutableLiveData()
    val orderByPartnerState : MutableLiveData<UiState<KomOrderByPartnerResponse>> = MutableLiveData()

    fun validateMinProduct(total : Int) : Boolean = total>1

    fun validateMaxProduct(total : Int, max : Int) : Boolean = total<=max

    fun getOrderByPartner(idPartner: Int,
                          orderParams: OrderByPartnerParams){
        orderByPartnerState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getOrderByPartner(idPartner, orderParams)
                .with(schedulerProvider)
                .subscribe({
                    orderByPartnerState.value = UiState.Success(it)
                },{
                    orderByPartnerState.value = UiState.Error(it)
                })
        )
    }

    fun getPartner(){
        partnerState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getPartner()
                .with(schedulerProvider)
                .subscribe({
                    partnerState.value = UiState.Success(it)
                },{
                    partnerState.value = UiState.Error(it)
                })
        )
    }

    fun getProduct(idPartner : Int){
        productState.value = UiState.Loading()

        compositeDisposable.add(
            komShipRepository.getProductByPartner(idPartner)
                .with(schedulerProvider)
                .subscribe({
                       productState.value = UiState.Success(it)
                },{
                    productState.value = UiState.Error(it)
                })
        )
    }

    fun addCart(data : AddCartParams){
        addCartState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.addCart(data)
                .with(schedulerProvider)
                .subscribe({
                    addCartState.value = UiState.Success(it)
                },{
                    addCartState.value = UiState.Error(it)
                })
        )
    }
}