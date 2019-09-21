package id.android.kmabsensi.presentation.sdm.pilihkantor

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_kantor_cabang.view.*

class KantorCabangItem(val kantor: String,
                       val listener: () -> Unit) : Item() {

    private var isSelected = false
    private lateinit var viewHolder: ViewHolder

    override fun bind(viewHolder: ViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.apply {
            itemView.txtNamaKantor.text = kantor

            itemView.setOnClickListener {
                listener()
                if (!isSelected){
                    viewHolder.itemView.icon_checklist.visible()
                    isSelected = true
                }
            }
        }

    }

    fun showOrHiddenChecklist(){
        if (isSelected){
            viewHolder.itemView.icon_checklist.gone()
            isSelected = false
        }
    }

    override fun getLayout(): Int = R.layout.item_row_kantor_cabang




}