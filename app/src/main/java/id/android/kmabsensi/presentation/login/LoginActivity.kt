package id.android.kmabsensi.presentation.login

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.main.MainActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            toast("Welcome")
            startActivity<MainActivity>()
        }

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
