package id.android.kmabsensi.presentation.komship.ordercart

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.komship.DeleteParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.data.remote.response.komship.KomCartResponse
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.data.remote.response.komship.KomPartnerResponse
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import kotlinx.android.parcel.Parcelize

class OrderCartViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val cartState: MutableLiveData<UiState<KomCartResponse>> = MutableLiveData()

    val deleteState: MutableLiveData<UiState<BaseResponse>> = MutableLiveData()

    val updateQtyState: MutableLiveData<UiState<BaseResponse>> = MutableLiveData()

    val partnerState: MutableLiveData<UiState<KomPartnerResponse>> = MutableLiveData()

    fun GetDataCart() {
        cartState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getCart()
                .with(schedulerProvider)
                .subscribe(
                    {
                        cartState.value = UiState.Success(it)
                    }, {
                        cartState.value = UiState.Error(it)
                    }
                )
        )
    }

    fun DeleteCart(p: List<Int>) {
        deleteState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.deleteCart(p)
                .with(schedulerProvider)
                .subscribe({
                    deleteState.value = UiState.Success(it)
                }, {
                    deleteState.value = UiState.Error(it)
                })
        )
    }

    fun UpdateQtyCart(cartId: Int, qty: Int) {
        updateQtyState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.updateQtyCart(cartId, qty)
                .with(schedulerProvider)
                .subscribe({
                    updateQtyState.value = UiState.Success(it)
                }, {
                    updateQtyState.value = UiState.Error(it)
                })
        )
    }

    fun getPartner() {
        partnerState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getPartner()
                .with(schedulerProvider)
                .subscribe({
                    partnerState.value = UiState.Success(it)
                }, {
                    partnerState.value = UiState.Error(it)
                })
        )
    }

    fun filterCart(list: ArrayList<CartItem>?, idPartner: Int): ArrayList<CartItem> {
        var filter = ArrayList<CartItem>()
        list?.forEach {
            if (it.partnerId == idPartner){
                filter.add(it)
            }
        }
        return filter
    }

    fun validateOrderChecked(list: MutableList<ValidateChecked>): MutableList<ValidateChecked> {
        val count: MutableList<ValidateChecked> = ArrayList()
        list.forEach {
            if (it.isChecked) count.add(it)
        }
        return count
    }

    fun getPartnerPosition(list: MutableList<KomPartnerItem>, idPartner : Int): Int{
        var position = 0
        for (id in 0 until list.size){
            if (list[id].partnerId == idPartner){
                position = id
            }
        }
        return position
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}

@Parcelize
data class ValidateChecked(
    val item: CartItem,
    val position: Int,
    val isChecked: Boolean
) : Parcelable

@Parcelize
data class CartListItem(
    val isChecked: Boolean,
    val item: CartItem
) : Parcelable
