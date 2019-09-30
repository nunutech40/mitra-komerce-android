package id.android.kmabsensi.presentation.checkin

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.CheckinResponse
import id.android.kmabsensi.data.repository.PresenceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CheckinViewModel(val presenceRepository: PresenceRepository,
                       val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val checkInResponse = MutableLiveData<UiState<CheckinResponse>>()

    fun checkIn(file: File){
        val imageReq = RequestBody.create(MediaType.parse("image/*"), file)
        val photo = MultipartBody.Part.createFormData("file", file.name, imageReq)

        checkInResponse.value = UiState.Loading()
        compositeDisposable.add(presenceRepository.checkIn(photo)
            .with(schedulerProvider)
            .subscribe({
                checkInResponse.value = UiState.Success(it)
            },{
                checkInResponse.value = UiState.Error(it)
            }))
    }

    fun checkOut(absenId: Int,file: File){
        val imageReq = RequestBody.create(MediaType.parse("image/*"), file)
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

    override fun onError(error: Throwable) {

    }
}