package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

/**
 * Created by Abdul Aziz on 30/08/20.
 */
class KmPoinRepository(val apiService: ApiService) {

    fun redeemPoin(
        userId: Int = 0,
        totalRequestRedeempoin: Int
    ): Single<BaseResponse> {
        val body = mapOf(
            "user_id" to userId,
            "total_request_redeem_kmpoin" to totalRequestRedeempoin
        )
        return apiService.redeemPoin(body)
    }
}