package id.android.kmabsensi.presentation.permission

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPermissionResponse
import id.android.kmabsensi.data.repository.PermissionRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PermissionViewModel(val permissionRepository: PermissionRepository,
                          val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val createPermissionResponse = MutableLiveData<UiState<BaseResponse>>()

    val listPermissionData = MutableLiveData<UiState<ListPermissionResponse>>()

    val approvePermissionResponse = MutableLiveData<UiState<BaseResponse>>()

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

    fun getListPermission(roleId: Int = 0,
                          userManagementId: Int = 0,
                          userId: Int = 0){
        listPermissionData.value = UiState.Loading()
        compositeDisposable.add(permissionRepository.getListPermission(roleId, userManagementId, userId)
            .with(schedulerProvider)
            .subscribe({
                listPermissionData.value = UiState.Success(it)
            },{
                listPermissionData.value = UiState.Error(it)
            }))

    }

    fun approvePermission(permissionId: Int,
                          status: Int) {
        approvePermissionResponse.value = UiState.Loading()
        compositeDisposable.add(permissionRepository.approvePermission(permissionId, status)
            .with(schedulerProvider)
            .subscribe({
                approvePermissionResponse.value = UiState.Success(it)
            },{
                approvePermissionResponse.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {
        createPermissionResponse.value = UiState.Error(error)
        Crashlytics.log(error.message)
    }
}