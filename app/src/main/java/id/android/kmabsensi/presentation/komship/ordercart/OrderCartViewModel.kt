package id.android.kmabsensi.presentation.komship.ordercart

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.komship.DeleteParams
import id.android.kmabsensi.data.remote.body.komship.UpdateQtyParams
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
            if (it.isChecked!!) count.add(it)
        }
        return count
    }

    fun getPartnerPosition(list: MutableList<KomPartnerItem>, idPartner : Int): Int{
        var position = 0
        for (id in 0..(list.size-1)){
            if (list[id].partnerId == idPartner){
                position = id
                break
            }
        }
        return position
    }

    fun updateListQtyCart(list: List<UpdateQtyParams>) {
        updateQtyState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.updateListQtyCart(list)
                .with(schedulerProvider)
                .subscribe({
                    updateQtyState.value = UiState.Success(it)
                }, {
                    updateQtyState.value = UiState.Error(it)
                })
        )
    }

    fun getUpdateQtyParams(list: List<CartListItem>): List<UpdateQtyParams>{
        val params = ArrayList<UpdateQtyParams>()
        list.forEach {
            it.item.apply {
                params.add(UpdateQtyParams(cartId?:0, qty?:0))
            }
        }
        return params
    }

    /**
     * new
     */

    fun getCartPosition(list: List<CartItem>, id: Int): Int{
        var position = 0
        for (idx in 0..(list.size-1)){
            if (list[idx].cartId == id){
                position = idx
                break
            }
        }
        return position
    }

    fun countCheckedItem(list: List<CartListItem>): Int{
        var total = 0
        list.forEach {
            if (it.isChecked) total++
        }
        return total
    }

    fun totalCost(list: List<CartListItem>): Double{
        var total = 0
        list.forEach {
            if (it.isChecked) {
                it.item.apply {
                    val cost = productPrice?.times(qty!!)
                    Log.d("_totalCost", "qty: ${it.item.qty}, ${it.item.productPrice?:0}, cost = $cost, ")
                    total += cost?:0
                }

            }
        }
        return total.toDouble()
    }

    fun getCartId(list: List<CartListItem>): List<Int>{
        val listId = ArrayList<Int>()
        list.forEach {
            if (it.isChecked){
                listId.add(it.item.cartId?:0)
            }
        }
        return listId
    }

    fun getCheckedCartItem(list: List<CartListItem>): List<CartItem>{
        val listCart = ArrayList<CartItem>()
        list.forEach {
            if (it.isChecked){
                listCart.add(it.item)
            }
        }
        return listCart
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}

@Parcelize
data class ValidateChecked(
    val item: CartItem,
    val position: Int? = 0,
    val isChecked: Boolean? = false
) : Parcelable

@Parcelize
data class CartListItem(
    val item: CartItem,
    val isChecked: Boolean,
    val isUpdate: Boolean
) : Parcelable
