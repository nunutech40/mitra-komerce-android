package id.android.kmabsensi.services.komboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import com.nex3z.notificationbadge.NotificationBadge
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PrefData
import id.android.kmabsensi.data.pref.PrefData.Companion.KEYBOARD_TYPE
import id.android.kmabsensi.services.windowService.LayoutCekongkir
import id.android.kmabsensi.services.windowService.LayoutResi

class KomboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var btn_CekOngkir: LinearLayout
        @SuppressLint("StaticFieldLeak")
        lateinit var btn_CekResi: LinearLayout
        @SuppressLint("StaticFieldLeak")
        lateinit var btn_CekOrderku: LinearLayout
        @SuppressLint("StaticFieldLeak")
        lateinit var btn_Leads: LinearLayout
        @SuppressLint("StaticFieldLeak")
        lateinit var notif: NotificationBadge
        @SuppressLint("StaticFieldLeak")
        lateinit var layout_menu: LinearLayout

    }
    private lateinit var deletedCharacters: CharSequence
    private lateinit var typedCharacters: CharSequence
    private lateinit var curentText: CharSequence
    private lateinit var keyboardView: KeyboardView
    private lateinit var keyboard: Keyboard
    private var caps = false
    private lateinit var capsState: String
    private lateinit var audioManager: AudioManager
    private lateinit var dataPref: PrefData

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_layout, null)
        keyboardView = root.findViewById(R.id.v_keyboard)
        layout_menu = root.findViewById(R.id.ll_menu)
        btn_CekOngkir = root.findViewById(R.id.ll_cek_ongkir)
        btn_CekOrderku = root.findViewById(R.id.ll_orderku)
        btn_CekResi = root.findViewById(R.id.ll_cek_resi)
        btn_Leads = root.findViewById(R.id.ll_leads)
        notif = root.findViewById(R.id.notif_badge)
        keyboard = Keyboard(this, R.xml.layout_qwerty)
        dataPref = PrefData(applicationContext)
        keyboardView.keyboard = keyboard
        keyboardView.setPreviewEnabled(false)
        keyboardView.setOnKeyboardActionListener(this)
        btn_CekOngkir.setOnClickListener {
            layout_menu.visibility = View.GONE
            startService(Intent(this, LayoutCekongkir::class.java))
        }
        btn_CekResi.setOnClickListener {
            layout_menu.visibility = View.GONE
            startService(Intent(this, LayoutResi::class.java))
        }
        return root
    }
    private fun playClick(keyCode: Int) {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (keyCode) {
            32 -> audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            Keyboard.KEYCODE_DONE, 10 -> audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
            Keyboard.KEYCODE_DELETE -> audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    private fun handleShift() {
        caps = !caps
        keyboard.isShifted = caps
        keyboardView.invalidateAllKeys()
        capsState = if (caps) getString(R.string.on) else getString(R.string.off)
    }

    private fun handleDelete(inputConnection: InputConnection) {
        val selectedText = inputConnection.getSelectedText(0)
        deletedCharacters = inputConnection.getTextBeforeCursor(1, 0).toString()
        if (TextUtils.isEmpty(selectedText)) {
            inputConnection.deleteSurroundingText(1, 0)
            val text = inputConnection.getExtractedText(ExtractedTextRequest(), 0).text
        } else {
            inputConnection.commitText("", 1)
        }
    }

    override fun onPress(primaryCode: Int) {
        playClick(primaryCode)
    }

    override fun onRelease(primaryCode: Int) {
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val inputConnection = currentInputConnection
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> handleDelete(inputConnection)
            Keyboard.KEYCODE_SHIFT -> handleShift()
            Keyboard.KEYCODE_DONE -> inputConnection.sendKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ENTER
                )
            )
            else -> {
                var code = primaryCode.toChar()
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code)
                }
                typedCharacters = code.toString()
                inputConnection.commitText(code.toString(), 1)
                inputConnection.finishComposingText()
                Log.d("onActivedED", "actived...")
                inputConnection.sendKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        EditorInfo.IME_ACTION_SEND
                    )
                )
                curentText = inputConnection.getExtractedText(ExtractedTextRequest(), 0).text
                Log.d("onConnection", "$curentText")
            }
        }
    }

    override fun onText(text: CharSequence?) {
    }

    override fun swipeLeft() {
    }

    override fun swipeRight() {
    }

    override fun swipeDown() {
    }

    override fun swipeUp() {
    }
}