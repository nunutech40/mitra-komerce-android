package id.android.kmabsensi.presentation.partner.partneroff

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.utils.localDateFormatter
import kotlinx.android.synthetic.main.item_row_partner_off.*
import org.joda.time.Days
import org.joda.time.LocalDate

class PartnerOffItem(
    val partner: Partner,
    val listener: (Partner) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            txtPartnerName.text = "${partner.noPartner} - ${partner.fullName}"
            txtStatusInvoice.text = if (partner.lastInvoiceStatus == 0) "UNPAID" else "COMPLETED"
            txtStatusInvoice.setTextColor(
                if (partner.lastInvoiceStatus == 0)
                    ContextCompat.getColor(itemView.context, R.color._DE4D4E)
                else
                    ContextCompat.getColor(itemView.context, R.color.green)
            )

            partner.lastDataOfPause.apply {
                if (split("-")[0] == "0000") {
                    txtDateOff.text = "-"
                } else {
                    val dateOff: LocalDate = LocalDate.parse(split(" ")[0])
                    txtDateOff.text = localDateFormatter(dateOff)
                }
            }

            itemView.setOnClickListener {
                listener(partner)
            }
        }
    }

    override fun getLayout() = R.layout.item_row_partner_off
}