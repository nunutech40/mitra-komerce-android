package id.android.kmabsensi.presentation.admin

import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.evaluation.LeaderEvaluationActivity
import id.android.kmabsensi.presentation.partner.evaluation.EvaluasiKolaborasiActivity
import kotlinx.android.synthetic.main.fragment_evaluasi_menu.*
import org.jetbrains.anko.startActivity

class EvaluasiMenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluasi_menu)
        setupToolbar("Evaluasi")

        btnEvaluasiLeader.setOnClickListener {
            startActivity<LeaderEvaluationActivity>()
        }

        btnEvaluasiKolaborasi.setOnClickListener {
            startActivity<EvaluasiKolaborasiActivity>()
        }
    }
}