package id.android.kmabsensi.presentation.base

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
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