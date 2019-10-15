package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.service.ApiService

class JabatanRepository(val apiService: ApiService) {

    fun addPosition(positionName: String) = apiService.addPosition(positionName)

    fun getPosition() = apiService.listPosition()

    fun editPosition(id: Int, positionName: String) = apiService.editPosition(id, positionName)

    fun deletePosition(positionId: Int) = apiService.deletePosition(positionId)

}