package id.android.kmabsensi.presentation.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.AllBankResponse
import id.android.kmabsensi.data.remote.response.LoginResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.AreaRepository
import id.android.kmabsensi.data.repository.AuthRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import java.lang.Exception

class LoginViewModel(
    val authRepository: AuthRepository,
    val userRepository: UserRepository,
    val areaRepository: AreaRepository,
    val prefHelper: PreferencesHelper,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {


    val loginState = MutableLiveData<UiState<LoginResponse>>()
    val userProfileData = MutableLiveData<UiState<UserResponse>>()
    val allBankData = MutableLiveData<UiState<AllBankResponse>>()
    var userId: Int ? =null

    fun login(
        usernameEmail: String,
        password: String,
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
//                        getAllBank()
                        getDataArea()
                    }
                },
                        this::onError)
        )
    }


    private fun getDataArea(){
        compositeDisposable.add(
            areaRepository.getDataArea()
                .with(schedulerProvider)
                .subscribe({},{
                    Timber.e((it))
                    it.message?.let { FirebaseCrashlytics.getInstance().log(it) }
                }))
    }

    private fun getUserProfile(userId: Int) {
        userProfileData.value = UiState.Loading()
        compositeDisposable.add(userRepository.getProfileUser(userId)
            .with(schedulerProvider)
            .subscribe({
                prefHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data[0]))
                toInteger(it.data[0].id.toString())
                prefHelper.saveInt(PreferencesHelper.ID_USER.toString(), userId)
                Log.d("Cek id", "getUserProfile: ${it.data[0]}")
                prefHelper.saveBoolean(PreferencesHelper.IS_LOGIN, true)
                userProfileData.value = UiState.Success(it)
            }, {
                userProfileData.value = UiState.Error(it)
            })
        )
    }


    private fun getAllBank(){
        try {
            allBankData.value = UiState.Loading()
            compositeDisposable.add(
                userRepository.getAllBank()
                .with(schedulerProvider)
                .subscribe({
                    prefHelper.saveString(PreferencesHelper.ALL_BANK, Gson().toJson(it.data))
                    allBankData.value = UiState.Success(it)
                }, {
                    allBankData.value = UiState.Error(it)
                }
                )
            )
        } catch (e: Exception){
            allBankData.value = UiState.Error(e)
        }

    }


    fun getAllBankData(): AllBankResponse {
        val userData = prefHelper.getString(PreferencesHelper.ALL_BANK)
        return Gson().fromJson(userData, AllBankResponse::class.java)
    }

    fun toInteger(s: String) {
        try {
            userId = s.toInt()
        } catch (ex: NumberFormatException) {
            ex.printStackTrace()
        }
    }
    fun getIsLogin() = prefHelper.getBoolean(PreferencesHelper.IS_LOGIN)

    override fun onError(error: Throwable) {
        loginState.value = UiState.Error(error)
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}