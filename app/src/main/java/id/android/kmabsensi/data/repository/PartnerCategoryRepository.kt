package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPartnerCategoryResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class PartnerCategoryRepository(val apiService: ApiService){

    fun addPartnerCategory(partnerCategory: String): Single<BaseResponse> {
        return apiService.addPartnerCategory(partnerCategory)
    }

    fun editPartnerCategory(id: Int,
                            partnerCategory: String): Single<BaseResponse>{
        return apiService.editPartnerCategory(id, partnerCategory)
    }

    fun deletePartnerCategory(id: Int): Single<BaseResponse>{
        return apiService.deletePartnerCategory(id)
    }

    fun getPartnerCategories(): Single<ListPartnerCategoryResponse>{
        return apiService.getPartnerCategories()
    }

}