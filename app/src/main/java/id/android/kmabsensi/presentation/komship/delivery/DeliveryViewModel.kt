package id.android.kmabsensi.presentation.komship.delivery

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.komship.AddOrderParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.*
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.presentation.komship.ordercart.ValidateChecked
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import id.android.kmabsensi.utils.visible

class DeliveryViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
): BaseViewModel() {
    val calculateState : MutableLiveData<UiState<KomCalculateResponse>> = MutableLiveData()

    val addOrderState : MutableLiveData<UiState<KomAddOrderResponse>> = MutableLiveData()

    val customerState : MutableLiveData<UiState<KomCustomerResponse>> = MutableLiveData()

    val bankState : MutableLiveData<UiState<KomBankResponse>> = MutableLiveData()

    val destinationState : MutableLiveData<UiState<KomDestinationResponse>> = MutableLiveData()

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

    fun getBank(){
        bankState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getBank()
                .with(schedulerProvider)
                .subscribe({
                    bankState.value = UiState.Success(it)
                },{
                    bankState.value = UiState.Error(it)
                })
        )
    }

    fun getCustomer(search: String? = null){
        customerState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getCustomer(search)
                .with(schedulerProvider)
                .subscribe({
                    customerState.value = UiState.Success(it)
                },{
                    customerState.value = UiState.Error(it)
                })
        )
    }

    fun getCalculate(
        discount: Int? = null,
        shipping: String,
        tariffCode: String,
        paymentMethod: String,
        partnerId: Int,
        cartId: List<Int>? = null
    ){
        calculateState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getCalculate(discount, shipping, tariffCode, paymentMethod, partnerId, cartId)
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

    fun shippingCost(type: String, list: List<CalculateItem>): ResultCalculate{
        var calculate = CalculateItem()
        var isCalculate = false
        list.forEach {
            if (it.shippingType?.lowercase() == type.lowercase()){
                calculate = it
                isCalculate = true
            }
        }
        return ResultCalculate(calculate, isCalculate)
    }

    fun setupListCart(isLess: Boolean, list: List<CartItem>): MutableList<CartItem>{
        val listCart = ArrayList<CartItem>()
        if (isLess){
            for (idx in 0 until 2){
                listCart.add(list[idx])
            }
        }else{
            list.forEach {
                listCart.add(it)
            }
        }
        return listCart
    }

    fun getCostOrder(list: List<CartItem>): Int{
        var cost = 0
        list.forEach {
            cost += (it.productPrice!!*it.qty!!)
        }
        return cost
    }

    fun getCustomerDetail(list: List<CustomerItem>, item: String): CustomerItem{
        var cusItem = CustomerItem(name = item)
        list.forEach {
            if (it.name?.lowercase() == item.lowercase()){
                cusItem = it
                Log.d("_getCustomerDetail", "getCustomerDetail: $it")
            }
        }
        return cusItem
    }

    fun getBankDetail(list: List<BankItem>, item: String): BankItem{
        var bank = BankItem()
        list.forEach {
            if (it.bankName?.lowercase()!!.contains(item)){
                bank = it
            }
        }
        return bank
    }
    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }

    /** get id Cart */
    fun getIdOrder(list: ArrayList<CartItem>?): ArrayList<Int> {
        val listId = ArrayList<Int>()
        list!!.forEach {
            listId.add(it.cartId!!)
        }
        return listId
    }

}

data class ResultCalculate(
    val item : CalculateItem,
    val isCalculate: Boolean
)