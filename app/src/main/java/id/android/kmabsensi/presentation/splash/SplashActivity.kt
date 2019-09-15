package id.android.kmabsensi.presentation.splash

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.login.LoginActivity
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
                startActivity<LoginActivity>()
            }, 1500
        )
    }
}
