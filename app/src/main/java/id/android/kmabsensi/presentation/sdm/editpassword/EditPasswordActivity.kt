package id.android.kmabsensi.presentation.sdm.editpassword

import android.os.Bundle
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityEditPasswordBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ValidationForm.validationTextInputEditText
import id.android.kmabsensi.utils.ui.MyDialog
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class EditPasswordActivity : BaseActivityRf<ActivityEditPasswordBinding>(
    ActivityEditPasswordBinding::inflate
) {

    private val vm: PasswordManagementViewModel by inject()

    private var userId: Int? = null

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar("Ubah Password", isBackable = true)

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
                        createAlertError(this, getString(R.string.label_gagal), it.data.message)
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    createAlertError(this, getString(R.string.label_gagal), getString(R.string.message_error_occured))
                    e { it.throwable.message.toString() }
                }
            }
        })
        setupListener()
    }

    private fun setupListener() {
        binding.apply {
            btnSimpan.setOnClickListener {
                if (validation()) {
                    vm.changePassword(
                        userId.toString(),
                        tieOldPass.text.toString(),
                        tieKonfirmPass.text.toString()
                    )
                }
            }
        }
    }

    fun validation(): Boolean {
        binding.apply {
            val oldPassword =
                validationTextInputEditText(tieOldPass,tilOldPass, "Password lama harus di isi")
            val newPassword = validationTextInputEditText(tieNewPass,tilNewPass, "Password baru harus di isi")
            val newPasswordConfirmation =
                validationTextInputEditText(tieKonfirmPass, tilKonfirmPass, "Konfirmasi password harus di isi")
            var isValid = false
            if (tieNewPass.text.toString() != tieKonfirmPass.text.toString()){
                tilKonfirmPass.requestFocus()
                tilKonfirmPass.error = "password baru harus sama"
            }else{
                isValid = true
            }
            return oldPassword && newPassword && newPasswordConfirmation && isValid

        }
    }
}
