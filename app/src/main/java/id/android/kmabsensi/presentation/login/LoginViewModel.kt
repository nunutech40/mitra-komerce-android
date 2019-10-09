package id.android.kmabsensi.presentation.login

import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber.d
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.LoginResponse
import id.android.kmabsensi.data.remote.response.SingleUserResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.AuthRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class LoginViewModel(
    val authRepository: AuthRepository,
    val userRepository: UserRepository,
    val prefHelper: PreferencesHelper,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {


    val loginState = MutableLiveData<UiState<LoginResponse>>()
    val userProfileData = MutableLiveData<UiState<UserResponse>>()

    fun login(
        usernameEmail: String,
        password: String
    ) {
        loginState.value = UiState.Loading()
        compositeDisposable.add(
            authRepository.login(usernameEmail, password)
                .with(schedulerProvider)
                .subscribe({
                    loginState.value = UiState.Success(it)
                    if (it.message == null){
                        prefHelper.saveString(PreferencesHelper.ACCESS_TOKEN_KEY, it.access_token)
                        getUserProfile(it.user_id)
                    }
                }, this::onError)
        )
    }

    fun getUserProfile(userId: Int) {
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
    }
}