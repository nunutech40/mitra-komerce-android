package id.android.kmabsensi.presentation.tidakhadir

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.repository.PermissionRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PermissionViewModel(val permissionRepository: PermissionRepository,
                          val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val createPermissionResponse = MutableLiveData<UiState<BaseResponse>>()

    fun createPermission(
        permissionType: Int,
        userId: Int,
        officeId: Int,
        roleId: Int,
        userManagementId: Int,
        status: Int,
        explanation: String,
        dateFrom: String,
        dateTo: String,
        file: File
    ){
        val imageReq = RequestBody.create(MediaType.parse("image/*"), file)
        val photo = MultipartBody.Part.createFormData("file", file.name, imageReq)
        createPermissionResponse.value = UiState.Loading()
        compositeDisposable.add(permissionRepository.createPermission(
            permissionType.toString().createRequestBodyText(),
            userId.toString().createRequestBodyText(),
            officeId.toString().createRequestBodyText(),
            roleId.toString().createRequestBodyText(),
            userManagementId.toString().createRequestBodyText(),
            status.toString().createRequestBodyText(),
            explanation.createRequestBodyText(),
            dateFrom.createRequestBodyText(),
            dateTo.createRequestBodyText(),
            photo
        )
            .with(schedulerProvider)
            .subscribe({
                createPermissionResponse.value = UiState.Success(it)
            }, this::onError))

    }

    override fun onError(error: Throwable) {
        createPermissionResponse.value = UiState.Error(error)
    }
}