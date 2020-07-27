package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.ListProductKnowledgeResponse
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.repository.ProductKnowledgeRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class ProductKnowledgeViewModel(
    val productKnowledgeRepository: ProductKnowledgeRepository,
    val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val productKnowledges by lazy {
        MutableLiveData<UiState<ListProductKnowledgeResponse>>()
    }

    fun getListProductKnowledge(noPartner: Int){
        productKnowledges.value = UiState.Loading()
        compositeDisposable.add(productKnowledgeRepository.getListProductKnowledge(noPartner)
            .with(schedulerProvider)
            .subscribe({
                productKnowledges.value = UiState.Success(it)
            },{
                productKnowledges.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}