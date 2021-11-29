package id.android.kmabsensi.presentation.komboard

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityKomboardSettingBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.utils.komboard.CustomKeyboard
import kotlinx.android.synthetic.main.activity_komboard_setting.*
import java.util.*

class KomboardSettingActivity : BaseActivityRf<ActivityKomboardSettingBinding>(
    ActivityKomboardSettingBinding::inflate
) {

    private var mInputMethodManager: InputMethodManager? = null
    var timertask: TimerTask? = null
    var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Komboard", isBackable = true)

        setupListener()
        mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        checkMyChanges()
    }

    private fun setupListener() {
        binding.apply {

            btnActivedKomboard.setOnClickListener {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }

            btnSelectedKomboard.setOnClickListener {
                mInputMethodManager?.showInputMethodPicker()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        checkKomboardActive()
        isInputMethodEnabled()
    }

    fun isInputMethodEnabled(): Boolean {
        val id = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val defaultInputMethod = ComponentName.unflattenFromString(id)
        val myInputMethod = ComponentName(applicationContext, CustomKeyboard::class.java)
        if (defaultInputMethod?.shortClassName.equals(komboardPackage)) {
            actived()
//            btn_nonaktif_keyboard.visibility = View.VISIBLE
        }
//        else {
//            btn_nonaktif_keyboard.visibility = View.GONE
//        }
        return myInputMethod == defaultInputMethod
    }

    fun actived() {
        btn_actived_komboard.background = getDrawable(R.drawable.bg_with_stroke_grey_8d)
        btn_actived_komboard.setTextColor(getColor(R.color.color_btn_disable))
        btn_actived_komboard.isEnabled = false
        btn_selected_komboard.background = getDrawable(R.drawable.bg_with_stroke_orange_8d)
        btn_selected_komboard.setTextColor(getColor(R.color.colorPrimaryDark))
    }

    fun inActived() {
        btn_actived_komboard.background = getDrawable(R.drawable.bg_with_stroke_orange_8d)
        btn_actived_komboard.setTextColor(getColor(R.color.colorPrimaryDark))
        btn_actived_komboard.isEnabled = true
        btn_selected_komboard.background = getDrawable(R.drawable.bg_with_stroke_grey_8d)
        btn_selected_komboard.setTextColor(getColor(R.color.color_btn_disable))
        btn_nonaktif_keyboard.visibility = View.GONE
    }

    fun checkKomboardActive() {
        val list = mInputMethodManager?.enabledInputMethodList.toString()
        if (komboardPackage in list) {
            actived()
            btn_selected_komboard.isEnabled = true
        } else {
            inActived()
        }
        Log.i("TAG", "isInputMethodEnabled: $list")
    }

    fun nonActiveKomboard(): Boolean {

        val id = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val LATIN = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME"

        Log.i("TAG", "nonActiveKomboard: $id")

        val defaultInputMethod = ComponentName.unflattenFromString(id)
        val myInputMethod = ComponentName(applicationContext, LATIN)
        Log.i("TAG", "nonActiveKomboar1d: $myInputMethod")
        btn_nonaktif_keyboard.background = getDrawable(R.drawable.bg_button_disable_komboard)
        if (defaultInputMethod?.shortClassName.equals(LATIN)) {
//            actived()
            btn_nonaktif_keyboard.visibility = View.GONE
        } else {
            btn_nonaktif_keyboard.visibility = View.VISIBLE
        }
        return myInputMethod == defaultInputMethod

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        isMyInputMethodEnabled()
    }

    fun isMyInputMethodEnabled() {
        val imId = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        if (imId.contains(packageName)) {
            checkMyChanges()
        }
    }

    fun checkMyChanges() {
        timertask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    isInputMethodEnabled()
                }
            }
        }
        timer = Timer()
        timer!!.schedule(timertask, 100, 500)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    companion object {
        const val komboardPackage = "id.android.kmabsensi.utils.komboard.CustomKeyboard"
    }

}