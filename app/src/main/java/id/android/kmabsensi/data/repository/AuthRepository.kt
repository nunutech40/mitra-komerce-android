package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.LoginResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class AuthRepository(private val apiService: ApiService) {

    fun login(
        usernameEmail: String,
        password: String, fcmToken: String
    ): Single<LoginResponse> {
        return apiService.login(usernameEmail, password, fcmToken)
    }

    fun logout(fcmToken: String): Single<BaseResponse> {
        return apiService.logout(fcmToken)
    }

    fun resetPassword(
        userId: String,
        password: String
    ): Single<BaseResponse> {
        return apiService.resetPassword(userId, password)
    }

    fun forgetPassword(email: String): Single<BaseResponse> {
        return apiService.forgetPassword(email)
    }

}