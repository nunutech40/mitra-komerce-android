package id.android.kmabsensi.presentation.kmpoint.penarikandetail

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.kmpoint.DetailWithdrawResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState

class DetailWithDrawViewModel(
        val kmPointRepository: KmPoinRepository
): BaseViewModel() {

    fun getDetalWithdraw(id : Int): MutableLiveData<UiState<DetailWithdrawResponse>>
    = kmPointRepository.getDetailWithdraw(compositeDisposable, id)

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}