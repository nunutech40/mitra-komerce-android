package id.android.kmabsensi.presentation.sdm.editpassword

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.repository.AuthRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class EditPasswordViewModel(val authRepository: AuthRepository,
                            val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val response = MutableLiveData<UiState<BaseResponse>>()

    fun resetPassword(userId: String,
                      password: String){
        response.value = UiState.Loading()
        compositeDisposable.add(authRepository.resetPassword(userId, password)
            .with(schedulerProvider)
            .subscribe({
                response.value = UiState.Success(it)
            }, this::onError))
    }

    override fun onError(error: Throwable) {
        response.value = UiState.Error(error)
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}