package id.android.kmabsensi.presentation.login

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.SingleUserResponse
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


    val loginState = MutableLiveData<UiState<UserResponse>>()

    fun login(
        usernameEmail: String,
        password: String
    ) {
        loginState.value = UiState.Loading()
        compositeDisposable.add(authRepository.login(usernameEmail, password)
            .flatMap {
                prefHelper.saveString(PreferencesHelper.ACCESS_TOKEN_KEY, "Bearer ${it.access_token}")
                userRepository.getProfileUser("Bearer ${it.access_token}", it.user_id)
            }
            .with(schedulerProvider)
            .subscribe({
                prefHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data[0]))
                prefHelper.saveBoolean(PreferencesHelper.IS_LOGIN, true)
                loginState.value = UiState.Success(it)
            }, this::onError)
        )

    }

    fun getIsLogin() = prefHelper.getBoolean(PreferencesHelper.IS_LOGIN)
    
    override fun onError(error: Throwable) {
        loginState.value = UiState.Error(error)
    }
}