package id.android.kmabsensi.presentation.myevaluation

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.MyEvaluationResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.repository.EvaluationRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class EvaluationViewModel(
    val evaluationRepository: EvaluationRepository,
    private val preferencesHelper: PreferencesHelper,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val myEvaluations by lazy {
        MutableLiveData<UiState<MyEvaluationResponse>>()
    }

    fun getMyEvaluation() {
        myEvaluations.value = UiState.Loading()
        val user = Gson().fromJson<User>(
            preferencesHelper.getString(PreferencesHelper.PROFILE_KEY),
            User::class.java
        )
        compositeDisposable.add(evaluationRepository.getMyEvaluation(user.id)
            .with(schedulerProvider)
            .subscribe({
                myEvaluations.value = UiState.Success(it)
            }, {
                myEvaluations.value = UiState.Error(it)
            })
        )
    }

    fun getUserData(): User {
        return Gson().fromJson<User>(
            preferencesHelper.getString(PreferencesHelper.PROFILE_KEY),
            User::class.java
        )
    }

    override fun onError(error: Throwable) {

    }
}