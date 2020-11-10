package id.android.kmabsensi.presentation.myevaluation

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Evaluation
import id.android.kmabsensi.utils.localDateFormatter
import kotlinx.android.synthetic.main.item_row_myevaluation.view.*
import org.joda.time.LocalDate

class MyEvaluationItem(
    val evaluation: Evaluation,
    val listener: (Evaluation) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textTitle.text = evaluation.title
            itemView.textLeaderName.text = evaluation.userTarget.fullName
            itemView.textEvaluatorName.text = evaluation.userEvaluator?.fullName
            val localDate = LocalDate.parse(evaluation.evaluationPeriod.split(" ")[0])
            itemView.textTime.text = localDateFormatter(localDate, "MMM yyyy")

            itemView.setOnClickListener {
                listener(evaluation)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_myevaluation
    }
}