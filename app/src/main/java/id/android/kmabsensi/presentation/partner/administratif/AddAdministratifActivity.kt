package id.android.kmabsensi.presentation.partner.administratif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity

class AddAdministratifActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_administratif)

        setupToolbar("Tambah Administratif")

    }
}