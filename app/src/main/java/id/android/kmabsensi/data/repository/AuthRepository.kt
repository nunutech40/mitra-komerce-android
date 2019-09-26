package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.LoginResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class AuthRepository(private val apiService: ApiService) {

    fun login(usernameEmail: String,
              password: String): Single<LoginResponse> {
        return apiService.login(usernameEmail, password)
    }

}