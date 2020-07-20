package id.android.kmabsensi.presentation.myevaluation

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.FilterEvaluationCollaborationParams
import id.android.kmabsensi.data.remote.response.EvaluationCollaborationResponse
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

    val leaderEvaluation by lazy {
        MutableLiveData<UiState<MyEvaluationResponse>>()
    }

    val evaluationCollaborations by lazy {
        MutableLiveData<UiState<EvaluationCollaborationResponse>>()
    }

    fun getLeaderEvaluation(
        startPeriode: String,
        endPeriod: String,
        userId: Int = 0
    ) {
        leaderEvaluation.value = UiState.Loading()
        compositeDisposable.add(
            evaluationRepository.getLeaderEvaluation(startPeriode, endPeriod, userId)
                .with(schedulerProvider)
                .subscribe({
                    leaderEvaluation.value = UiState.Success(it)
                }, {
                    leaderEvaluation.value = UiState.Error(it)
                })
        )
    }

    fun getMyEvaluation() {
        myEvaluations.value = UiState.Loading()
        val user = Gson().fromJson<User>(
            preferencesHelper.getString(PreferencesHelper.PROFILE_KEY),
            User::class.java
        )
        compositeDisposable.add(
            evaluationRepository.getMyEvaluation(user.id)
                .with(schedulerProvider)
                .subscribe({
                    myEvaluations.value = UiState.Success(it)
                }, {
                    myEvaluations.value = UiState.Error(it)
                })
        )
    }

    fun getEvaluationCollaboration(){
        evaluationCollaborations.value = UiState.Loading()
        compositeDisposable.add(evaluationRepository.getEvaluationCollaboration()
            .with(schedulerProvider)
            .subscribe({
                evaluationCollaborations.value = UiState.Success(it)
            },{
                evaluationCollaborations.value = UiState.Error(it)
            }))
    }

    fun filterEvaluationCollaboration(params: FilterEvaluationCollaborationParams){
        evaluationCollaborations.value = UiState.Loading()
        compositeDisposable.add(evaluationRepository.filterEvaluationCollaboration(params)
            .with(schedulerProvider)
            .subscribe({
                evaluationCollaborations.value = UiState.Success(it)
            },{
                evaluationCollaborations.value = UiState.Error(it)
            }))
    }



    override fun onError(error: Throwable) {

    }
}