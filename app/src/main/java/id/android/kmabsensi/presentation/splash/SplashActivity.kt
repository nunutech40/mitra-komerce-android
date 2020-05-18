package id.android.kmabsensi.presentation.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import id.android.kmabsensi.BuildConfig
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.login.LoginActivity
import id.android.kmabsensi.utils.createAlertError
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject


class SplashActivity : AppCompatActivity() {

    private val prefHelper: PreferencesHelper by inject()
    private val MY_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tv_version_name.text = BuildConfig.VERSION_NAME
    }

    override fun onResume() {
        super.onResume()
        checkUpdate()
        initView()
    }

    private fun initView() {
        try {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) {
                Log.i(SplashActivity::class.simpleName, "device token is " + it.token)
                prefHelper.saveString(PreferencesHelper.FCM_TOKEN, it.token)

            }.addOnFailureListener {
                if (!BuildConfig.DEBUG) {
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

    private fun initProcess() {
        Handler().postDelayed(
            {
                if (prefHelper.getBoolean(PreferencesHelper.IS_LOGIN)) {
                    startActivity<HomeActivity>()
                    finish()
                } else {
                    startActivity<LoginActivity>()
                    finish()
                }
            }, 1500
        )
    }


    private fun checkUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val appUpdateManager = AppUpdateManagerFactory.create(this)
            appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.UPDATE_AVAILABLE
                    ) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            MY_REQUEST_CODE
                        )
                    } else {
                        initProcess()
                    }
                }.addOnFailureListener {
                    initProcess()
                }

        } else {
            initProcess()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                createAlertError(this, "Error", "Gagal melakukan update aplikasi")
            }
            initProcess()
        }
    }


}
