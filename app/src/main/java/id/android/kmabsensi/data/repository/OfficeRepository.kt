package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.CrudOfficeResponse
import id.android.kmabsensi.data.remote.response.OfficeResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class OfficeRepository(val apiService: ApiService) {

    fun addOffice(
        officeName: String,
        lat: String,
        lng: String,
        address: String,
        pjUserId: Int
    ): Single<CrudOfficeResponse> = apiService.addOffice(officeName, lat, lng, address, pjUserId)

    fun getOffices() = apiService.getOffices()

    fun editOffice(
        id: Int,
        officeName: String,
        lat: String,
        lng: String,
        address: String,
        pjUserId: Int
    ): Single<CrudOfficeResponse> = apiService.editOffice(id, officeName, lat, lng, address, pjUserId)

    fun deleteOffice(officeId: Int) = apiService.deleteOffice(officeId)

}