package id.android.kmabsensi.presentation.partner.kategori

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPartnerCategoryResponse
import id.android.kmabsensi.data.repository.PartnerCategoryRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class PartnerCategoryViewModel(val partnerCategoryRepository: PartnerCategoryRepository,
                               val schedulerProvider: SchedulerProvider): BaseViewModel() {

    val partnerCategories by lazy {
        MutableLiveData<UiState<ListPartnerCategoryResponse>>()
    }

    val crudResponse by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    fun getPartnerCategories(){
        partnerCategories.value = UiState.Loading()
        compositeDisposable.add(partnerCategoryRepository.getPartnerCategories()
            .with(schedulerProvider)
            .subscribe({
                partnerCategories.value = UiState.Success(it)
            }, ::onError)
        )
    }

    fun addPartnerCategory(partnerCategory: String){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(partnerCategoryRepository.addPartnerCategory(partnerCategory)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            }, ::onError))
    }

    fun editPartnerCategory(id: Int, partnerCategory: String){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(partnerCategoryRepository.editPartnerCategory(id, partnerCategory)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            }, ::onError))
    }

    fun deletePartnerCategory(id: Int){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(partnerCategoryRepository.deletePartnerCategory(id)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            }, ::onError))
    }

    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
        partnerCategories.value = UiState.Error(error)
        crudResponse.value = UiState.Error(error)
    }

}