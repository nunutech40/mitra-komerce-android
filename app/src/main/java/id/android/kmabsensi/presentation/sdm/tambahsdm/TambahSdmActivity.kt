package id.android.kmabsensi.presentation.sdm.tambahsdm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.*

class TambahSdmActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_sdm)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Tambah SDM"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}
