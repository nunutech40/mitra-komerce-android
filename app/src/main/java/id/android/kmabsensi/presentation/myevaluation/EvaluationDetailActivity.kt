package id.android.kmabsensi.presentation.myevaluation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Evaluation
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.EVALUATION_KEY
import id.android.kmabsensi.utils.spannableQuestionEvaluation
import kotlinx.android.synthetic.main.activity_evaluation_detail.*

class EvaluationDetailActivity : BaseActivity() {

    private var evaluation: Evaluation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation_detail)
        setupToolbar("Detail Evaluasi")

        evaluation = intent.getParcelableExtra(EVALUATION_KEY)
        evaluation?.let { submission ->
//            setupToolbar("${submission.title}")
            textTime.text = submission.evaluationPeriod
            textLeader.text = submission.userTarget.fullName
            textLeaderName.text = submission.userTarget.fullName
            textEvaluatorName.text = submission.userEvaluator.fullName

            textQuestion1.spannableQuestionEvaluation(submission.formEvaluationAnswer[0].questionName, submission.formEvaluationAnswer[0].notes)
            textQuestion2.spannableQuestionEvaluation(submission.formEvaluationAnswer[1].questionName, submission.formEvaluationAnswer[1].notes)
            textQuestion3.spannableQuestionEvaluation(submission.formEvaluationAnswer[2].questionName, submission.formEvaluationAnswer[2].notes)
            textAnswer1.text = submission.formEvaluationAnswer[0].answerValue
            textAnswer2.text = submission.formEvaluationAnswer[1].answerValue
            textAnswer3.text = submission.formEvaluationAnswer[2].answerValue
        }

    }
}
