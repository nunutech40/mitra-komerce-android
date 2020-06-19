package id.android.kmabsensi.presentation.myevaluation

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Evaluation
import kotlinx.android.synthetic.main.item_row_myevaluation.view.*

class MyEvaluationItem(
    val evaluation: Evaluation,
    val listener: (Evaluation) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textTitle.text = evaluation.title
            itemView.textLeaderName.text = evaluation.userTarget.fullName
            itemView.textEvaluatorName.text = evaluation.userEvaluator.fullName
            itemView.textTime.text = evaluation.evaluationPeriod

            itemView.setOnClickListener {
                listener(evaluation)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_myevaluation
    }
}