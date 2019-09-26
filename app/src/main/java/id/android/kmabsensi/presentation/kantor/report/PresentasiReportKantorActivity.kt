package id.android.kmabsensi.presentation.kantor.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_presentasi_report_kantor.*
import org.koin.android.ext.android.inject

class PresentasiReportKantorActivity : BaseActivity() {

    private val groupAdapter: GroupAdapter<ViewHolder> by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentasi_report_kantor)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Presentasi Report Kantor"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        val data = listOf<Absensi>(
            Absensi("Sendi", "07.55", "17.10"),
            Absensi("Sendi", "07.55", "17.10"),
            Absensi("Sendi", "07.55", "17.10"),
            Absensi("Sendi", "07.55", "17.10"),
            Absensi("Sendi", "07.55", "17.10")
        )

        data.forEach {
            groupAdapter.add(AbsensiItem(it))
        }
    }

    fun initRv(){
        val linearLayoutManager = LinearLayoutManager(this)
        rvAbsensi.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }
}
