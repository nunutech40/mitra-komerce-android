package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPermissionResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PermissionRepository(val apiService: ApiService) {

    fun createPermission(
        permissionType: RequestBody,
        userId: RequestBody,
        officeId: RequestBody,
        roleId: RequestBody,
        userManagementId: RequestBody,
        explanation: RequestBody,
        dateFrom: RequestBody,
        dateTo: RequestBody,
        attachment_leader: MultipartBody.Part,
        attachment_partner: MultipartBody.Part?
    ): Single<BaseResponse> = apiService.createPermission(
        permissionType,
        userId,
        officeId,
        roleId,
        userManagementId,
        explanation,
        dateFrom,
        dateTo,
        attachment_leader,
        attachment_partner
    )

    fun getListPermission(
        roleId: Int,
        userManagementId: Int,
        userId: Int
    ): Single<ListPermissionResponse> {
        return apiService.getListPermission(roleId, userManagementId, userId)
    }

    fun filterPermission(
        roleId: Int,
        userManagementId: Int,
        userId: Int,
        dateFrom: String,
        dateTo: String,
        status: Int
    ): Single<ListPermissionResponse> {
        return apiService.filterListPermission(roleId, userManagementId, userId, dateFrom, dateTo, status)
    }

    fun approvePermission(
        permissionId: Int,
        status: Int
    ): Single<BaseResponse> {
        return apiService.approvePermission(permissionId, status)
    }

}