package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.ProfileResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class UserRepository(val apiService: ApiService) {

    fun getProfileUser(accessToken: String, userId: Int): Single<ProfileResponse> {
        return apiService.getProfileData(accessToken, userId)
    }

    fun getUserByRole(roleId: Int): Single<UserResponse>{
        return apiService.getUser(roleId = roleId)
    }

}