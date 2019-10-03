package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.CreatePermissionResponse
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
        status: RequestBody,
        explanation: RequestBody,
        dateFrom: RequestBody,
        dateTo: RequestBody,
        file: MultipartBody.Part
    ): Single<CreatePermissionResponse> = apiService.createPermission(
        permissionType,
        userId,
        officeId,
        roleId,
        userManagementId,
        status,
        explanation,
        dateFrom,
        dateTo,
        file
    )

}