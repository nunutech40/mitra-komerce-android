package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.CreateInvoiceBody
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.CreateInvoiceResponse
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetailResponse
import id.android.kmabsensi.data.remote.response.invoice.MyInvoiceResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class InvoiceRepository(val apiService: ApiService) {

    fun getMyInvoice(
        userId: Int,
        isActive: Boolean
    ): Single<MyInvoiceResponse> {
        val body = mapOf(
            "user_id" to userId,
            "is_active" to isActive
        )
        return apiService.getMyInvoice(body)
    }

    fun getInvoiceAdminDetail(invoiceId: Int): Single<InvoiceDetailResponse>{
        return apiService.getInvoiceAdminDetail(invoiceId)
    }

    fun getInvoiceGajiDetail(invoiceId: Int): Single<InvoiceDetailResponse>{
        return apiService.getInvoiceGajiDetail(invoiceId)
    }

    fun createInvoice(body: CreateInvoiceBody): Single<CreateInvoiceResponse> {
        return apiService.createInvoice(body)
    }

    fun updateInvoice(invoiceId: Int, status: Int): Single<BaseResponse>{
        val body = mapOf(
            "invoice_id" to invoiceId,
            "status" to status
        )
        return apiService.updateInvoice(body)
    }

}