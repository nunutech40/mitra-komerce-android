package id.android.kmabsensi.presentation.invoice

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.body.CreateInvoiceBody
import id.android.kmabsensi.data.remote.response.CreateInvoiceResponse
import id.android.kmabsensi.data.repository.InvoiceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class InvoiceViewModel(val invoiceRepository: InvoiceRepository,
                       val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val createInvoiceResponse by lazy {
        MutableLiveData<UiState<CreateInvoiceResponse>>()
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

    override fun onError(error: Throwable) {

    }

}