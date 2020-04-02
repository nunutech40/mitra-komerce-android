package id.android.kmabsensi.presentation.partner.manajemenpartner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity

class FormPartnerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_partner)

        setupToolbar("Tambah Partner")
    }
}
