package id.android.kmabsensi.presentation.login

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.Observer
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.lupapassword.LupaPasswordActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
import id.android.kmabsensi.utils.createAlertError
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject


class LoginActivity : BaseActivity() {

    private val vm: LoginViewModel by inject()

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        myDialog = MyDialog(this)
        checkPermission()

        vm.loginState.observe(this, Observer {
            when(it){
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                   if (it.data.message != null){
                       myDialog.dismiss()
                       createAlertError(this, "Login gagal", it.data.message)
                   }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        vm.userProfileData.observe(this, Observer {
            when(it){
                is UiState.Loading -> {
//                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    startActivity(intentFor<HomeActivity>().clearTask().newTask())
//                    toast("Welcome, ${it.data.data[0].full_name}")
                    finish()
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        btnLogin.setOnClickListener {
            if (validation()) vm.login(edtEmail.text.toString(), edtPasword.text.toString())
        }

        btnLupaPassword.setOnClickListener {
            startActivity<LupaPasswordActivity>()
        }


    }

    fun validation() : Boolean {
        val email = ValidationForm.validationInput(edtEmail, "Email tidak boleh kosong")
        val password = ValidationForm.validationInput(edtPasword, "Password tidak boleh kosong")

        return email && password
    }

    private fun checkPermission(){
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    response?.let {
                        if (it.isPermanentlyDenied){
                            alert("This app needs permission to use this feature. You can grant them in app settings.", "Need Permission") {
                                yesButton { openSettings() }
                            }.show()
                        }
                    }
                }

            }).check()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.setData(uri)
        startActivityForResult(intent, 101)
    }
}
