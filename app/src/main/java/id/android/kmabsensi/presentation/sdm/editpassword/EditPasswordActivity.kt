package id.android.kmabsensi.presentation.sdm.editpassword

import android.os.Bundle
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.USER_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
import id.android.kmabsensi.utils.loadCircleImage
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_edit_password.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class EditPasswordActivity : BaseActivity() {

    private val vm: EditPasswordViewModel by inject()

    private var karyawan : User? = null

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        karyawan = intent.getParcelableExtra(USER_KEY)

        myDialog = MyDialog(this)

        karyawan?.photo_profile_url?.let {
            imgProfile.loadCircleImage(it)
        }

        vm.response.observe(this, Observer {
            when(it){
                is UiState.Loading -> { myDialog.show() }
                is UiState.Success -> {
                    myDialog.dismiss()
                    toast(it.data.message)
                    edtEmail.setText("")
                    edtPasword.setText("")
                    edtConfirmPassword.setText("")
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        btnSimpan.setOnClickListener {
            if (validation()){
                vm.resetPassword(karyawan?.id.toString(), edtConfirmPassword.text.toString())
            }
        }
    }

    fun validation(): Boolean {

        val oldPassword = ValidationForm.validationInput(edtEmail, "Password lama harus di isi")
        val newPassword = ValidationForm.validationInput(edtPasword, "Password baru harus di isi")
        val newPasswordConfirmation = ValidationForm.validationInput(edtConfirmPassword, "Konfirmasi password harus di isi")
        val reNewPAssInput: Boolean = ValidationForm.validationSingkronPassword(
            edtPasword,
            edtConfirmPassword,
            "password baru harus sama"
        )

        return oldPassword && newPassword && newPasswordConfirmation && reNewPAssInput

    }
}
