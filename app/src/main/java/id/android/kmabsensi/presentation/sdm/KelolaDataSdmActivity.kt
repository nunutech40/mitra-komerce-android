package id.android.kmabsensi.presentation.sdm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.*
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.toolbar
import kotlinx.android.synthetic.main.activity_kelola_kantor.*

class KelolaDataSdmActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val data = listOf<Sdm>(
        Sdm("Ardika Mahendra", "Akuntan"),
        Sdm("Ardika Mahendra", "Akuntan"),
        Sdm("Ardika Mahendra", "Akuntan"),
        Sdm("Ardika Mahendra", "Akuntan")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_data_sdm)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kelola Data Kantor"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        data.forEach {
            groupAdapter.add(SdmItem(it))
        }

    }

    fun initRv(){
        val linearLayoutManager = LinearLayoutManager(this)
        rvSdm.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }
}
