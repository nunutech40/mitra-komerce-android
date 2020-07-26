package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.service.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AdministrationRepository(val apiService: ApiService) {

    fun getListAdministration() = apiService.getListAdministration()

    fun addAdministration(
        title: RequestBody,
        desc: RequestBody,
        positionId: RequestBody,
        attachment: MultipartBody.Part?
    ) =
        apiService.addAdministrationData(
            title,
            desc, positionId, attachment
        )

    fun editAdministration(
        id: RequestBody,
        title: RequestBody,
        desc: RequestBody,
        positionId: RequestBody,
        attachment: MultipartBody.Part?
    ) =
        apiService.editAdministrationData(
            id,
            title,
            desc, positionId, attachment
        )

    fun deleteAdministrationData(id: Int) = apiService.deleteAdministrationData(id)

}