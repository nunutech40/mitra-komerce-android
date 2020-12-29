package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.*
import id.android.kmabsensi.data.remote.response.SingleUserResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SdmRepository(val apiService: ApiService) {

    fun addSdm(
        username: RequestBody,
        email: RequestBody,
        password: RequestBody,
        password_confirmation: RequestBody,
        role_id: RequestBody,
        full_name: RequestBody,
        npk: RequestBody,
        division_id: RequestBody,
        office_id: RequestBody,
        position_id: RequestBody,
        no_partner: RequestBody,
        originVillage: RequestBody,
        noHp: RequestBody,
        address: RequestBody,
        birthDate: RequestBody,
        gender: RequestBody,
        userManagementId: RequestBody,
        status: RequestBody,
        photoProfileUrl: MultipartBody.Part?,
        joinDate: RequestBody,
        martialStatus: RequestBody,
        bankName: RequestBody,
        bankNo: RequestBody,
        bankOwnerName: RequestBody
    ): Single<SingleUserResponse> {
        return apiService.addSdm(username, email, password, password_confirmation, role_id, full_name,
            npk, division_id, office_id, position_id, no_partner, originVillage, noHp, address, birthDate,
            gender, userManagementId, status, photoProfileUrl, joinDate, martialStatus, bankName, bankNo, bankOwnerName)
    }

    fun updateSdm(
        id: RequestBody,
        username: RequestBody,
        email: RequestBody,
        role_id: RequestBody,
        full_name: RequestBody,
        division_id: RequestBody,
        office_id: RequestBody,
        position_id: RequestBody,
        no_partner: RequestBody,
        originVillage: RequestBody,
        noHp: RequestBody,
        address: RequestBody,
        birthDate: RequestBody,
        gender: RequestBody,
        userManagementId: RequestBody,
        status: RequestBody,
        photoProfileUrl: MultipartBody.Part?,
        joinDate: RequestBody,
        martialStatus: RequestBody,
        bankAccoutId: RequestBody,
        bankName: RequestBody,
        bankNo: RequestBody,
        bankOwnerName: RequestBody
    ): Single<SingleUserResponse>{
        return apiService.editSdm(id, username, email, role_id, full_name, division_id, office_id,
            position_id,
            no_partner,
            originVillage,
            noHp,
            address,
            birthDate,
            gender,
            userManagementId,
            status,
            photoProfileUrl,
            joinDate, martialStatus, bankAccoutId, bankName, bankNo, bankOwnerName)
    }

    fun deleteKaryawan(userId: Int) = apiService.deleteSdm(userId)

    fun getSdmNonJob() = apiService.getSdmNonJob()

    fun getSdmReports() = apiService.getSdmReport()

    fun filterSdmReports(params: FilterSdmReportParams) = apiService.filterSdmReport(params)

    fun filterCsReportSummary(params: FilterSdmReportParams) = apiService.filterCsReportSummary(params)

    fun addSdmReport(params: AddSdmReportParams) = apiService.addSdmReport(params)

    fun editSdmReport(params: EditSdmReportParams) = apiService.editSdmReport(params)

    fun deleteSdmReport(id: Int) = apiService.deleteSdmReport(id)

    // ---- advertiser ---- //
    fun getAdvertiserReports() = apiService.getAdvertiserReports()
    fun addAdvertiserReport(params: AddAdvertiserReportParams) = apiService.addAdvertiserReport(params)
    fun editAdvertiserReport(params: EditAdvertiserReportParams) = apiService.editAdvertiserReport(params)
    fun deleteAdvertiserReport(id: Int) = apiService.deleteAdvertiserReport(id)
    fun filterAdvertiserReport(params: FilterSdmReportParams) = apiService.filterAdvertiserReport(params)
    fun filterAdvertiserReportSummary(params: FilterSdmReportParams) = apiService.filterAdvertiserReportSummary(params)
    //---- advertiser ----//

    fun getSdm(roleId: Int = 3, userManagementId: Int = 0, officeId : Int = 0, positionId: Int = 0, noPartner : Int = 0): Single<UserResponse>{
        return apiService.getUser(roleId, userManagementId, officeId, positionId, noPartner)
    }

}