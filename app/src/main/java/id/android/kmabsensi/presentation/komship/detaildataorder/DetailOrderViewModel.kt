package id.android.kmabsensi.presentation.komship.detaildataorder

import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.KomOrderDetailResponse
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import java.text.SimpleDateFormat

class DetailOrderViewModel(
    private val repository: KomShipRepository,
    private val schedulerProvider: SchedulerProvider
): BaseViewModel(){

    val detailOrderState : MutableLiveData<UiState<KomOrderDetailResponse>> = MutableLiveData()
    val deleteState : MutableLiveData<UiState<BaseResponse>> = MutableLiveData()

    fun getDetailOrder(idPartner: Int, idOrder: Int){
        detailOrderState.value = UiState.Loading()
        compositeDisposable.add(
            repository.getDetailOrder(idPartner, idOrder)
                .with(schedulerProvider)
                .subscribe({
                    detailOrderState.value = UiState.Success(it)
                },{
                    detailOrderState.value = UiState.Error(it)
                })
        )
    }

    fun deleteOrder(idPartner: Int, idOrder: Int){
        deleteState.value = UiState.Loading()
        compositeDisposable.add(
            repository.deleteOrder(idPartner, idOrder)
                .with(schedulerProvider)
                .subscribe({
                    deleteState.value = UiState.Success(it)
                },{
                    deleteState.value = UiState.Error(it)
                })
        )
    }

    fun getTime(date: String): String{
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val convert = SimpleDateFormat("HH:mm")
        return convert.format(parser.parse(date))
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }

}