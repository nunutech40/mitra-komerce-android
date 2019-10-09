package id.android.kmabsensi.presentation.lupapassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
import id.android.kmabsensi.utils.createAlertError
import id.android.kmabsensi.utils.createAlertSuccess
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_kelola_kantor.*
import kotlinx.android.synthetic.main.activity_kelola_kantor.toolbar
import kotlinx.android.synthetic.main.activity_lupa_password.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class LupaPasswordActivity : BaseActivity() {

    private val vm: LupaPasswordViewModel by inject()
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Lupa Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDialog = MyDialog(this)

        vm.response.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status){
                        createAlertSuccess(this, it.data.message)
                    } else {
                        createAlertError(this, "Gagal", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e(it.throwable)
                }
            }
        })

        btnSubmit.setOnClickListener {
            if (validation()){
                vm.forgetPassword(edtEmail.text.toString())
            }
        }
    }

    fun validation() : Boolean {
        val email = ValidationForm.validationInput(edtEmail, "Email tidak boleh kosong")
        val validEmail = ValidationForm.validationInput(edtEmail, "Email tidak valid")

        return email && validEmail
    }
}
