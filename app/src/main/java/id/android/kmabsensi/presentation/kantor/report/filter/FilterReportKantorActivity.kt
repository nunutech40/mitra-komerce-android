package id.android.kmabsensi.presentation.kantor.report.filter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_filter_report_kantor.*

class FilterReportKantorActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_report_kantor)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Filter Report"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
