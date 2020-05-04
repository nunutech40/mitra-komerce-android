package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.CreateInvoiceBody
import id.android.kmabsensi.data.remote.response.CreateInvoiceResponse
import id.android.kmabsensi.data.remote.response.MyInvoiceResponse
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

    fun createInvoice(body: CreateInvoiceBody): Single<CreateInvoiceResponse> {
        return apiService.createInvoice(body)
    }

}