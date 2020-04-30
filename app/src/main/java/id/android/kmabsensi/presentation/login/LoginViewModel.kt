package id.android.kmabsensi.presentation.login

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.LoginResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.AreaRepository
import id.android.kmabsensi.data.repository.AuthRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class LoginViewModel(
    val authRepository: AuthRepository,
    val userRepository: UserRepository,
    val areaRepository: AreaRepository,
    val prefHelper: PreferencesHelper,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {


    val loginState = MutableLiveData<UiState<LoginResponse>>()
    val userProfileData = MutableLiveData<UiState<UserResponse>>()

    fun login(
        usernameEmail: String,
        password: String
    ) {
        val fcmToken = prefHelper.getString(PreferencesHelper.FCM_TOKEN)
        loginState.value = UiState.Loading()
        compositeDisposable.add(
            authRepository.login(usernameEmail, password, if(fcmToken == "") "fcmTokenEmpty" else fcmToken)
                .with(schedulerProvider)
                .subscribe({
                    loginState.value = UiState.Success(it)
                    if (it.message == null){
                        prefHelper.saveString(PreferencesHelper.ACCESS_TOKEN_KEY, it.access_token)
                        getUserProfile(it.user_id)
                        getDataArea()
                    }
                }, this::onError)
        )
    }

    private fun getDataArea(){
        compositeDisposable.add(
            areaRepository.getDataArea()
                .with(schedulerProvider)
                .subscribe({},{
                    Timber.e((it))
                    Crashlytics.log(it.message)
                }))
    }

    private fun getUserProfile(userId: Int) {
        userProfileData.value = UiState.Loading()
        compositeDisposable.add(userRepository.getProfileUser(userId)
            .with(schedulerProvider)
            .subscribe({
                prefHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data[0]))
                prefHelper.saveBoolean(PreferencesHelper.IS_LOGIN, true)
                userProfileData.value = UiState.Success(it)
            }, {
                userProfileData.value = UiState.Error(it)
            })
        )
    }

    fun getIsLogin() = prefHelper.getBoolean(PreferencesHelper.IS_LOGIN)

    override fun onError(error: Throwable) {
        loginState.value = UiState.Error(error)
        Crashlytics.log(error.message)
    }
}