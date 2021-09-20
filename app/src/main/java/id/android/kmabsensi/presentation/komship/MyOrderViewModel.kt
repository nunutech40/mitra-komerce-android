package id.android.kmabsensi.presentation.komship

import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.rx.SchedulerProvider

class MyOrderViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
    ) : BaseViewModel(){
    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }

    fun validateMinProduct(total : Int) : Boolean = total>0

    fun validateMaxProduct(total : Int, max : Int) : Boolean = total<=max
}