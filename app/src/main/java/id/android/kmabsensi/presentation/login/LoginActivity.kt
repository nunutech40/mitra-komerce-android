package id.android.kmabsensi.presentation.login

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import id.android.kmabsensi.BuildConfig
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.databinding.ActivityLoginBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.lupapassword.LupaPasswordActivity
import id.android.kmabsensi.presentation.splash.SplashActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
import id.android.kmabsensi.utils.createAlertError
import id.android.kmabsensi.utils.ui.MyDialog
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject


class LoginActivity : BaseActivityRf<ActivityLoginBinding>(
    ActivityLoginBinding::inflate
) {

    private val vm: LoginViewModel by inject()
    private val prefHelper: PreferencesHelper by inject()

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDialog = MyDialog(this)
        checkPermission()
        initView()
        setupObserver()
        setupListener()
    }

    private fun setupListener(){
        binding.apply {
            btnLogin.setOnClickListener {
                if (validation()) vm.login(tieUsername.text.toString(), tiePassword.text.toString())
            }

            tvForgotPass.setOnClickListener {
                startActivity<LupaPasswordActivity>()
            }
        }
    }

    private fun setupObserver() {
        vm.loginState.observe(this, {
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

        vm.userProfileData.observe(this, {
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
    }

    private fun initView() {
        try {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) {
                d { "fcm token : " +it.token.toString() }
                prefHelper.saveString(PreferencesHelper.FCM_TOKEN, it.token)

            }.addOnFailureListener {
                if (!BuildConfig.DEBUG) {
                    d { "fcm token : " +it.message.toString() }
                    it.message?.let { FirebaseCrashlytics.getInstance().log(it) }
                } else {
                    it.message?.let { FirebaseCrashlytics.getInstance().log(it) }
                }
            }
        } catch (ex: Exception) {
            if (!BuildConfig.DEBUG) {
                ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
            } else {
                ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
            }
        }

    }


    fun validation() : Boolean {
        binding.apply {
            val email = ValidationForm.validationTextInputEditText(tieUsername, tilUsername, "Email tidak boleh kosong")
            val password = ValidationForm.validationTextInputEditText(tiePassword, tilPass, "Password tidak boleh kosong")
            return email && password
        }
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
        intent.data = uri
        startActivityForResult(intent, 101)
    }
}
