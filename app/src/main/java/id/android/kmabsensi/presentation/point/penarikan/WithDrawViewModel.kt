package id.android.kmabsensi.presentation.point.penarikan

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState

class WithDrawViewModel(
        val kmPointRepository: KmPoinRepository
) : BaseViewModel() {

    fun getDataWithdraw(page : Int? = 1, limit: Int? = 10) : MutableLiveData<UiState<GetWithdrawResponse>> =
            kmPointRepository.getDataWithdraw(compositeDisposable, page!!, limit!!)

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}