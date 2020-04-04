package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.SingleUserResponse
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
            joinDate, martialStatus, bankName, bankNo, bankOwnerName)
    }

    fun deleteKaryawan(userId: Int) = apiService.deleteSdm(userId)

}