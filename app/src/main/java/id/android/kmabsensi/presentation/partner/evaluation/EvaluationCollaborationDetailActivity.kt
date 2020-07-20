package id.android.kmabsensi.presentation.partner.evaluation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.EvaluationCollaboration
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.EVALUATION_KEY
import kotlinx.android.synthetic.main.activity_evaluation_collaboration_detail.*

class EvaluationCollaborationDetailActivity : BaseActivity() {

    private var evaluasi: EvaluationCollaboration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation_collaboration_detail)
        setupToolbar("Evaluasi Kolaborasi")

        evaluasi = intent.getParcelableExtra(EVALUATION_KEY)

        evaluasi?.let {
            edtJudul.setText(it.title)
            edtNama.setText(it.name)
            edtNoPartner.setText(it.noPartner)
            edtLeader.setText(it.leader.fullName)
            edtAlasanBerhentiKolaborasi.setText(it.reasonStopCollaboration)
            edtSaranPerbaikan.setText(it.improvementSuggestions)
        }
    }
}