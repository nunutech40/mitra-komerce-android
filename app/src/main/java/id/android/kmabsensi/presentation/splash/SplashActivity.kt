package id.android.kmabsensi.presentation.splash

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tv_version_name.text = BuildConfig.VERSION_NAME

        Handler().postDelayed(
            {
                if (prefHelper.getBoolean(PreferencesHelper.IS_LOGIN)){
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
