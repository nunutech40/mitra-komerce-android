package id.android.kmabsensi.presentation.kmpoint.penarikan

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState

class WithdrawViewModel(
        val kmPointRepository: KmPoinRepository
) : BaseViewModel() {

    var detailWithdraw = MutableLiveData<UiState<GetWithdrawResponse>>()

    fun getDataWithdraw(page : Int? = 1, limit: Int? = 10){
        detailWithdraw = kmPointRepository.getDataWithdraw(compositeDisposable, page!!, limit!!)
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}