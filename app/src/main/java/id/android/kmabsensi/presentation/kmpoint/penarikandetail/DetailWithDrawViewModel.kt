package id.android.kmabsensi.presentation.kmpoint.penarikandetail

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.remote.response.kmpoint.DetailWithdrawResponse
import id.android.kmabsensi.data.remote.response.kmpoint.UpdateWithdrawResponse
import id.android.kmabsensi.data.remote.response.kmpoint.UploadAttachmentResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBody
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.kmabsensi.utils.rx.SchedulerProvider
import okhttp3.MultipartBody
import java.io.File

class DetailWithDrawViewModel(
        val kmPointRepository: KmPoinRepository,
        val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    fun getDetalWithdraw(id : Int): MutableLiveData<UiState<DetailWithdrawResponse>>
    = kmPointRepository.getDetailWithdraw(compositeDisposable, id)

    fun updateStatusWithdraw(id: Int, status: String? = "completed"): MutableLiveData<UiState<UpdateWithdrawResponse>>
    = kmPointRepository.updateStatusWithdraw(
            compositeDisposable,
            id = id,
            status = status!!)

    fun uploadAttachment(
            reference_id: Int,
            attachment_type: String? = "Kmpoin WIthdrawal",
            attachmentFile: File?
    ): MutableLiveData<UiState<UploadAttachmentResponse>>{
        var attachment : MultipartBody.Part? = null
        attachmentFile?.let {
            val image = attachmentFile.createRequestBody()
            attachment = MultipartBody.Part.createFormData("attachment_file", attachmentFile.name, image)
        }
        return kmPointRepository.uploadAttachment(schedulerProvider, compositeDisposable, reference_id.toString().createRequestBodyText(), attachment_type!!.createRequestBodyText(), attachment)
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}