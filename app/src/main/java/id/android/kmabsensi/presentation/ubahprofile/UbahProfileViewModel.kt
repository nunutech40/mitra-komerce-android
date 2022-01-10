package id.android.kmabsensi.presentation.ubahprofile

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.SingleUserResponse
import id.android.kmabsensi.data.remote.response.komship.SingleUserResponse2
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBody
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UbahProfileViewModel(val sdmRepository: SdmRepository,
                           val prefHelper: PreferencesHelper,
                           val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val crudResponse = MutableLiveData<UiState<SingleUserResponse>>()
    val crudResponseStaff = MutableLiveData<UiState<SingleUserResponse2>>()
    val crudResponseTalent = MutableLiveData<UiState<SingleUserResponse2>>()

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
        bankAccoundId: String,
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
            bankAccoundId.createRequestBodyText(),
            bankName.createRequestBodyText(),
            bankNo.createRequestBodyText(),
            bankOwnerName.createRequestBodyText()
        )
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
//                prefHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data))
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    fun updateKaryawanStaff(
        id: Int,
        method: String,
        username: String,
        email: String,
        fullName: String,
        divisionId: String,
        officeId: String,
        positionId: String,
        noHp: String,
        address: String,
        birthDate: String,
        gender: String,
        status: Int,
        districtId: Int,
        photoProfileFile: File?,
        joinDate: String,
        martialStatus: String,
        bankCode: String,
        bankName: String,
        bankNo: String,
        bankOwnerName: String
    ){
        var photoProfile : MultipartBody.Part? = null

        photoProfileFile?.let{
            val imageReq = photoProfileFile.createRequestBody()
            photoProfile = MultipartBody.Part.createFormData("photo_profile_url", photoProfileFile.name, imageReq)
        }

        crudResponseStaff.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.updateSdmStaff(
            id,
            method.createRequestBodyText(),
            username.createRequestBodyText(),
            email.createRequestBodyText(),
            fullName.createRequestBodyText(),
            divisionId.createRequestBodyText(),
            officeId.createRequestBodyText(),
            positionId.createRequestBodyText(),
            noHp.createRequestBodyText(),
            address.createRequestBodyText(),
            birthDate.createRequestBodyText(),
            gender.createRequestBodyText(),
            status.toString().createRequestBodyText(),
            districtId.toString().createRequestBodyText(),
            photoProfile,
            joinDate.createRequestBodyText(),
            martialStatus.createRequestBodyText(),
            bankCode.createRequestBodyText(),
            bankName.createRequestBodyText(),
            bankNo.createRequestBodyText(),
            bankOwnerName.createRequestBodyText()
        )
            .with(schedulerProvider)
            .subscribe({
                crudResponseStaff.value = UiState.Success(it)
//                prefHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data))
            },{
                crudResponseStaff.value = UiState.Error(it)
            }))
    }

    fun updateKaryawanTalent(
        id: Int,
        method: String,
        username: String,
        email: String,
        fullName: String,
        divisionId: String,
        officeId: String,
        positionId: String,
        noHp: String,
        address: String,
        birthDate: String,
        gender: String,
        status: String,
        districtId: Int,
        photoProfileFile: File?,
        joinDate: String,
        martialStatus: String,
        bankCode: String,
        bankName: String,
        bankNo: String,
        bankOwnerName: String
    ){
        var photoProfile : MultipartBody.Part? = null

        photoProfileFile?.let{
            val imageReq = photoProfileFile.createRequestBody()
            photoProfile = MultipartBody.Part.createFormData("photo_profile_url", photoProfileFile.name, imageReq)
        }

        crudResponseTalent.value = UiState.Loading()
        compositeDisposable.add(sdmRepository.updateSdmTalent(
            id,
            method.createRequestBodyText(),
            username.createRequestBodyText(),
            email.createRequestBodyText(),
            fullName.createRequestBodyText(),
            divisionId.createRequestBodyText(),
            officeId.createRequestBodyText(),
            positionId.createRequestBodyText(),
            noHp.createRequestBodyText(),
            address.createRequestBodyText(),
            birthDate.createRequestBodyText(),
            gender.createRequestBodyText(),
            status.createRequestBodyText(),
            districtId.toString().createRequestBodyText(),
            photoProfile,
            joinDate.createRequestBodyText(),
            martialStatus.createRequestBodyText(),
            bankCode.createRequestBodyText(),
            bankName.createRequestBodyText(),
            bankNo.createRequestBodyText(),
            bankOwnerName.createRequestBodyText()
        )
            .with(schedulerProvider)
            .subscribe({
                crudResponseTalent.value = UiState.Success(it)
//                prefHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data))
            },{
                crudResponseTalent.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}