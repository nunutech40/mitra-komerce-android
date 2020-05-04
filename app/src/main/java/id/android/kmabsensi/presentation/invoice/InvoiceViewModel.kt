package id.android.kmabsensi.presentation.invoice

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.CreateInvoiceBody
import id.android.kmabsensi.data.remote.response.CreateInvoiceResponse
import id.android.kmabsensi.data.remote.response.MyInvoiceResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.repository.InvoiceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class InvoiceViewModel(val invoiceRepository: InvoiceRepository,
                       val prefHelper: PreferencesHelper,
                       val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val invoices by lazy {
        MutableLiveData<UiState<MyInvoiceResponse>>()
    }

    val createInvoiceResponse by lazy {
        MutableLiveData<UiState<CreateInvoiceResponse>>()
    }

    fun getMyInvoice(isActive: Boolean){
        invoices.value = UiState.Loading()
        compositeDisposable.add(invoiceRepository.getMyInvoice(5, isActive)
            .with(schedulerProvider)
            .subscribe({
                invoices.value = UiState.Success(it)
            },{
                invoices.value = UiState.Error(it)
            }))
    }

   fun createInvoice(body: CreateInvoiceBody){
       createInvoiceResponse.value = UiState.Loading()
       compositeDisposable.add(invoiceRepository.createInvoice(body)
           .with(schedulerProvider)
           .subscribe({
                createInvoiceResponse.value = UiState.Success(it)
           },{
                createInvoiceResponse.value = UiState.Error(it)
           })
       )
   }

    fun getUser() : User {
        return Gson().fromJson(
            prefHelper.getString(PreferencesHelper.PROFILE_KEY),
            User::class.java
        )
    }

    override fun onError(error: Throwable) {

    }

}