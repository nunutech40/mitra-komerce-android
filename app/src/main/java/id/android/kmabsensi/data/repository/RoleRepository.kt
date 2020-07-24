package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.AssignReleasePositionParams
import id.android.kmabsensi.data.remote.service.ApiService

class RoleRepository(val apiService: ApiService) {

    fun getMenuRole() = apiService.getMenuRole()

    fun assignPosition(params: AssignReleasePositionParams) = apiService.assignPosition(params)

    fun releasePosition(params: AssignReleasePositionParams) = apiService.releasePosition(params)

}