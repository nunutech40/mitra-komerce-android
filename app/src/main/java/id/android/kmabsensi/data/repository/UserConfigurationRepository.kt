package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.FilterSdmReportParams
import id.android.kmabsensi.data.remote.body.UpdateSdmShiftConfigParam
import id.android.kmabsensi.data.remote.service.ApiService

class UserConfigurationRepository(val apiService: ApiService) {

    fun getSdmShiftConfiguration(userManagementId: Int = 0) =
        apiService.getSdmShiftConfiguration(userManagementId = userManagementId)

    fun updateSdmShift(param: UpdateSdmShiftConfigParam) = apiService.updateSdmShift(param)

}