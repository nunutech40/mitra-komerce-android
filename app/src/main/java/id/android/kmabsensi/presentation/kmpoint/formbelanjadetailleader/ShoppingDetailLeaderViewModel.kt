package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.response.kmpoint.CreateShoppingRequestResponse
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState

class ShoppingDetailLeaderViewModel(
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

    fun getTextStatus(status : String) : String{
        return when (status) {
            "requested" -> "Diajukan"
            "completed" -> "Selesai"
            "approved" -> "Disetujui"
            "rejected" -> "Ditolak"
            "canceled" -> "Dibatalkan"
            else -> "-"
        }
    }

    fun getStatusTextColor(status : String) : Int{
        return  when (status) {
            "requested" -> R.color.cl_yellow
            "completed" -> R.color.cl_blue
            "approved" -> R.color.cl_green
            "rejected" -> R.color.cl_orange
            "canceled" -> R.color.cl_orange
            else -> R.color.cl_yellow
        }
    }

    fun getStatusBackgroundColor(status : String) : Int{
        return  when (status) {
            "requested" -> R.color.cl_semi_yellow
            "completed" -> R.color.cl_semi_blue
            "approved" -> R.color.cl_semi_green
            "rejected" -> R.color.cl_semi_orange
            "canceled" -> R.color.cl_semi_orange
            else -> R.color.cl_semi_yellow
        }
    }

    fun editable(status : String): Boolean{
        return when (status) {
            "completed" -> false
            "rejected" -> false
            "canceled" -> false
            else -> true
        }
    }
}