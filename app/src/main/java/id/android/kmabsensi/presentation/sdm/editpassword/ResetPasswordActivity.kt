package id.android.kmabsensi.presentation.sdm.editpassword

import android.os.Bundle
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_reset_password.btnSimpan
import kotlinx.android.synthetic.main.activity_reset_password.edtConfirmPassword
import kotlinx.android.synthetic.main.activity_reset_password.edtPasword
import org.koin.android.ext.android.inject

class ResetPasswordActivity : BaseActivity() {

    private val vm: PasswordManagementViewModel by inject()

    private var userId: Int? = null

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        disableAutofill()

        setupToolbar("Reset Password")

        userId = intent.getIntExtra(USER_ID_KEY, 0)

        myDialog = MyDialog(this)

        vm.response.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        createAlertSuccess(this, it.data.message)
                        finish()
                    } else {
                        createAlertError(this, "Gagal", it.data.message)
                    }


                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    createAlertError(this, "Gagal", getString(R.string.message_error_occured))
                    Timber.e { it.throwable.message.toString() }
                }
            }
        })

        btnSimpan.setOnClickListener {
            if (validation()) {
                vm.resetPassword(
                    userId.toString(),
                    edtConfirmPassword.text.toString()
                )
            }
        }
    }

    fun validation(): Boolean {

        val newPassword = ValidationForm.validationInput(edtPasword, "Password baru harus di isi")
        val newPasswordConfirmation =
            ValidationForm.validationInput(edtConfirmPassword, "Konfirmasi password harus di isi")
        val reNewPAssInput: Boolean = ValidationForm.validationSingkronPassword(
            edtPasword,
            edtConfirmPassword,
            "password baru harus sama"
        )

        return   newPassword && newPasswordConfirmation && reNewPAssInput

    }

}