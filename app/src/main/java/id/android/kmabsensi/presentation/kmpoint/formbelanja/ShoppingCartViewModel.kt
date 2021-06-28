package id.android.kmabsensi.presentation.kmpoint.formbelanja

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.kmpoint.AllShoppingRequestParams
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState

class ShoppingCartViewModel(
    val kmPointRepository: KmPoinRepository
) : BaseViewModel(){

    fun getAllShoppingList(data : AllShoppingRequestParams): MutableLiveData<UiState<AllShoppingRequestResponse>> = kmPointRepository.allShoppingRequest(compositeDisposable, params = data)

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}