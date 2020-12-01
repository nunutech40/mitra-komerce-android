package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.WorkConfigParams
import id.android.kmabsensi.data.remote.service.ApiService

class WorkConfigRepository(val apiService: ApiService){

    fun updateWorkConfig(workConfigParams: WorkConfigParams) =
        apiService.updateWorkConfig(workConfigParams)

    fun getWorkConfig() =
        apiService.getWorkConfig()

}