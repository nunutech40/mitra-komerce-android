package id.android.kmabsensi.presentation.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.myevaluation.LeaderEvaluationActivity
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
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }
}