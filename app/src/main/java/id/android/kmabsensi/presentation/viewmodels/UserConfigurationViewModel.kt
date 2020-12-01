package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.UpdateSdmShiftConfigParam
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.UserConfigurationRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class UserConfigurationViewModel(
    private val preferencesHelper: PreferencesHelper,
    val repository: UserConfigurationRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val sdmShiftConfigurationResult by lazy {
        MutableLiveData<UiState<UserConfigurationResponse>>()
    }

    val updateSdmShiftResult by lazy {
        MutableLiveData<UiState<SingleSdmShiftConfigResponse>>()
    }

    fun getsSmShiftConfiguration(userManagementId: Int){
        sdmShiftConfigurationResult.value = UiState.Loading()
        compositeDisposable.add(repository.getSdmShiftConfiguration(userManagementId)
            .with(schedulerProvider)
            .subscribe({
                sdmShiftConfigurationResult.value = UiState.Success(it)
            },{
                sdmShiftConfigurationResult.value = UiState.Error(it)
            }))
    }

    fun updateSdmShiftResult(param: UpdateSdmShiftConfigParam){
        updateSdmShiftResult.value = UiState.Loading()
        compositeDisposable.add(repository.updateSdmShift(param)
            .with(schedulerProvider)
            .subscribe({
                updateSdmShiftResult.value = UiState.Success(it)
            },{
                updateSdmShiftResult.value = UiState.Error(it)
            }))
    }

    fun getUserData(): User {
        val userDara = preferencesHelper.getString(PreferencesHelper.PROFILE_KEY)
        return Gson().fromJson(
            userDara,
            User::class.java
        )
    }

    override fun onError(error: Throwable) {

    }
}