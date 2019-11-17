package id.android.kmabsensi.presentation.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.admin.HomeAdminFragment
import id.android.kmabsensi.presentation.management.HomeManagementFragment
import id.android.kmabsensi.presentation.profile.MyProfileFragment
import id.android.kmabsensi.presentation.report.ReportFragment
import id.android.kmabsensi.presentation.riwayat.RiwayatFragment
import id.android.kmabsensi.presentation.sdm.home.HomeSdmFragment
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private val vm: HomeViewModel by inject()

    var role: String = ""

    lateinit var user: User

    var hasCheckin = false

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.statusBarColor = Color.TRANSPARENT
        }

        hasCheckin = intent.getBooleanExtra("hasCheckin", false)
        val message = intent.getStringExtra("message")
        if (hasCheckin) createAlertSuccess(this, message)

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
                adapter.addFragment(ReportFragment.newInstance())
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
    fun setGreeting() : Pair<String, Int> {
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

        val now = Calendar.getInstance()
        if (now.before(morning)) {
            greeting = "Selamat Pagi, ${user.full_name}"
            header = R.drawable.pagi
        } else if (now.before(noon)) {
            greeting = "Selamat Siang, ${user.full_name}"
            header = R.drawable.siang
        } else if (now.before(afterNoon)) {
            greeting = "Selamat Sore, ${user.full_name}"
            header = R.drawable.sore
        } else if (now.before(evening)) {
            greeting = "Selamat Malam, ${user.full_name}"
            header = R.drawable.malam
        }

        return Pair(greeting, header)
    }

    fun getCountdownTime(time_zuhur: String, time_ashar: String) : Pair<String, Long> {

        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")

        val time_istirahat = "12:00:00"
        val time_istirhat_selesai = "13:00:00"
        val time_pulang = "16:30:00"

        val istirahat = Calendar.getInstance()
        val dzuhur = Calendar.getInstance()
        val selesai_istirahat = Calendar.getInstance()
        val ashar = Calendar.getInstance()
        val pulang = Calendar.getInstance()

        istirahat.set(Calendar.HOUR_OF_DAY, 12)
        selesai_istirahat.set(Calendar.HOUR_OF_DAY, 13)
        pulang.set(Calendar.HOUR_OF_DAY, 17)

        dzuhur.set(Calendar.HOUR_OF_DAY, time_zuhur.split(":")[0].toInt())
        dzuhur.set(Calendar.MINUTE, time_zuhur.split(":")[1].toInt())

        ashar.set(Calendar.HOUR_OF_DAY, time_ashar.split(":")[1].toInt())
        ashar.set(Calendar.MINUTE, time_ashar.split(":")[1].toInt())

        val now = Calendar.getInstance()


        val currentTime = simpleDateFormat.parse(simpleDateFormat.format(now.time))

        var statusWaktu = ""
        var endTime: Date? = null

        val isDzuhurFirst : Boolean = dzuhur.before(istirahat)

        when {
            now.before(if (isDzuhurFirst) dzuhur else istirahat) -> {
                statusWaktu = if (isDzuhurFirst) "Menuju Waktu Dzuhur" else "Menuju Waktu Istirahat"
                endTime = simpleDateFormat.parse(if (isDzuhurFirst) "$time_zuhur:00" else time_istirahat)
            }
            now.before(if (isDzuhurFirst) istirahat else dzuhur) -> {
                statusWaktu = if (isDzuhurFirst) "Menuju Waktu Istirahat" else "Menuju Waktu Dzuhur"
                endTime = simpleDateFormat.parse(if (isDzuhurFirst) time_istirahat else "$time_zuhur:00")
            }
            now.before(selesai_istirahat) -> {
                statusWaktu = "Menuju Waktu Selesai Istirahat"
                endTime = simpleDateFormat.parse(time_istirhat_selesai)
            }
            now.before(time_ashar) -> {
                statusWaktu = "Menuju Waktu Ashar"
                endTime = simpleDateFormat.parse("$time_ashar:00")
            }
            now.before(pulang) -> {
                statusWaktu = "Menuju Waktu Pulang"
                endTime = simpleDateFormat.parse(time_pulang)
            }
            else -> statusWaktu = "Waktu Pulang"
        }

        var differenceTime : Long = 0

        if (endTime != null) {
            differenceTime  = endTime.time - currentTime.time
        }

        return Pair(statusWaktu, differenceTime)
    }

    private fun getFragmentTag(viewId: Int, id: Long): String {
        return "android:switcher:$viewId:$id"
    }

    // for hit api dahsboard in every click beranda page
    private fun getDashboardData(){
        if (role == ROLE_ADMIN){
            val beranda = supportFragmentManager.findFragmentByTag(getFragmentTag(R.id.viewpager, 0)) as HomeAdminFragment
            beranda.getDashboardData()
        }

        if (role == ROLE_MANAGEMEMENT){
            val beranda = supportFragmentManager.findFragmentByTag(getFragmentTag(R.id.viewpager, 0)) as HomeManagementFragment
            beranda.getDashboardData()
        }
    }

}
