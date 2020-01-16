package id.android.kmabsensi.presentation.coworking

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.CoworkingSpace
import kotlinx.android.synthetic.main.item_row_coworking.*
import kotlinx.android.synthetic.main.item_row_coworking.view.*

class CoworkingItem(val coworking: CoworkingSpace,
                    val listener: (CoworkingSpace) -> Unit) : Item() {

    /**
     * status : - 1 : Tersedia
     *          - 2 : Acara
     */
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.txtCoworking.text = coworking.cowork_name
        viewHolder.itemView.txtLokasi.text = coworking.address

        if (coworking.status == 1){
            viewHolder.itemView.txtStatus.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.status_tersedia))
            viewHolder.itemView.txtStatus.text = "${coworking.slot} Kursi / 100"
        } else if (coworking.status == 2){
            viewHolder.itemView.txtStatus.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.status_acara))
            viewHolder.itemView.txtStatus.text = "Acara"
        }

        viewHolder.itemView.container.setOnClickListener {
            listener(coworking)
        }


    }

    override fun getLayout(): Int = R.layout.item_row_coworking
}