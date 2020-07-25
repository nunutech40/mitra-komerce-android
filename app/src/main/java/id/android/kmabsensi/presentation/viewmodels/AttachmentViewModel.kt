package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.repository.AttachmentRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class AttachmentViewModel(
    val attachmentRepository: AttachmentRepository,
    val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val deleteResult by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    fun deleteAttachment(id: Int){
        deleteResult.value = UiState.Loading()
        compositeDisposable.add(attachmentRepository.deleteAttachment(id)
            .with(schedulerProvider)
            .subscribe({
                deleteResult.value = UiState.Success(it)
            },{
                deleteResult.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }

}