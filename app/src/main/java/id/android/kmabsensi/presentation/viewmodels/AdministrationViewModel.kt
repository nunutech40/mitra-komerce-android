package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListAdministrationResponse
import id.android.kmabsensi.data.repository.AdministrationRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBody
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import okhttp3.MultipartBody
import java.io.File

class AdministrationViewModel(
    private val administrationRepository: AdministrationRepository,
    val schedulerProvider: SchedulerProvider
): BaseViewModel(){

    val administrations by lazy {
        MutableLiveData<UiState<ListAdministrationResponse>>()
    }

    val crudResult by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    fun getListAdministration(){
        administrations.value = UiState.Loading()
        compositeDisposable.add(administrationRepository.getListAdministration()
            .with(schedulerProvider)
            .subscribe({
                administrations.value = UiState.Success(it)
            },{
                administrations.value = UiState.Error(it)
            }))
    }

    fun addAdministration(
        title: String,
        desc: String,
        positionId: String,
        attachment: File?
    ){
        var attachmentFile: MultipartBody.Part? = null

        attachment?.let {
            val attachment = it.createRequestBody()
            attachmentFile = MultipartBody.Part.createFormData("attachment", it.name, attachment)
        }

        crudResult.value = UiState.Loading()
        compositeDisposable.add(administrationRepository.addAdministration(
            title.createRequestBodyText(),
            desc.createRequestBodyText(),
            positionId.createRequestBodyText(),
            attachmentFile
        )
            .with(schedulerProvider)
            .subscribe({
                crudResult.value = UiState.Success(it)
            },{
                crudResult.value = UiState.Error(it)
            }))
    }

    fun editAdministration(
        id: String,
        title: String,
        desc: String,
        positionId: String,
        attachment: File?
    ){
        var attachmentFile: MultipartBody.Part? = null

        attachment?.let {
            val attachment = it.createRequestBody()
            attachmentFile = MultipartBody.Part.createFormData("attachment", it.name, attachment)
        }

        crudResult.value = UiState.Loading()
        compositeDisposable.add(administrationRepository.editAdministration(
            id.createRequestBodyText(),
            title.createRequestBodyText(),
            desc.createRequestBodyText(),
            positionId.createRequestBodyText(),
            attachmentFile
        )
            .with(schedulerProvider)
            .subscribe({
                crudResult.value = UiState.Success(it)
            },{
                crudResult.value = UiState.Error(it)
            }))
    }

    fun deleteAdministrationData(id: Int){
        crudResult.value = UiState.Loading()
        compositeDisposable.add(administrationRepository.deleteAdministrationData(id)
            .with(schedulerProvider)
            .subscribe({
                crudResult.value = UiState.Success(it)
            },{
                crudResult.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }

}