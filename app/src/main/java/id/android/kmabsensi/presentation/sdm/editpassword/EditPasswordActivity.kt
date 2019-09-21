package id.android.kmabsensi.presentation.sdm.editpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_edit_password.*

class EditPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }
}
