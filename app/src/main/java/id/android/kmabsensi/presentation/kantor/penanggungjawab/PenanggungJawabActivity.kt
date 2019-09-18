package id.android.kmabsensi.presentation.kantor.penanggungjawab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.sdm.ListSdmActivity
import kotlinx.android.synthetic.main.activity_penanggung_jawab.*
import org.jetbrains.anko.startActivity

class PenanggungJawabActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val data = listOf("Muhamad Arief", "Abdul Azis", "Yusriyadi", "Bambang Cahyono")

    val headers = mutableListOf<ExpandableHeaderItem>()

    var penanggungJawabSelected: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penanggung_jawab)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Pilih Penanggung Jawab"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        data.forEach { name ->
            val header = ExpandableHeaderItem(name){
                headers.forEach {
                    it.collapse()
                    penanggungJawabSelected = name
                }
            }
            headers.add(header)
            groupAdapter.add(ExpandableGroup(header).apply {
                add(PenanggungJawabItem {
                    startActivity<ListSdmActivity>("name" to penanggungJawabSelected)
                })
            })
        }
    }

    fun initRv(){
        rvPenanggungJawab.apply {
            layoutManager = LinearLayoutManager(this@PenanggungJawabActivity)
            adapter = groupAdapter
        }
    }
}
