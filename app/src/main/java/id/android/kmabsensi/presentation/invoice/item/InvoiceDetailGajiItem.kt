package id.android.kmabsensi.presentation.invoice.item

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetailGaji
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_invoice_detail_gaji.view.*

class InvoiceDetailGajiItem(val context: Context, val invoiceDetailGaji: InvoiceDetailGaji): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textLeaderName.text = invoiceDetailGaji.sdmData?.fullName
            invoiceDetailGaji.bankAccount?.let {
                itemView.textBankAccountName.text = "${it.bankName} | ${it.bankOwnerName}"
                itemView.textBankAccountNumber.text = it.bankNo
                itemView.btnCopyBankAccNumber.visible()
            } ?: run {
                itemView.textBankAccountName.text = "Informasi akun bank SDM belum tersedia"
                itemView.textBankAccountNumber.text = "-"
                itemView.btnCopyBankAccNumber.gone()
            }

            itemView.textGajiSdm.text = convertRp(invoiceDetailGaji.total.toDouble())
            invoiceDetailGaji.description?.let {
                itemView.textDescription.visible()
                itemView.textDescription.setTrimMode(0)
                itemView.textDescription.setTrimLines(2)
                itemView.textDescription.setColorClickableText(ContextCompat.getColor(itemView.context, R.color._2196F3))
                itemView.textDescription.text = invoiceDetailGaji.description
            }

            itemView.btnCopyBankAccNumber.setOnClickListener {
                val clipboard: ClipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("Nomor Rekening",invoiceDetailGaji.bankAccount?.bankNo )
                clipboard.setPrimaryClip(clip)

                Toast.makeText(context, "Nomor rekening tersalin", Toast.LENGTH_SHORT).show()
            }

            itemView.btnCopyGajiSdm.setOnClickListener {
                val clipboard: ClipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("Nominal",invoiceDetailGaji.total )
                clipboard.setPrimaryClip(clip)

                Toast.makeText(context, "Nominal tersalin", Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun getLayout() = R.layout.item_row_invoice_detail_gaji
}