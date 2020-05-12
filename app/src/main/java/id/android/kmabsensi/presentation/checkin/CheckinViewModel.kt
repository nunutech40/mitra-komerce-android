package id.android.kmabsensi.presentation.checkin

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.CheckinResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.repository.PresenceRepository
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

class CheckinViewModel(val presenceRepository: PresenceRepository,
                       val preferencesHelper: PreferencesHelper,
                       val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val checkInResponse = MutableLiveData<UiState<CheckinResponse>>()

    val reportAbsenResponse = MutableLiveData<UiState<BaseResponse>>()

    fun checkIn(file: File, ontimeLevel: Int){
        val imageReq = file.createRequestBody()
        val photo = MultipartBody.Part.createFormData("file", file.name, imageReq)

        checkInResponse.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.checkIn(photo, ontimeLevel.toString().createRequestBodyText())
            .with(schedulerProvider)
            .subscribe({
                checkInResponse.value = UiState.Success(it)
            },{
                checkInResponse.value = UiState.Error(it)
            }))
    }

    fun checkOut(absenId: Int,file: File){
        val imageReq = file.createRequestBody()
        val photo = MultipartBody.Part.createFormData("file", file.name, imageReq)

        checkInResponse.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.checkOut(absenId, photo)
            .with(schedulerProvider)
            .subscribe({
                checkInResponse.value = UiState.Success(it)
            },{
                checkInResponse.value = UiState.Error(it)
            }))
    }

    fun reportAbsen(
        userId: Int,
        presenceDate: String,
        description: String
    ) {
       reportAbsenResponse.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.reportAbsen(userId, presenceDate, description)
            .with(schedulerProvider)
            .subscribe({
                reportAbsenResponse.value = UiState.Success(it)
            },{
                reportAbsenResponse.value = UiState.Error(it)
            }))
    }

    fun getUserData(): User {
        return Gson().fromJson<User>(
            preferencesHelper.getString(PreferencesHelper.PROFILE_KEY),
            User::class.java
        )
    }

    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
    }
}