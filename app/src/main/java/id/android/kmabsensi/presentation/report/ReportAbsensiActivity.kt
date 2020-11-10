package id.android.kmabsensi.presentation.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kantor.report.PresentasiReportKantorActivity
import id.android.kmabsensi.utils.CATEGORY_REPORT_KEY
import id.android.kmabsensi.utils.IS_MANAGEMENT_KEY
import id.android.kmabsensi.utils.USER_KEY
import kotlinx.android.synthetic.main.fragment_report.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.compat.SharedViewModelCompat.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

// For manajemen

class ReportAbsensiActivity : BaseActivity() {

    private val vm: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_absensi2)
        setupToolbar("Report Absensi")

        btnReportKantor.setOnClickListener {
            startActivity<PresentasiReportKantorActivity>(CATEGORY_REPORT_KEY to 0)
        }

        btnReportSdm.setOnClickListener {
            startActivity<PresentasiReportKantorActivity>(
                CATEGORY_REPORT_KEY to 2,
                IS_MANAGEMENT_KEY to (vm.getUserData().role_id == 2),
                USER_KEY to vm.getUserData())
        }
    }
}