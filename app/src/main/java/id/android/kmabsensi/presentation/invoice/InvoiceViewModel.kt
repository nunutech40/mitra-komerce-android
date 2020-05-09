package id.android.kmabsensi.presentation.invoice

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.CreateInvoiceBody
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.CreateInvoiceResponse
import id.android.kmabsensi.data.remote.response.InvoiceReportResponse
import id.android.kmabsensi.data.remote.response.invoice.MyInvoiceResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetailResponse
import id.android.kmabsensi.data.repository.InvoiceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class InvoiceViewModel(
    val invoiceRepository: InvoiceRepository,
    val prefHelper: PreferencesHelper,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val invoices by lazy {
        MutableLiveData<UiState<MyInvoiceResponse>>()
    }

    val invoiceDetail by lazy {
        MutableLiveData<UiState<InvoiceDetailResponse>>()
    }

    val createInvoiceResponse by lazy {
        MutableLiveData<UiState<CreateInvoiceResponse>>()
    }

    val updateInvoiceResponse by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    val invoiceReports by lazy {
        MutableLiveData<UiState<InvoiceReportResponse>>()
    }

    fun getMyInvoice(isActive: Boolean) {
        invoices.value = UiState.Loading()
        compositeDisposable.add(
            invoiceRepository.getMyInvoice(getUser().id, isActive)
                .with(schedulerProvider)
                .subscribe({
                    invoices.value = UiState.Success(it)
                }, {
                    invoices.value = UiState.Error(it)
                })
        )
    }

    fun createInvoice(body: CreateInvoiceBody) {
        createInvoiceResponse.value = UiState.Loading()
        compositeDisposable.add(
            invoiceRepository.createInvoice(body)
                .with(schedulerProvider)
                .subscribe({
                    createInvoiceResponse.value = UiState.Success(it)
                }, {
                    createInvoiceResponse.value = UiState.Error(it)
                })
        )
    }

    fun getInvoiceAdminDetail(invoiceId: Int) {
        invoiceDetail.value = UiState.Loading()
        compositeDisposable.add(invoiceRepository.getInvoiceAdminDetail(invoiceId)
            .with(schedulerProvider)
            .subscribe({
                invoiceDetail.value = UiState.Success(it)
            }, {
                invoiceDetail.value = UiState.Error(it)
            })
        )
    }

    fun getInvoiceGajiDetail(invoiceId: Int) {
        invoiceDetail.value = UiState.Loading()
        compositeDisposable.add(invoiceRepository.getInvoiceGajiDetail(invoiceId)
            .with(schedulerProvider)
            .subscribe({
                invoiceDetail.value = UiState.Success(it)
            }, {
                invoiceDetail.value = UiState.Error(it)
            })
        )
    }

    fun updateInvoice(invoiceId: Int, status: Int) {
        updateInvoiceResponse.value = UiState.Loading()
        compositeDisposable.add(invoiceRepository.updateInvoice(invoiceId, status)
            .with(schedulerProvider)
            .subscribe({
                updateInvoiceResponse.value = UiState.Success(it)
            }, {
                updateInvoiceResponse.value = UiState.Error(it)
            })
        )
    }

    fun getInvoiceReport(startPeriod: String, endPeriod: String, userRequesterId: Int){
        invoiceReports.value = UiState.Loading()
        compositeDisposable.add(invoiceRepository.getInvoiceReport(startPeriod, endPeriod, userRequesterId)
            .with(schedulerProvider)
            .subscribe({
                invoiceReports.value = UiState.Success(it)
            },{
                invoiceReports.value = UiState.Error(it)
            }))
    }

    fun getUser(): User {
        return Gson().fromJson(
            prefHelper.getString(PreferencesHelper.PROFILE_KEY),
            User::class.java
        )
    }

    override fun onError(error: Throwable) {

    }

}