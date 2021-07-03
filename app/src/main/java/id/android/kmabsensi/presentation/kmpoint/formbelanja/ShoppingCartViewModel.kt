package id.android.kmabsensi.presentation.kmpoint.formbelanja

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.isEmpty

class ShoppingCartViewModel(
    val kmPointRepository: KmPoinRepository
) : BaseViewModel(){

    fun getAllShoppingListPaged(status : String? = null, user_request_id : Int? = null): LiveData<PagedList<ShoppingRequestModel>>
    = kmPointRepository.getShoppingRequest(compositeDisposable, status = status, user_requester_id = user_request_id)

    fun getState(): LiveData<State> =
            kmPointRepository.getState()

    fun isEmpty(): LiveData<isEmpty> =
            kmPointRepository.isEmptyShopping()

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}