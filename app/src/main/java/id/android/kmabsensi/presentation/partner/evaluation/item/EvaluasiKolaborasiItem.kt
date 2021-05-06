package id.android.kmabsensi.presentation.partner.evaluation.item

import android.util.Log
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.EvaluationCollaboration
import kotlinx.android.synthetic.main.item_row_evaluation_collaboration.view.*

class EvaluasiKolaborasiItem(
    val evaluationCollaboration: EvaluationCollaboration,
    val listener: (EvaluationCollaboration) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtNoPartnerAndName.text =
                "${evaluationCollaboration.noPartner} - ${evaluationCollaboration.name}"
            try {
                itemView.txtLeaderName.text = evaluationCollaboration.leader.fullName
            }catch (e: Exception){
                Log.d("_evalCollaboration", "error: ${e.message} - detail data : ${evaluationCollaboration}")
                itemView.txtLeaderName.text = "Nama tidak diketahui"
                itemView.alpha = 0.5F
            }

            itemView.setOnClickListener {
                listener(evaluationCollaboration)
            }
        }
    }

    override fun getLayout() = R.layout.item_row_evaluation_collaboration
}