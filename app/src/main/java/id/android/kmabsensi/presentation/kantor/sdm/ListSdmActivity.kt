package id.android.kmabsensi.presentation.kantor.sdm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_list_sdm.*

class ListSdmActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val data = listOf(
        "Adam Maulanan",
        "Malik Juliyan",
        "Ayu R",
        "Nela P",
        "Gibran Pramudya"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_sdm)

        val name = intent.getStringExtra("name")

        setSupportActionBar(toolbar)
        supportActionBar?.title = "List SDM $name"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        data.forEach {
            groupAdapter.add(SdmItem(it))
        }
    }

    private fun initRv(){
        rvSdm.apply {
            layoutManager = LinearLayoutManager(this@ListSdmActivity)
            adapter = groupAdapter
        }
    }
}
