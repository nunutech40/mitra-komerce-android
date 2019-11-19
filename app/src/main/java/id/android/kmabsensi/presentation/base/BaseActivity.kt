package id.android.kmabsensi.presentation.base

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.utils.ui.MyDialog
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.Exception

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDialog = MyDialog(this)

        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.statusBarColor = Color.TRANSPARENT
        }


    }

    fun showDialog() {
        myDialog.show()
    }

    fun hideDialog() {
        myDialog.dismiss()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun disableAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                window.decorView.importantForAutofill =
                    View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            } catch (e: Exception) {
                Crashlytics.log(e.message)
            }
        }
    }

    fun setToolbarTitle(title: String){
        txtTitle.text = title
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

}