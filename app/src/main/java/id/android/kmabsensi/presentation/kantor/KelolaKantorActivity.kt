package id.android.kmabsensi.presentation.kantor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.cabang.TambahCabangActivity
import kotlinx.android.synthetic.main.activity_kelola_kantor.*
import org.jetbrains.anko.startActivity

class KelolaKantorActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_kantor)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kelola Data Kantor"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        val data = listOf(
            Kantor("Cabang A"),
            Kantor("Cabang B"),
            Kantor("Cabang C")
        )

        data.forEach {
            groupAdapter.add(KantorItem(it))
        }

        btnTambahKantor.setOnClickListener {
            startActivity<TambahCabangActivity>()
        }

    }

    private fun initRv(){
        rvKantor.apply {
            layoutManager = LinearLayoutManager(this@KelolaKantorActivity)
            adapter = groupAdapter
        }
    }


}
