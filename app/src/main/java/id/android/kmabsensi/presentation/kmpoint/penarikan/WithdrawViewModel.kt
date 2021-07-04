package id.android.kmabsensi.presentation.kmpoint.penarikan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingRequestModel
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.isEmpty

class WithdrawViewModel(
        val kmPointRepository: KmPoinRepository
) : BaseViewModel() {

    fun getAllWithdrawPaged(): LiveData<PagedList<WithdrawMainModel>>
    = kmPointRepository.getWithdraw(compositeDisposable)


    fun getState(): LiveData<State> =
            kmPointRepository.getStateWithdraw()

    fun isEmpty(): LiveData<isEmpty> =
            kmPointRepository.isEmptyWithdrawal()

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}