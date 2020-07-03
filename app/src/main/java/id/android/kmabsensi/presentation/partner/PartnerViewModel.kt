package id.android.kmabsensi.presentation.partner

import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.db.entity.City
import id.android.kmabsensi.data.db.entity.Province
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.AreaRepository
import id.android.kmabsensi.data.repository.PartnerRepository
import id.android.kmabsensi.data.repository.UserRepository
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

class PartnerViewModel(
    val partnerRepository: PartnerRepository,
    val areaRepository: AreaRepository,
    val userRepository: UserRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel(){

    val provinces by lazy {
        MutableLiveData<List<Province>>()
    }

    val cities by lazy {
        MutableLiveData<List<City>>()
    }

    val crudResponse by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    val partners by lazy {
        MutableLiveData<UiState<ListPartnerResponse>>()
    }

    val simplePartners by lazy {
        MutableLiveData<UiState<SimplePartnersResponse>>()
    }

    val userManagements by lazy {
        MutableLiveData<UiState<UserResponse>>()
    }

    val sdmByPartner by lazy {
        MutableLiveData<UiState<UserResponse>>()
    }

    fun addPartner(
        noPartner: String,
        username: String,
        status: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        roleId: String = "4",
        fullname: String,
        noHp: String,
        address: String,
        photoProfileUrl: File?,
        birthdate: String,
        gender: String,
        joinDate: String,
        martialStatus: String,
        partnerCategoryId: String,
        partnerCategoryName: String,
        provinceCode: String,
        provinceName: String,
        cityCode: String,
        cityName: String,
        userManagementId: String,
        bonus: String
    ){
        var photoProfile : MultipartBody.Part? = null

        photoProfileUrl?.let{
            val imageReq = it.createRequestBody()
            photoProfile = MultipartBody.Part.createFormData("photo_profile_url", it.name, imageReq)
        }

        crudResponse.value = UiState.Loading()
        compositeDisposable.add(partnerRepository.addPartner(
            noPartner.createRequestBodyText(),
            username.createRequestBodyText(),
            status.createRequestBodyText(),
            email.createRequestBodyText(),
            password.createRequestBodyText(),
            passwordConfirmation.createRequestBodyText(),
            roleId.createRequestBodyText(),
            fullname.createRequestBodyText(),
            noHp.createRequestBodyText(),
            address.createRequestBodyText(),
            photoProfile,
            birthdate.createRequestBodyText(),
            gender.createRequestBodyText(),
            joinDate.createRequestBodyText(),
            martialStatus.createRequestBodyText(),
            partnerCategoryId.createRequestBodyText(),
            partnerCategoryName.createRequestBodyText(),
            provinceCode.createRequestBodyText(),
            provinceName.createRequestBodyText(),
            cityCode.createRequestBodyText(),
            cityName.createRequestBodyText(),
            userManagementId.createRequestBodyText(),
            bonus.createRequestBodyText()
        )
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },::onError))
    }

    fun getPartners(){
        partners.value = UiState.Loading()
        compositeDisposable.add(partnerRepository.getPartners()
            .with(schedulerProvider)
            .subscribe({
                partners.value = UiState.Success(it)
            },{
                partners.value = UiState.Error(it)
            }))
    }

    fun getSimplePartners(){
        simplePartners.value = UiState.Loading()
        compositeDisposable.add(partnerRepository.getSimplePartners()
            .with(schedulerProvider)
            .subscribe({
                simplePartners.value = UiState.Success(it)
            },{
                simplePartners.value = UiState.Error(it)
            }))
    }

    fun editPartner(
        id: String,
        noPartner: String,
        username: String,
        status: String,
        email: String,
        roleId: String = "4",
        fullname: String,
        noHp: String,
        address: String,
        photoProfileUrl: File?,
        birthdate: String,
        gender: String,
        joinDate: String,
        martialStatus: String,
        partnerCategoryId: String,
        partnerCategoryName: String,
        provinceCode: String,
        provinceName: String,
        cityCode: String,
        cityName: String,
        userManagementId: String,
        bonus: String
    ){

        var photoProfile : MultipartBody.Part? = null

        photoProfileUrl?.let{
            val imageReq = it.createRequestBody()
            photoProfile = MultipartBody.Part.createFormData("photo_profile_url", it.name, imageReq)
        }

        crudResponse.value = UiState.Loading()
        compositeDisposable.add(partnerRepository.editPartner(
            id.createRequestBodyText(),
            noPartner.createRequestBodyText(),
            username.createRequestBodyText(),
            status.createRequestBodyText(),
            email.createRequestBodyText(),
            roleId.createRequestBodyText(),
            fullname.createRequestBodyText(),
            noHp.createRequestBodyText(),
            address.createRequestBodyText(),
            photoProfile,
            birthdate.createRequestBodyText(),
            gender.createRequestBodyText(),
            joinDate.createRequestBodyText(),
            martialStatus.createRequestBodyText(),
            partnerCategoryId.createRequestBodyText(),
            partnerCategoryName.createRequestBodyText(),
            provinceCode.createRequestBodyText(),
            provinceName.createRequestBodyText(),
            cityCode.createRequestBodyText(),
            cityName.createRequestBodyText(),
            userManagementId.createRequestBodyText(),
            bonus.createRequestBodyText()
        )
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },::onError))
    }

    fun deletePartner(userId: Int){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(partnerRepository.deletePartner(userId)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },::onError))
    }

    fun getProvinces(){
        compositeDisposable.add(areaRepository.getProvince()
            .with(schedulerProvider)
            .subscribe({
                provinces.value = it
            }, ::onError))
    }

    fun getCities(code: String){
        compositeDisposable.add(areaRepository.getCityByKodeWilayah(code)
            .with(schedulerProvider)
            .subscribe({
                cities.value = it
            }, ::onError))
    }

    fun getUserManagement(roleId: Int = 2){
        compositeDisposable.add(userRepository.getUserByRole(roleId)
            .with(schedulerProvider)
            .subscribe({
                userManagements.value = UiState.Success(it)
            },{
                userManagements.value = UiState.Error(it)
            }))
    }


    fun getSdmByPartner(noPartner: Int){
        sdmByPartner.value = UiState.Loading()
        compositeDisposable.add(userRepository.getUserByPartner(noPartner)
            .with(schedulerProvider)
            .subscribe({
                sdmByPartner.value = UiState.Success(it)
            },{
                sdmByPartner.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {
        crudResponse.value = UiState.Error(error)
        Timber.e((error))
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }

}