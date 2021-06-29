package id.android.kmabsensi.presentation.kmpoint.penarikandetail

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.body.kmpoint.RequestWithdrawParams
import id.android.kmabsensi.data.remote.response.kmpoint.DetailWithdrawResponse
import id.android.kmabsensi.data.remote.response.kmpoint.UpdateWithdrawResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState

class DetailWithDrawViewModel(
        val kmPointRepository: KmPoinRepository
): BaseViewModel() {

    var requestWithdraw = MutableLiveData<UiState<UpdateWithdrawResponse>>()

    fun getDetalWithdraw(id : Int): MutableLiveData<UiState<DetailWithdrawResponse>>
    = kmPointRepository.getDetailWithdraw(compositeDisposable, id)

    fun updateStatusWithdraw(id: Int, status: String? = "completed"): MutableLiveData<UiState<UpdateWithdrawResponse>>
    = kmPointRepository.updateStatusWithdraw(
            compositeDisposable,
            id = id,
            status = status!!)

    fun requestWithdraw(params : RequestWithdrawParams): MutableLiveData<UiState<UpdateWithdrawResponse>>
    = kmPointRepository.requestWithdraw(compositeDisposable, params = params)

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}