package id.android.kmabsensi.presentation.invoice.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetail
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.item.UploadBuktiPembayaranItem
import id.android.kmabsensi.utils.INVOICE_DATA_KEY
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.getDateWithDay
import id.android.kmabsensi.utils.parseStringDate
import kotlinx.android.synthetic.main.activity_bukti_pembayaran.*

class BuktiPembayaranActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bukti_pembayaran)
        setupToolbar("Bukti Pembayaran")
        initRv()

        val invoiceDetail = intent.getParcelableExtra<InvoiceDetail>(INVOICE_DATA_KEY)
        showDetailData(invoiceDetail)

    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvInvoiceUpload.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    private fun showDetailData(invoiceDetail: InvoiceDetail) {
        invoiceDetail.apply {
            textDate.text = getDateWithDay(parseStringDate(createdAt))
            textInvoiceNumber.text = invoiceKmId
            textDibuatOleh.text = userRequester.fullName
            textTotalTagihan.text = convertRp(amount.toDouble())

            when (status) {
                1 -> {
                    textInvoiceStatus.text = "OPEN"
                    textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_open)
                    textInvoiceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@BuktiPembayaranActivity,
                            R.color._64C3F9
                        )
                    )
                }
                2 -> {
                    textInvoiceStatus.text = "COMPLETED"
                    textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_complete)
                    textInvoiceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@BuktiPembayaranActivity,
                            R.color._5AB04F
                        )
                    )
                }
                3 -> {
                    textInvoiceStatus.text = "EXPIRED"
                    textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_cancel)
                    textInvoiceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@BuktiPembayaranActivity,
                            R.color._DE4D4E
                        )
                    )

                }
            }

            invoiceDetail.invoiceDetailGaji?.forEach {
                groupAdapter.add(UploadBuktiPembayaranItem(it))
            }

        }
    }
}
