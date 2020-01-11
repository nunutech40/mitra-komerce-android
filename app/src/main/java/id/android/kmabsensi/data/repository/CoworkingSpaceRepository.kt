package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.response.AddCoworkingSpaceResponse
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class CoworkingSpaceRepository(val apiService: ApiService) {

    fun getCoworkingSpace() = apiService.getCoworkingSpace()

    fun addCoworkingSpace(
        coworkName: String,
        description: String,
        lat: Double,
        lng: Double,
        address: String,
        status: Int,
        slot: Int
    ): Single<AddCoworkingSpaceResponse> {

        val body = mapOf(
            "cowork_name" to coworkName,
            "description" to description,
            "lat" to lat,
            "lng" to lng,
            "address" to address,
            "status" to status,
            "slot" to slot
        )

        return apiService.addCoworkingSpace(body)

    }

    fun editCoworkingSpace(
        id: Int,
        coworkName: String,
        description: String,
        lat: Double,
        lng: Double,
        address: String,
        status: Int,
        slot: Int
    ): Single<AddCoworkingSpaceResponse> {

        val body = mapOf(
            "id" to id,
            "cowork_name" to coworkName,
            "description" to description,
            "lat" to lat,
            "lng" to lng,
            "address" to address,
            "status" to status,
            "slot" to slot
        )

        return apiService.editCoworkingSpace(body)

    }

    fun deleteCoworkingSpace(id: Int): Single<AddCoworkingSpaceResponse>{
        return apiService.deleteCoworkingSpace(id)

    }

}