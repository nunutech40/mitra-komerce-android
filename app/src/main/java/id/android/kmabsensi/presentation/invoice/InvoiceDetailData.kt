package id.android.kmabsensi.presentation.invoice

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetail

object InvoiceDetailData {

    private val invoiceItems = mutableListOf<InvoiceDetail>()

    val invoiceItemsData by lazy {
        MutableLiveData<List<InvoiceDetail>>()
    }

    fun addInvoiceItem(invoiceDetail: InvoiceDetail){
        invoiceItems.add(invoiceDetail)
        invoiceItemsData.value = invoiceItems
    }

    fun updateInvoiceItem(invoiceDetail: InvoiceDetail, index: Int){
        invoiceItems[index] = invoiceDetail
        invoiceItemsData.value = invoiceItems
    }

    fun removeItem(index: Int){
        invoiceItems.removeAt(index)
        invoiceItemsData.value = invoiceItems
    }

    fun clear(){
        invoiceItems.clear()
        invoiceItemsData.value = invoiceItems
    }


}