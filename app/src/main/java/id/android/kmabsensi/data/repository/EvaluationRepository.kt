package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.FilterEvaluationCollaborationParams
import id.android.kmabsensi.data.remote.response.MyEvaluationResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class EvaluationRepository(val apiService: ApiService) {

    fun getMyEvaluation(userId: Int): Single<MyEvaluationResponse>{
        return apiService.getMyEvaluation(userId)
    }

    fun getLeaderEvaluation(
        startPeriode: String,
        endPeriode: String,
        userId: Int = 0
    ): Single<MyEvaluationResponse> {

        val body = mapOf(
            "start_period" to startPeriode,
            "end_period" to endPeriode,
            "user_id" to userId
        )

        return apiService.getLeaderEvaluation(body)
    }

    fun getEvaluationCollaboration() = apiService.getEvaluationCollaboration()

    fun filterEvaluationCollaboration(params: FilterEvaluationCollaborationParams) =
        apiService.filterEvaluationCollaboration(params)

}