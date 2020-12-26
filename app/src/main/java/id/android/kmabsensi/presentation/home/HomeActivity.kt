package id.android.kmabsensi.presentation.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.admin.HomeAdminFragment
import id.android.kmabsensi.presentation.management.HomeManagementFragment
import id.android.kmabsensi.presentation.profile.MyProfileFragment
import id.android.kmabsensi.presentation.report.ReportFragment
import id.android.kmabsensi.presentation.report.ReportManajemenFragment
import id.android.kmabsensi.presentation.riwayat.RiwayatFragment
import id.android.kmabsensi.presentation.sdm.home.HomeSdmFragment
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {

    private val vm: HomeViewModel by inject()

    var role: String = ""

    lateinit var user: User

    var isCheckin = false
    var isCheckout = false
    var hasReportPresence = false

    var isOpenGroupMenu = false

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewpager.currentItem = 0
                    getDashboardData()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_history -> {
                    viewpager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_report -> {
                    viewpager.currentItem = if (role == ROLE_MANAGEMEMENT) 2 else 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    viewpager.currentItem = if (role == ROLE_MANAGEMEMENT) 3 else 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        nav_view.itemIconTintList = null

        user = vm.getUserData()

        role = getRoleName(user.role_id)

        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }

        isCheckin = intent.getBooleanExtra("isCheckin", false)
        isCheckout = intent.getBooleanExtra("isCheckout", false)

        if (isCheckin) {
            var ontimeLevel = intent.getIntExtra("ontimeLevel", 0)
            showDialogCheckIn(ontimeLevel)
        }

        if (isCheckout) {
            showDialogCheckIn(0)
        }

        hasReportPresence = intent.getBooleanExtra("hasReportPresence", false)
        val message = intent.getStringExtra("message") ?: ""
        if (hasReportPresence) {
            createAlertSuccess(this, message)
        }

        when (role) {
            ROLE_ADMIN -> {
                nav_view.inflateMenu(R.menu.bottom_nav_menu)
            }
            ROLE_MANAGEMEMENT -> {
                nav_view.inflateMenu(R.menu.bottom_nav_menu_management)
            }
            ROLE_SDM -> {
                nav_view.inflateMenu(R.menu.bottom_nav_menu_sdm)
            }
        }

        setupViewPager()
    }

    fun showDialogNotYetCheckout() {
        val dialog = MaterialDialog(this).show {
            cornerRadius(16f)
            customView(
                R.layout.dialog_keterangan_checkin,
                scrollable = false,
                horizontalPadding = true,
                noVerticalPadding = true
            )
        }
        val customView = dialog.getCustomView()
        val close = customView.findViewById<ImageView>(R.id.close)
        val lottie = customView.findViewById<LottieAnimationView>(R.id.animation_view)
        val txtKeterangan = customView.findViewById<TextView>(R.id.txtKeterangan)

        txtKeterangan.text = getString(R.string.ket_belum_waktu_pulang)
        lottie.setAnimation("5399-loading-17-coffee-cup.json")
        lottie.repeatCount = ValueAnimator.INFINITE
        lottie.playAnimation()
        close.setOnClickListener {
            dialog.dismiss()
        }
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

        // checkout success dialog as default
        val dialog = MaterialDialog(this).show {
            cornerRadius(16f)
            customView(
                R.layout.dialog_keterangan_checkin,
                scrollable = false,
                horizontalPadding = true,
                noVerticalPadding = true
            )
        }
        val customView = dialog.getCustomView()
        val close = customView.findViewById<ImageView>(R.id.close)
        val lottie = customView.findViewById<LottieAnimationView>(R.id.animation_view)
        val txtKeterangan = customView.findViewById<TextView>(R.id.txtKeterangan)
        lottie.setAnimation("433-checked-done.json")

        when (onTimeLevel) {
            1 -> {
                txtKeterangan.text = getString(R.string.ket_absen_lebih_awal)
                lottie.setAnimation("427-happy-birthday.json")
            }
            2 -> {
                txtKeterangan.text = getString(R.string.ket_absen_tepat_waktu)
                lottie.setAnimation("14595-thumbs-up.json")
            }
            3 -> {
                val different: Long = now.time - jam8Telat.time

                var telat = ""

                var minutes = TimeUnit.MILLISECONDS.toMinutes(different)

                if (minutes > 60) {
                    val hour = (different / 1000) / (60 * 60) % 24
                    val minute = (different / 1000) / 60 % 60
                    telat = "$hour jam $minute menit"
                } else {
                    telat = "$minutes menit"
                }

                txtKeterangan.text = getString(R.string.ket_absen_telat, telat)
                lottie.setAnimation("466-stopwatch-via-sketch2ae.json")
            }
        }

        lottie.repeatCount = ValueAnimator.INFINITE
        lottie.playAnimation()
        close.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewpager.offscreenPageLimit = if (role != ROLE_MANAGEMEMENT) 3 else 4
        when (role) {
            ROLE_ADMIN -> {
                adapter.addFragment(HomeAdminFragment.newInstance())
                adapter.addFragment(ReportFragment.newInstance())
                adapter.addFragment(MyProfileFragment.newInstance())
            }
            ROLE_MANAGEMEMENT -> {
                adapter.addFragment(HomeManagementFragment.newInstance())
                adapter.addFragment(RiwayatFragment.newInstance())
                adapter.addFragment(ReportManajemenFragment())
                adapter.addFragment(MyProfileFragment.newInstance())
            }
            ROLE_SDM -> {
                adapter.addFragment(HomeSdmFragment.newInstance())
                adapter.addFragment(RiwayatFragment.newInstance())
                adapter.addFragment(MyProfileFragment.newInstance())
            }
        }

        viewpager.adapter = adapter
    }


    @SuppressLint("SimpleDateFormat")
    fun setGreeting(): Pair<String, Int> {
        var greeting = ""
        var header = 0
        val morning = Calendar.getInstance()
        val noon = Calendar.getInstance()
        val afterNoon = Calendar.getInstance()
        val evening = Calendar.getInstance()

        morning.set(Calendar.HOUR_OF_DAY, 11)
        noon.set(Calendar.HOUR_OF_DAY, 15)
        afterNoon.set(Calendar.HOUR_OF_DAY, 18)
        evening.set(Calendar.HOUR_OF_DAY, 24)

        var name = ""

        if (!user.full_name.isNullOrEmpty()) {
            name = user.full_name.split(" ")[0].toLowerCase().capitalizeWords()
        }

        val now = Calendar.getInstance()
        if (now.before(morning)) {
            greeting = "Selamat Pagi, $name"
            header = R.drawable.pagi
        } else if (now.before(noon)) {
            greeting = "Selamat Siang, $name"
            header = R.drawable.siang
        } else if (now.before(afterNoon)) {
            greeting = "Selamat Sore, $name"
            header = R.drawable.sore
        } else if (now.before(evening)) {
            greeting = "Selamat Malam, $name"
            header = R.drawable.malam
        }

        return Pair(greeting, header)
    }

    fun getCountdownTime(time_zuhur: String?, time_ashar: String?): Triple<String, Long, String> {

        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        var nextTime = ""

        val time_datang = "08:00:00"
        val time_istirahat = "12:00:00"
        val time_istirhat_selesai = "13:00:00"
        val time_pulang = "16:30:00"

        val datang = Calendar.getInstance()
        val istirahat = Calendar.getInstance()
        val selesai_istirahat = Calendar.getInstance()
        val ashar = Calendar.getInstance()
        val pulang = Calendar.getInstance()

        datang.set(Calendar.HOUR_OF_DAY, 8)
        istirahat.set(Calendar.HOUR_OF_DAY, 12)
        selesai_istirahat.set(Calendar.HOUR_OF_DAY, 13)
        pulang.set(Calendar.HOUR_OF_DAY, 17)

        time_ashar?.let {
            if (it.isNotEmpty()){
                ashar.set(Calendar.HOUR_OF_DAY, time_ashar.split(":")[0].toInt())
                ashar.set(Calendar.MINUTE, time_ashar.split(":")[1].toInt())
            }
        }
        val now = Calendar.getInstance()

        val currentTime = simpleDateFormat.parse(simpleDateFormat.format(now.time))

        var statusWaktu = "-"
        var endTime: Date? = null

        when {
            now.before(datang) -> {
                statusWaktu = "Menuju Waktu Datang"
                nextTime = "08:00"
                endTime = simpleDateFormat.parse(time_datang)
            }
            now.before(istirahat) -> {
                statusWaktu = "Menuju Waktu Istirahat"
                nextTime = "12:00"
                endTime = simpleDateFormat.parse(time_istirahat)
            }
            now.before(selesai_istirahat) -> {
                statusWaktu = "Menuju Waktu \nSelesai Istirahat"
                nextTime = "13:00"
                endTime = simpleDateFormat.parse(time_istirhat_selesai)
            }
            now.before(ashar) -> {
                statusWaktu = "Menuju Waktu Ashar"
                nextTime = time_ashar ?: "-"
                endTime = simpleDateFormat.parse("$time_ashar:00")
            }
            now.before(pulang) -> {
                statusWaktu = "Menuju Waktu Pulang"
                nextTime = "16:30"
                endTime = simpleDateFormat.parse(time_pulang)
            }
            else -> statusWaktu = "Waktu Pulang"
        }

        var differenceTime: Long = 0

        if (endTime != null) {
            differenceTime = endTime.time - currentTime.time
        }

        return Triple(statusWaktu, differenceTime, nextTime)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getFragmentTag(viewId: Int, id: Long): String {
        return "android:switcher:$viewId:$id"
    }

    // for hit api dahsboard in every click beranda page
    private fun getDashboardData() {
//        if (role == ROLE_ADMIN) {
//            val beranda = supportFragmentManager.findFragmentByTag(
//                getFragmentTag(
//                    R.id.viewpager,
//                    0
//                )
//            ) as HomeAdminFragment
//            beranda.getDashboardData()
//        }

        if (role == ROLE_MANAGEMEMENT) {
            val beranda = supportFragmentManager.findFragmentByTag(
                getFragmentTag(
                    R.id.viewpager,
                    0
                )
            ) as HomeManagementFragment
            beranda.getDashboardData()
        }
    }

    override fun onBackPressed() {
        if (viewpager.currentItem == 0) {
            if (isOpenGroupMenu) {
                if (role == ROLE_ADMIN) {
                    val beranda = supportFragmentManager.findFragmentByTag(
                        getFragmentTag(
                            R.id.viewpager,
                            0
                        )
                    ) as HomeAdminFragment
                    beranda.hideGroupMenu()
                }
                if (role == ROLE_MANAGEMEMENT) {
                    val beranda = supportFragmentManager.findFragmentByTag(
                        getFragmentTag(
                            R.id.viewpager,
                            0
                        )
                    ) as HomeManagementFragment
                    beranda.hideGroupMenu()
                }
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }


    }

}
