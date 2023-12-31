package id.android.kmabsensi.presentation.sdm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.*
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBody
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import okhttp3.MultipartBody
import java.io.File

class KelolaDataSdmViewModel(val officeRepository: OfficeRepository,
                             val userRepository: UserRepository,
                             val sdmRepository: SdmRepository,
                             val jabatanRepository: JabatanRepository,
                             val partnerRepository: PartnerRepository,
                             val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val userData = MutableLiveData<UiState<UserResponse>>()
    val officeData = MutableLiveData<UiState<OfficeResponse>>()
    val userManagementData = MutableLiveData<UiState<UserResponse>>()
    val crudResponse = MutableLiveData<UiState<SingleUserResponse>>()
    val positionResponse = MutableLiveData<UiState<ListPositionResponse>>()
    val sdmNonJob = MutableLiveData<UiState<UserResponse>>()
    val partners = MutableLiveData<UiState<ListPartnerResponse>>()

    fun searchUser(keyword: String): LiveData<UiState<UserResponse>> =
        userRepository.searchUser(compositeDisposable, keyword)

    fun filterUser(managementId: Int): LiveData<UiState<UserResponse>> =
        userRepository.filterUser(compositeDisposable, managementId)

    fun getPartners(): LiveData<UiState<ListPartnerResponse>> =
        partnerRepository.getPartnersCoba(compositeDisposable)

    fun getUserDataWithPagedList(roleId: Int,
                                 userManagementId: Int = 0,
                                 leaderId: Int
    ): LiveData<PagedList<User>> =
        userRepository.getDataUser(compositeDisposable,
            roleId = roleId,
            userManagementId = userManagementId,
            leaderId = leaderId)

    fun getState(): LiveData<State> =
        userRepository.getState()


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
        userManagementData.value = UiState.Loading()
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
        photoProfileFile: File?,
        status: Int = 1,
        joinDate: String,
        martialStatus: String,
        bankName: String,
        bankNo: String,
        bankOwnerName: String
    ){
        var photoProfile : MultipartBody.Part? = null

        photoProfileFile?.let{
            val imageReq = photoProfileFile.createRequestBody()
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
            status.toString().createRequestBodyText(),
            photoProfile,
            joinDate.createRequestBodyText(),
            martialStatus.createRequestBodyText(),
            bankName.createRequestBodyText(),
            bankNo.createRequestBodyText(),
            bankOwnerName.createRequestBodyText()
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
        status: Int,
        photoProfileFile: File?,
        joinDate: String,
        martialStatus: String,
        bankAccoutId: String,
        bankName: String,
        bankNo: String,
        bankOwnerName: String
    ){
        var photoProfile : MultipartBody.Part? = null

        photoProfileFile?.let{
            val imageReq = photoProfileFile.createRequestBody()
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
            status.toString().createRequestBodyText(),
            photoProfile,
            joinDate.createRequestBodyText(),
            martialStatus.createRequestBodyText(),
            bankAccoutId.createRequestBodyText(),
            bankName.createRequestBodyText(),
            bankNo.createRequestBodyText(),
            bankOwnerName.createRequestBodyText()
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

    fun getSdmNonJob(){
        sdmNonJob.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.getSdmNonJob()
            .with(schedulerProvider)
            .subscribe({
                sdmNonJob.value = UiState.Success(it)
            },{
                sdmNonJob.value = UiState.Error(it)
            }))

    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}