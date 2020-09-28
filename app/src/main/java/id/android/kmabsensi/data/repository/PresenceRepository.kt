package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.ListAlphaParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.PresenceCheckResponse
import id.android.kmabsensi.data.remote.response.PresenceReportResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*

class PresenceRepository(val apiService: ApiService) {

    fun presenceCheck(userId: Int) : Single<PresenceCheckResponse> {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = dateFormat.format(cal.time)
        return apiService.presenceCheck(userId, now)
    }

    fun checkIn(file: MultipartBody.Part, ontimeLevel: RequestBody) = apiService.checkIn(file, ontimeLevel)

    fun checkOut(absenId: Int, file: MultipartBody.Part) = apiService.checkOut(absenId, file)

    fun presenceHistory(userId: Int) = apiService.presenceHistory(userId)

    fun presenceReport(
        roleId: Int,
        userManagementId: Int,
        officeId: Int,
        reportDate: String
    ): Single<PresenceReportResponse>{
        return apiService.presenceReport(roleId, userManagementId, officeId, reportDate)
    }

    fun getPresenceReportFiltered(
        roleId: Int,
        userManagementId: Int,
        officeId: Int,
        startDate: String,
        endDate: String
    ): Single<PresenceReportResponse> {
        val body = mapOf(
            "role_id" to roleId,
            "user_management_id" to userManagementId,
            "office_id" to officeId,
            "no_partner" to 0,
            "start_date" to startDate,
            "end_date" to endDate
        )
        return apiService.presenceReportFiltered(body)
    }

    fun reportAbsen(
        userId: Int,
        presenceDate: String,
        description: String
    ): Single<BaseResponse> {
        val body = mapOf(
            "user_id" to userId,
            "presence_date" to presenceDate,
            "description" to description
        )
        return apiService.reportAbsen(body)
    }

    fun getListAlpha(params: ListAlphaParams) = apiService.getListAlpha(params)

}