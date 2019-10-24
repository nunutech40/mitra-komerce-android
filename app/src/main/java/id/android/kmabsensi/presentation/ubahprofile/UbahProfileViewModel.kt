package id.android.kmabsensi.presentation.ubahprofile

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.SingleUserResponse
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UbahProfileViewModel(val sdmRepository: SdmRepository,
                           val prefHelper: PreferencesHelper,
                           val schedulerProvider: SchedulerProvider): BaseViewModel() {

    val crudResponse = MutableLiveData<UiState<SingleUserResponse>>()

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
                prefHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data))
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}