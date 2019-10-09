package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.SingleUserResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class UserRepository(val apiService: ApiService) {

//    fun getProfileUser(accessToken: String, userId: Int): Single<UserResponse> {
//        return apiService.getProfileData(accessToken, userId)
//    }

    fun getProfileUser(userId: Int): Single<UserResponse> {
        return apiService.getProfileData(userId)
    }


    fun getUserByRole(roleId: Int, userManagementId: Int = 0): Single<UserResponse>{
        return apiService.getUser(roleId = roleId)
    }

}