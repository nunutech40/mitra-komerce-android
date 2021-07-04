package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.response.kmpoint.CreateShoppingRequestResponse
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse
import id.android.kmabsensi.data.remote.response.kmpoint.UploadAttachmentResponse
import id.android.kmabsensi.data.repository.KmPoinRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createRequestBody
import id.android.kmabsensi.utils.createRequestBodyText
import id.android.kmabsensi.utils.rx.SchedulerProvider
import okhttp3.MultipartBody
import java.io.File

class ShoppingDetailFinanceViewModel(
        val kmPointRepository: KmPoinRepository,
        val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    var shoppingDetail : MutableLiveData<UiState<DetailShoppingResponse>> = MutableLiveData()


    fun getShoppingDetail(id : Int) {
        shoppingDetail = kmPointRepository.shoppingRequestDetail(compositeDisposable, id = id)
    }

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

    fun updateShoppingRequest(id: Int, body : UpdateShoppingRequestParams): MutableLiveData<UiState<CreateShoppingRequestResponse>>
            = kmPointRepository.updateShoppingRequest(compositeDisposable, id = id, body = body)

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}