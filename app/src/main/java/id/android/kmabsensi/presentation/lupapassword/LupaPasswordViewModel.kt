package id.android.kmabsensi.presentation.lupapassword

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.repository.AuthRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class LupaPasswordViewModel(val authRepository: AuthRepository,
                            val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val response = MutableLiveData<UiState<BaseResponse>>()

    fun forgetPassword(email: String){
        response.value = UiState.Loading()
        compositeDisposable.add(authRepository.forgetPassword(email)
            .with(schedulerProvider)
            .subscribe({
                response.value = UiState.Success(it)
            }, this::onError))
    }

    override fun onError(error: Throwable) {
        response.value = UiState.Error(error)
        Crashlytics.log(error.message)
    }
}