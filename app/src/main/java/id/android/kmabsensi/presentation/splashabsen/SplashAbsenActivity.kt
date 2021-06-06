package id.android.kmabsensi.presentation.splashabsen

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivitySplashAbsenBinding
import id.android.kmabsensi.presentation.home.HomeActivity
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.util.*

class SplashAbsenActivity : AppCompatActivity() {
    private var lottieAnimation = ""
    private var txtMessagePresence = ""
    private var presenceTime = ""
    private var isCheckin = false

    private var isCheckout = false
    private var ontimeLevel = 0
    private var message = ""
    private val binding by lazy { ActivitySplashAbsenBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        try {
            presenceTime = intent.getStringExtra("presenceTime").toString()
            isCheckin = intent.getBooleanExtra("isCheckin", false)
            isCheckout = intent.getBooleanExtra("isCheckout", false)
            message = intent.getStringExtra("message") ?: ""

        }catch (ex: Exception){
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
            Log.d("TAGTAGTAG", "onCreate: $ex")
        }

        if (isCheckin) {
            ontimeLevel = intent.getIntExtra("ontimeLevel", 0)
            showDialogCheckIn(ontimeLevel)
        }

        if (isCheckout) {
            showDialogCheckIn(0)
        }

        Handler(mainLooper).postDelayed({
            startActivity(
                intentFor<HomeActivity>().clearTask().newTask()
            )
        }, 3000)

    }

    private fun showDialogCheckIn(onTimeLevel: Int) {
        val currentTime = Calendar.getInstance()
        val now: Date = currentTime.time

        // for ontime level 3
        val cal4 = Calendar.getInstance()
        cal4.set(Calendar.HOUR_OF_DAY, 8)
        cal4.set(Calendar.MINUTE, 10)
        cal4.set(Calendar.SECOND, 0)
        val jam8Telat: Date = cal4.time

        when (onTimeLevel) {
            0 -> {
                txtMessagePresence = getString(R.string.ket_absen_pulang)
                lottieAnimation = "lf30_success_presence.json"
            }
            1 -> {
                txtMessagePresence = getString(R.string.ket_absen_lebih_awal)
                lottieAnimation = "lf30_success_presence.json"
            }
            2 -> {
                txtMessagePresence = getString(R.string.ket_absen_tepat_waktu)
                lottieAnimation = "lf30_success_presence.json"
            }
            3 -> {
//                val different: Long = now.time - jam8Telat.time
//                var telat = ""
//                var minutes = TimeUnit.MILLISECONDS.toMinutes(different)
//                if (minutes > 60) {
//                    val hour = (different / 1000) / (60 * 60) % 24
//                    val minute = (different / 1000) / 60 % 60
//                    telat = "$hour jam $minute menit"
//                } else {
//                    telat = "$minutes menit"
//                }
                txtMessagePresence = getString(R.string.ket_absen_telat)
                lottieAnimation = "lf30_success_presence.json"
            }
        }
        binding.tvTime.text = presenceTime
        binding.tvMessage.text = txtMessagePresence
        binding.animationView.setAnimation(lottieAnimation)
        binding.animationView.repeatCount = ValueAnimator.INFINITE
        binding.animationView.playAnimation()
    }

}