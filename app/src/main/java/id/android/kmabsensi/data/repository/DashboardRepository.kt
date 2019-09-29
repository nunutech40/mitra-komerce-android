package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.DashboardResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class DashboardRepository(val apiService: ApiService) {

    fun getDashboardInfo(userId: Int) : Single<DashboardResponse> {
        return apiService.getDashboardInfo(userId)
    }

}
