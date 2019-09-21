package id.android.kmabsensi.presentation.sdm.pilihkantor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.activity_pilih_kantor_cabang.*
import org.koin.android.ext.android.inject

class PilihKantorCabangActivity : AppCompatActivity() {

    private val groupAdapter : GroupAdapter<ViewHolder> by inject()

    private val data = listOf(
        "Kantor Z",
        "Kantor C",
        "Kantor B",
        "Kantor A"
    )

    val cabangItems = mutableListOf<KantorCabangItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_kantor_cabang)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Pilih Kantor Cabang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        rvKantor.apply {
            layoutManager = LinearLayoutManager(this@PilihKantorCabangActivity)
            adapter = groupAdapter
        }

        data.forEach {
            val kantorCabangItem = KantorCabangItem(it){
                cabangItems.forEach {
                    it.showOrHiddenChecklist()
                }
            }
            cabangItems.add(kantorCabangItem)

            groupAdapter.add(kantorCabangItem)

        }



    }
}
