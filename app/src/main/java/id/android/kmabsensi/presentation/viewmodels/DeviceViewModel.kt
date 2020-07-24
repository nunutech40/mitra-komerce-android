package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListDeviceResponse
import id.android.kmabsensi.data.repository.DeviceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBody
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import okhttp3.MultipartBody
import java.io.File

class DeviceViewModel(
    val deviceRepository: DeviceRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val devices by lazy {
        MutableLiveData<UiState<ListDeviceResponse>>()
    }

    val crudResult by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    fun getListDevice() {
        devices.value = UiState.Loading()
        compositeDisposable.add(deviceRepository.getListDevice()
            .with(schedulerProvider)
            .subscribe({
                devices.value = UiState.Success(it)
            }, {
                devices.value = UiState.Error(it)
            })
        )
    }

    fun addDevice(
        deviceType: String,
        brancd: String,
        spesification: String,
        noPartner: String,
        userSdmId: String,
        devicePickDate: String,
        attachment1: File?,
        attachment2: File?,
        attachment3: File?
    ) {
        var attachment1File: MultipartBody.Part? = null
        var attachment2File: MultipartBody.Part? = null
        var attachment3File: MultipartBody.Part? = null

        attachment1?.let {
            val imageReq = it.createRequestBody()
            attachment1File = MultipartBody.Part.createFormData("attachment_1", it.name, imageReq)
        }
        attachment2?.let {
            val imageReq = it.createRequestBody()
            attachment2File = MultipartBody.Part.createFormData("attachment_2", it.name, imageReq)
        }
        attachment3?.let {
            val imageReq = it.createRequestBody()
            attachment3File = MultipartBody.Part.createFormData("attachment_3", it.name, imageReq)
        }

        crudResult.value = UiState.Loading()
        compositeDisposable.add(
            deviceRepository.addDevice(
                deviceType = deviceType.createRequestBodyText(),
                brancd = brancd.createRequestBodyText(),
                spesification = spesification.createRequestBodyText(),
                noPartner = noPartner.createRequestBodyText(),
                userSdmId = userSdmId.createRequestBodyText(),
                device_pick_date = devicePickDate.createRequestBodyText(),
                attachment_1 = attachment1File,
                attachment_2 = attachment2File,
                attachment_3 = attachment3File
            ).with(schedulerProvider)
                .subscribe({
                    crudResult.value = UiState.Success(it)
                }, {
                    crudResult.value = UiState.Error(it)
                })
        )

    }

    override fun onError(error: Throwable) {

    }
}