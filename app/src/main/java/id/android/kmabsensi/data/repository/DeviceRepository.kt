package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.FilterDeviceParams
import id.android.kmabsensi.data.remote.service.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DeviceRepository(val apiService: ApiService) {

    fun getListDevice() = apiService.getListDevice()

    fun filterDevice(filterDeviceParams: FilterDeviceParams) =
        apiService.filterDevice(filterDeviceParams)

    fun addDevice(
        deviceType: RequestBody,
        brancd: RequestBody,
        spesification: RequestBody,
        noPartner: RequestBody,
        userSdmId: RequestBody,
        device_pick_date: RequestBody,
        attachment_1: MultipartBody.Part?,
        attachment_2: MultipartBody.Part?,
        attachment_3: MultipartBody.Part?
    ) = apiService.addDevice(
        deviceType,
        brancd,
        spesification,
        noPartner,
        userSdmId,
        device_pick_date,
        attachment_1,
        attachment_2,
        attachment_3
    )

    fun editDevice(
        id: RequestBody,
        deviceType: RequestBody,
        brancd: RequestBody,
        spesification: RequestBody,
        noPartner: RequestBody,
        userSdmId: RequestBody,
        device_pick_date: RequestBody,
        attachment_1: MultipartBody.Part?,
        attachment_2: MultipartBody.Part?,
        attachment_3: MultipartBody.Part?
    ) = apiService.editDevice(
        id,
        deviceType,
        brancd,
        spesification,
        noPartner,
        userSdmId,
        device_pick_date,
        attachment_1,
        attachment_2,
        attachment_3
    )

    fun deleteDevice(id: Int) = apiService.deleteDevice(id)


}