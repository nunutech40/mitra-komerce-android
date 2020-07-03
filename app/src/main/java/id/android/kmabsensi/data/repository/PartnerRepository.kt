package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPartnerResponse
import id.android.kmabsensi.data.remote.response.SdmOfPartnerResponse
import id.android.kmabsensi.data.remote.response.SimplePartnersResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PartnerRepository(val apiService: ApiService) {

    fun addPartner(
        noPartner: RequestBody,
        username: RequestBody,
        status: RequestBody,
        email: RequestBody,
        password: RequestBody,
        passwordConfirmation: RequestBody,
        roleId: RequestBody,
        fullname: RequestBody,
        noHp: RequestBody,
        address: RequestBody,
        photoProfileUrl: MultipartBody.Part?,
        birthdate: RequestBody,
        gender: RequestBody,
        joinDate: RequestBody,
        martialStatus: RequestBody,
        partnerCategoryId: RequestBody,
        partnerCategoryName: RequestBody,
        provinceCode: RequestBody,
        provinceName: RequestBody,
        cityCode: RequestBody,
        cityName: RequestBody,
        userManagementId: RequestBody,
        bonus: RequestBody
    ): Single<BaseResponse> {
        return apiService.addPartner(
            noPartner,
            username,
            status,
            email,
            password,
            passwordConfirmation,
            roleId,
            fullname,
            noHp,
            address,
            photoProfileUrl,
            birthdate,
            gender,
            joinDate,
            martialStatus,
            partnerCategoryId,
            partnerCategoryName,
            provinceCode,
            provinceName,
            cityCode,
            cityName,
            userManagementId,
            bonus
        )
    }

    fun getPartners(): Single<ListPartnerResponse> {
        return apiService.getPartners()
    }

    fun getSimplePartners(): Single<SimplePartnersResponse>{
        return apiService.getSimplePartners()
    }

    fun editPartner(
        id: RequestBody,
        noPartner: RequestBody,
        username: RequestBody,
        status: RequestBody,
        email: RequestBody,
        roleId: RequestBody,
        fullname: RequestBody,
        noHp: RequestBody,
        address: RequestBody,
        photoProfileUrl: MultipartBody.Part?,
        birthdate: RequestBody,
        gender: RequestBody,
        joinDate: RequestBody,
        martialStatus: RequestBody,
        partnerCategoryId: RequestBody,
        partnerCategoryName: RequestBody,
        provinceCode: RequestBody,
        provinceName: RequestBody,
        cityCode: RequestBody,
        cityName: RequestBody,
        userManagementId: RequestBody,
        bonus: RequestBody
    ): Single<BaseResponse> {
        return apiService.editPartner(
            id,
            noPartner,
            username,
            status,
            email,
            roleId,
            fullname,
            noHp,
            address,
            photoProfileUrl,
            birthdate,
            gender,
            joinDate,
            martialStatus,
            partnerCategoryId,
            partnerCategoryName,
            provinceCode,
            provinceName,
            cityCode,
            cityName,
            userManagementId,
            bonus
        )
    }

    fun deletePartner(userId: Int): Single<BaseResponse>{
        return apiService.deletePartner(userId)
    }

    fun getSdmOfPartner(noPartner: String): Single<SdmOfPartnerResponse>{
        return apiService.getSdmOfPartner(noPartner)
    }
}