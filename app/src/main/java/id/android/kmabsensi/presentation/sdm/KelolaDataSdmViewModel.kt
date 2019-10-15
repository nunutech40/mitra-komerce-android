package id.android.kmabsensi.presentation.sdm

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.ListPositionResponse
import id.android.kmabsensi.data.remote.response.OfficeResponse
import id.android.kmabsensi.data.remote.response.SingleUserResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.JabatanRepository
import id.android.kmabsensi.data.repository.OfficeRepository
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class KelolaDataSdmViewModel(val officeRepository: OfficeRepository,
                             val userRepository: UserRepository,
                             val sdmRepository: SdmRepository,
                             val jabatanRepository: JabatanRepository,
                             val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val userData = MutableLiveData<UiState<UserResponse>>()
    val officeData = MutableLiveData<UiState<OfficeResponse>>()
    val userManagementData = MutableLiveData<UiState<UserResponse>>()
    val crudResponse = MutableLiveData<UiState<SingleUserResponse>>()
    val positionResponse = MutableLiveData<UiState<ListPositionResponse>>()

    fun getDataOffice(){
        compositeDisposable.add(officeRepository.getOffices()
            .with(schedulerProvider)
            .subscribe({
                officeData.value = UiState.Success(it)
            },{
                officeData.value = UiState.Error(it)
            })
        )
    }

    fun getUserManagement(roleId: Int){
        compositeDisposable.add(userRepository.getUserByRole(roleId)
            .with(schedulerProvider)
            .subscribe({
                userManagementData.value = UiState.Success(it)
            },{
                userManagementData.value = UiState.Error(it)
            }))
    }

    fun tambahSdm(
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        roleId: String,
        fullName: String,
        npk: String,
        divisionId: String,
        officeId: String,
        positionId: String,
        noPartner: String,
        originVillage: String,
        noHp: String,
        address: String,
        birthDate: String,
        gender: String,
        userManagementId: String,
        photoProfileFile: File?
    ){
        var photoProfile : MultipartBody.Part? = null

        photoProfileFile?.let{
            val imageReq = RequestBody.create(MediaType.parse("image/*"), photoProfileFile)
            photoProfile = MultipartBody.Part.createFormData("photo_profile_url", photoProfileFile.name, imageReq)
        }

        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.addSdm(
            username.createRequestBodyText(),
            email.createRequestBodyText(),
            password.createRequestBodyText(),
            passwordConfirmation.createRequestBodyText(),
            roleId.createRequestBodyText(),
            fullName.createRequestBodyText(),
            npk.createRequestBodyText(),
            divisionId.createRequestBodyText(),
            officeId.createRequestBodyText(),
            positionId.createRequestBodyText(),
            noPartner.createRequestBodyText(),
            originVillage.createRequestBodyText(),
            noHp.createRequestBodyText(),
            address.createRequestBodyText(),
            birthDate.createRequestBodyText(),
            gender.createRequestBodyText(),
            userManagementId.createRequestBodyText(),
            photoProfile
        )
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun updateKaryawan(
        id: String,
        username: String,
        email: String,
        roleId: String,
        fullName: String,
        divisionId: String,
        officeId: String,
        positionId: String,
        noPartner: String,
        originVillage: String,
        noHp: String,
        address: String,
        birthDate: String,
        gender: String,
        userManagementId: String,
        photoProfileFile: File?
    ){
        var photoProfile : MultipartBody.Part? = null

        photoProfileFile?.let{
            val imageReq = RequestBody.create(MediaType.parse("image/*"), photoProfileFile)
            photoProfile = MultipartBody.Part.createFormData("photo_profile_url", photoProfileFile.name, imageReq)
        }

        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.updateSdm(
            id.createRequestBodyText(),
            username.createRequestBodyText(),
            email.createRequestBodyText(),
            roleId.createRequestBodyText(),
            fullName.createRequestBodyText(),
            divisionId.createRequestBodyText(),
            officeId.createRequestBodyText(),
            positionId.createRequestBodyText(),
            noPartner.createRequestBodyText(),
            originVillage.createRequestBodyText(),
            noHp.createRequestBodyText(),
            address.createRequestBodyText(),
            birthDate.createRequestBodyText(),
            gender.createRequestBodyText(),
            userManagementId.createRequestBodyText(),
            photoProfile
        )
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun deleteKaryawan(userId: Int) {
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.deleteKaryawan(userId)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun getUserData(roleId: Int, userManagementId: Int = 0){
        userData.value = UiState.Loading()
        compositeDisposable.add(userRepository.getUserByRole(roleId, userManagementId)
            .with(schedulerProvider)
            .subscribe({
                userData.value = UiState.Success(it)
            },{
                userData.value = UiState.Error(it)
            }))
    }

    fun getPositions(){
        positionResponse.value = UiState.Loading()
        compositeDisposable.add(jabatanRepository.getPosition()
            .with(schedulerProvider)
            .subscribe({
                positionResponse.value = UiState.Success(it)
            },{
                positionResponse.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}