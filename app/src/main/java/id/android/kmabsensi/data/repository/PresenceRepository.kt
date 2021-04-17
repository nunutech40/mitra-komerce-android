package id.android.kmabsensi.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.android.kmabsensi.data.remote.body.ListAlphaParams
import id.android.kmabsensi.data.remote.datasource.presentasiuser.PresentasiBody
import id.android.kmabsensi.data.remote.datasource.presentasiuser.PresentasiDataSource
import id.android.kmabsensi.data.remote.datasource.presentasiuser.PrsentasiDataSourceFactory
import id.android.kmabsensi.data.remote.datasource.userdata.UserDataSource
import id.android.kmabsensi.data.remote.datasource.userdata.UserDataSourceFactory
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.utils.State
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
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
    private lateinit var factory : PrsentasiDataSourceFactory

    fun getPresenceReportFilteredPaging(
            compositeDisposable: CompositeDisposable,
            roleId: Int,
            userManagementId: Int,
            officeId: Int,
            startDate: String,
            endDate: String
    ): LiveData<PagedList<Presence>> {
        val body = PresentasiBody(
                roleId = roleId,
                userManagementId = userManagementId,
                officeId = officeId,
                startDate = startDate,
                endDate = endDate)

        factory = PrsentasiDataSourceFactory(apiService, compositeDisposable, body)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(100)
                .build()

        return LivePagedListBuilder(factory, config).build()
    }

    fun getReportFilter() : LiveData<Report> = Transformations.switchMap(factory.presentasiLiveData,
            PresentasiDataSource::report)

    fun getStateFilter() : LiveData<State> = Transformations.switchMap(factory.presentasiLiveData,
            PresentasiDataSource::state)

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