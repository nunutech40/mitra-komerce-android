package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.CreateInvoiceBody
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.CreateInvoiceResponse
import id.android.kmabsensi.data.remote.response.InvoiceReportDetailResponse
import id.android.kmabsensi.data.remote.response.InvoiceReportResponse
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

    fun filterMyInvoice(
        userId: Int,
        isActive: Boolean,
        invoiceType: Int,
        userToId: Int,
        leaderIdSelected: Int
    ): Single<MyInvoiceResponse> {
        val body = mapOf(
            "user_id" to userId,
            "is_active" to isActive,
            "invoice_type" to invoiceType,
            "user_to_id" to userToId,
            "user_requester_id" to leaderIdSelected
        )
        return apiService.filterMyInvoice(body)
    }

    fun getInvoiceAdminDetail(invoiceId: Int): Single<InvoiceDetailResponse> {
        return apiService.getInvoiceAdminDetail(invoiceId)
    }

    fun getInvoiceGajiDetail(invoiceId: Int): Single<InvoiceDetailResponse> {
        return apiService.getInvoiceGajiDetail(invoiceId)
    }

    fun createInvoice(body: CreateInvoiceBody): Single<CreateInvoiceResponse> {
        return apiService.createInvoice(body)
    }

    fun updateInvoice(invoiceId: Int, status: Int): Single<BaseResponse> {
        val body = mapOf(
            "invoice_id" to invoiceId,
            "status" to status
        )
        return apiService.updateInvoice(body)
    }

    fun getInvoiceReport(
        startPeriod: String,
        endPeriod: String,
        userRequestedId: Int,
        invoiceType: Int
    ): Single<InvoiceReportResponse> {
        val body = mapOf(
            "start_period" to startPeriod,
            "end_period" to endPeriod,
            "user_requester_id" to userRequestedId,
            "invoice_type" to invoiceType
        )

        return apiService.getInvoiceReport(body)
    }

    fun getInvoiceDetailReport(
        startPeriod: String,
        endPeriod: String,
        userRequestedId: Int,
        invoiceType: Int,
        status: Int
    ): Single<InvoiceReportDetailResponse> {
        val body = mapOf(
            "start_period" to startPeriod,
            "end_period" to endPeriod,
            "user_requester_id" to userRequestedId,
            "invoice_type" to invoiceType,
            "status" to status
        )

        return apiService.getInvoiceReportDetail(body)
    }

}