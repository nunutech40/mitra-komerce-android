package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.PresenceCheckResponse
import id.android.kmabsensi.data.remote.response.PresenceReportResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.*

class PresenceRepository(val apiService: ApiService) {

    fun presenceCheck(userId: Int) : Single<PresenceCheckResponse> {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = dateFormat.format(cal.time)
        return apiService.presenceCheck(userId, now)

    }

    fun checkIn(file: MultipartBody.Part) = apiService.checkIn(file)

    fun checkOut(absenId: Int) = apiService.checkOut(absenId)

    fun presenceHistory(userId: Int) = apiService.presenceHistory(userId)

    fun presenceReport(
        roleId: Int,
        userManagementId: Int,
        officeId: Int,
        reportDate: String
    ): Single<PresenceReportResponse>{
        return apiService.presenceReport(roleId, userManagementId, officeId, reportDate)
    }

}