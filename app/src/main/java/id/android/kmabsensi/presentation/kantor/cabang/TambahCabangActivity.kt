package id.android.kmabsensi.presentation.kantor.cabang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.penanggungjawab.PenanggungJawabActivity
import kotlinx.android.synthetic.main.activity_tambah_cabang.*
import org.jetbrains.anko.startActivity

class TambahCabangActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_cabang)


        setSupportActionBar(toolbar)
        supportActionBar?.title = "Tambah Cabang Baru"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnPilihPenanggungJawab.setOnClickListener {
            startActivity<PenanggungJawabActivity>()
        }

    }

}
