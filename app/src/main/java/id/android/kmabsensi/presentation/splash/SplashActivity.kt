package id.android.kmabsensi.presentation.splash

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import id.android.kmabsensi.BuildConfig
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject


class SplashActivity : AppCompatActivity() {

    private val prefHelper: PreferencesHelper by inject()
    val MY_REQUEST_CODE = 10

    // Creates instance of the manager.
    val appUpdateManager = AppUpdateManagerFactory.create(this)

    // Returns an intent object that you use to check for an update.
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tv_version_name.text = BuildConfig.VERSION_NAME
        checkForUpdate()
    }

    private fun checkForUpdate() {


        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                // Checks that the platform will allow the specified type of update.
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE
                    )
                ) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            MY_REQUEST_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }

                } else {
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
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, resultCode.toString(), Toast.LENGTH_SHORT).show()
                // If the update is cancelled or fails,
                // you can request to start the update again.
//                checkForUpdate();
            } else {
                if (prefHelper.getBoolean(PreferencesHelper.IS_LOGIN)) {
                    startActivity<HomeActivity>()
                    finish()
                } else {
                    startActivity<LoginActivity>()
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .getAppUpdateInfo()
            .addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability() === UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            MY_REQUEST_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }

                }
            }
    }
}
