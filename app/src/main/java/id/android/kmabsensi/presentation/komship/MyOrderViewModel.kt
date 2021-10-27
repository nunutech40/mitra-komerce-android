package id.android.kmabsensi.presentation.komship

import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.komship.AddCartParams
import id.android.kmabsensi.data.remote.body.komship.OrderByPartnerParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.*
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class MyOrderViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }

    val partnerState: MutableLiveData<UiState<KomPartnerResponse>>
    = MutableLiveData()
    val productState: MutableLiveData<UiState<KomProductByPartnerResponse>>
    = MutableLiveData()
    val addCartState: MutableLiveData<UiState<KomAddCartResponse>>
    = MutableLiveData()
    val orderByPartnerState: MutableLiveData<UiState<KomListOrderByPartnerResponse>>
    = MutableLiveData()

    fun validateMinProduct(total: Int): Boolean = total > 1

    fun validateMaxProduct(total: Int, max: Int): Boolean = total < max

    fun getOrderByPartner(
        idPartner: Int,
        orderParams: OrderByPartnerParams
    ) {
        orderByPartnerState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.getOrderByPartner(idPartner, orderParams)
                .with(schedulerProvider)
                .subscribe({
                    orderByPartnerState.value = UiState.Success(it)
                }, {
                    orderByPartnerState.value = UiState.Error(it)
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

    fun getProduct(idPartner: Int) {
        productState.value = UiState.Loading()

        compositeDisposable.add(
            komShipRepository.getProductByPartner(idPartner)
                .with(schedulerProvider)
                .subscribe({
                    productState.value = UiState.Success(it)
                }, {
                    productState.value = UiState.Error(it)
                })
        )
    }

    fun addCart(data: AddCartParams) {
        addCartState.value = UiState.Loading()
        compositeDisposable.add(
            komShipRepository.addCart(data)
                .with(schedulerProvider)
                .subscribe({
                    addCartState.value = UiState.Success(it)
                }, {
                    addCartState.value = UiState.Error(it)
                })
        )
    }

    fun getVariantName(idV: Int, item: VariantKomItem): String {
        var variant = ""
        item.variantOption?.forEach {
            if (it.optionId == idV) {
                variant = it.optionName!!
            }
        }
        return variant
    }

    fun addVariantName(list: ArrayList<String>, position: Int, variant: String): ArrayList<String> {
        if (list.size >= (position + 1))
            list.set(position, variant)
        else
            list.add(position, variant)
        return list
    }

    fun variantName(list: List<String>): String {
        var name = ""
        for (idx in list.indices) {
            name += if (idx != list.size - 1) "${list[idx]} - " else list[idx]
        }
        return name
    }

    fun getIdPartnerFromList(list: List<KomPartnerItem>, positiion: Int): Int {
        return list[positiion].partnerId!!
    }

    fun hiddenVariant(
        position: Int,
        ll2: LinearLayoutCompat,
        ll3: LinearLayoutCompat,
        ll4: LinearLayoutCompat,
        ll5: LinearLayoutCompat
    ) {
        when (position) {
            0 -> {
                ll2.gone()
                ll3.gone()
                ll4.gone()
                ll5.gone()
            }
            1 -> {
                ll3.gone()
                ll4.gone()
                ll5.gone()
            }
            2 -> {
                ll4.gone()
                ll5.gone()
            }
            3 -> {
                ll5.gone()
            }
        }
    }

    fun getDataOrderParam(
        idPartner: Int,
        pk: ProductKomItem,
        pv: ProductVariantKomItem,
        vn: String,
        total: Int
    ): AddCartParams {
        return AddCartParams(
            idPartner,
            pk.productId!!,
            pk.productName!!,
            pv.optionId!!,
            vn,
            pv.price!!,
            pk.weight!!,
            total,
            (total * pv.price)
        )

    }
}

