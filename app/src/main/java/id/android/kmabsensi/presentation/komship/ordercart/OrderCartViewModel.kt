package id.android.kmabsensi.presentation.komship.ordercart

import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.repository.KomShipRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.rx.SchedulerProvider

class OrderCartViewModel(
    val komShipRepository: KomShipRepository,
    val schedulerProvider: SchedulerProvider
) :BaseViewModel(){

    fun validateOrderChecked(list : MutableList<ValidateChecked>) : MutableList<ValidateChecked>{
        val count : MutableList<ValidateChecked> = ArrayList()
        list.forEach {
            if (it.isChecked) count.add(it)
        }
        return count
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}

data class ValidateChecked(
    val id : Int,
    val position : Int,
    val isChecked: Boolean
)
