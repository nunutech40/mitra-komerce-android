package id.android.kmabsensi.presentation.lupapassword

import android.os.Bundle
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityLupaPasswordBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
import id.android.kmabsensi.utils.ValidationForm.validationTextInputEditText
import id.android.kmabsensi.utils.createAlertError
import id.android.kmabsensi.utils.createAlertSuccess
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_lupa_password.*
import org.koin.android.ext.android.inject

class LupaPasswordActivity : BaseActivityRf<ActivityLupaPasswordBinding>(
    ActivityLupaPasswordBinding::inflate
) {

    private val vm: LupaPasswordViewModel by inject()
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        setupToolbar("Lupa Password", isBackable = true)

        myDialog = MyDialog(this)

        vm.response.observe(this, {
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
                vm.forgetPassword(binding.tieEmail.text.toString())
            }
        }
    }

    fun validation() : Boolean {
        return validationTextInputEditText(binding.tieEmail, binding.tilEmail,"Email tidak boleh kosong")
    }
}
