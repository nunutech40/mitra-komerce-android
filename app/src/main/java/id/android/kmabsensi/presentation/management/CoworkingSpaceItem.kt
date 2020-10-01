package id.android.kmabsensi.presentation.management

import androidx.core.content.ContextCompat
import com.github.ajalt.timberkt.Timber.d
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.UserCoworkingSpace
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.fragment_home_management.*
import kotlinx.android.synthetic.main.item_row_coworking_space.view.*


class CoworkingSpaceItem(val coworking: UserCoworkingSpace,
                         val listener: (UserCoworkingSpace, Boolean) -> Unit) : Item() {

    private var hasCheckin = false

    /**
     * status : - 1 : Tersedia
     *          - 2 : Acara
     */
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtCoworkName.text = coworking.cowork_name
            itemView.txtInformasi.text = coworking.description


            if (coworking.status == "1"){
                itemView.txtStatus.text = "Tersedia / ${coworking.available_slot} Kursi"
                if (coworking.cowork_presence.isEmpty()){
                    itemView.btnCheckIn.text = "Check In"
                    hasCheckin = false
                } else {
                    d { coworking.cowork_presence.last().checkout_date_time.toString() }
                    if (coworking.cowork_presence.last().checkout_date_time == null){
                        itemView.btnCheckIn.text = "Check Out"
                        hasCheckin = true
                    } else {
                        itemView.btnCheckIn.text = "Check In"
                        hasCheckin = false
                    }
                }
                itemView.btnCheckIn.visible()
                //if (coworking.available_slot == 0) itemView.btnCheckIn.isEnabled = false
                if (coworking.available_slot == 0 && !hasCheckin) itemView.btnCheckIn.isEnabled = false

            } else if (coworking.status == "2"){
                itemView.txtStatus.text = "Acara"
                itemView.btnCheckIn.gone()
            }

            itemView.btnCheckIn.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.white)
            itemView.btnCheckIn.setOnClickListener {
                listener(coworking, hasCheckin)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_coworking_space
}