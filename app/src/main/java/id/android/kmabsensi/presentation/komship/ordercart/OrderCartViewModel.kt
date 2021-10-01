package id.android.kmabsensi.presentation.komship.ordercart

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.data.remote.response.komship.KomCartResponse
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class OrderCartViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
) :BaseViewModel(){

    val cartState : MutableLiveData<UiState<KomCartResponse>> = MutableLiveData()

    val deleteState : MutableLiveData<UiState<BaseResponse>> = MutableLiveData()

    val updateQtyState : MutableLiveData<UiState<BaseResponse>> = MutableLiveData()

    fun GetDataCart(){
        cartState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getCart()
                .with(schedulerProvider)
                .subscribe(
                    {
                        cartState.value = UiState.Success(it)
                    },{
                        cartState.value = UiState.Error(it)
                    }
                )
        )
    }

    fun DeleteCart(listId : ArrayList<Int>){
        deleteState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.deleteCart(listId)
                .with(schedulerProvider)
                .subscribe({
                    deleteState.value = UiState.Success(it)
                },{
                    deleteState.value = UiState.Error(it)
                })
        )
    }

    fun UpdateQtyCart(cartId: Int, qty: Int){
        updateQtyState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.updateQtyCart(cartId, qty)
                .with(schedulerProvider)
                .subscribe({
                    updateQtyState.value = UiState.Success(it)
                },{
                    updateQtyState.value = UiState.Error(it)
                })
        )
    }

    fun validateOrderChecked(list : MutableList<ValidateChecked>) : MutableList<ValidateChecked>{
        val count : MutableList<ValidateChecked> = ArrayList()
        list.forEach {
            if (it.isChecked) count.add(it)
        }
        return count
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}

data class ValidateChecked(
    val item : CartItem,
    val position : Int,
    val isChecked: Boolean
)
