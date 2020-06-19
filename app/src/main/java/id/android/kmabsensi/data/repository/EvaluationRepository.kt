package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.MyEvaluationResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class EvaluationRepository(val apiService: ApiService) {

    fun getMyEvaluation(userId: Int): Single<MyEvaluationResponse>{
        return apiService.getMyEvaluation(userId)
    }

}