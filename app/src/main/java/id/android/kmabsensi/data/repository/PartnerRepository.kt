package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPartnerResponse
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
        bankName: RequestBody,
        bankNo: RequestBody,
        bankOwnerName: RequestBody,
        partnerCategoryId: RequestBody,
        partnerCategoryName: RequestBody,
        provinceCode: RequestBody,
        provinceName: RequestBody,
        cityCode: RequestBody,
        cityName: RequestBody
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
            bankName,
            bankNo,
            bankOwnerName,
            partnerCategoryId,
            partnerCategoryName,
            provinceCode,
            provinceName,
            cityCode,
            cityName
        )
    }

    fun getPartners(): Single<ListPartnerResponse> {
        return apiService.getPartners()
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
        bankAccountId: RequestBody,
        bankName: RequestBody,
        bankNo: RequestBody,
        bankOwnerName: RequestBody,
        partnerCategoryId: RequestBody,
        partnerCategoryName: RequestBody,
        provinceCode: RequestBody,
        provinceName: RequestBody,
        cityCode: RequestBody,
        cityName: RequestBody
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
            bankAccountId,
            bankName,
            bankNo,
            bankOwnerName,
            partnerCategoryId,
            partnerCategoryName,
            provinceCode,
            provinceName,
            cityCode,
            cityName
        )
    }

    fun deletePartner(userId: Int): Single<BaseResponse>{
        return apiService.deletePartner(userId)
    }

}