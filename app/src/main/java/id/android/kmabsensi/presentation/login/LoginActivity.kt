package id.android.kmabsensi.presentation.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.main.MainActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ui.MyDialog
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject


class LoginActivity : AppCompatActivity() {

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
                    myDialog.dismiss()
                    startActivity<HomeActivity>("role" to it.data.data[0].role_name.toLowerCase())
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    toast(it.throwable.message.toString())
                }
            }
        })

        btnLogin.setOnClickListener {
            vm.login(edtUsername.text.toString(), edtPassword.text.toString())
        }



    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    private fun checkPermission(){
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    toast("granted")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    toast("denied")
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
