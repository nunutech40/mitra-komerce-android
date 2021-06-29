package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.response.kmpoint.CreateShoppingRequestResponse
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState

class ShoppingDetailFinanceViewModel(
        val kmPointRepository: KmPoinRepository
) : BaseViewModel() {

    var shoppingDetail : MutableLiveData<UiState<DetailShoppingResponse>> = MutableLiveData()


    fun getShoppingDetail(id : Int) {
        shoppingDetail = kmPointRepository.shoppingRequestDetail(compositeDisposable, id = id)
    }

    fun updateShoppingRequest(id : Int, body : UpdateShoppingRequestParams): MutableLiveData<UiState<CreateShoppingRequestResponse>>
    = kmPointRepository.updateShoppingRequest(compositeDisposable, id = id, body = body)

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}