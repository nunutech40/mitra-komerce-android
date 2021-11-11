package id.android.kmabsensi.presentation.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.ActivityHomeBinding
import id.android.kmabsensi.presentation.admin.HomeAdminFragment
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.management.HomeManagementFragment
import id.android.kmabsensi.presentation.profile.MyProfileFragment
import id.android.kmabsensi.presentation.report.ReportFragment
import id.android.kmabsensi.presentation.report.ReportManajemenFragment
import id.android.kmabsensi.presentation.riwayat.RiwayatFragment
import id.android.kmabsensi.presentation.sdm.home.HomeSdmFragment
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import java.util.*


class HomeActivity : BaseActivityRf<ActivityHomeBinding>(
    ActivityHomeBinding::inflate
){

    private val vm: HomeViewModel by inject()

    private var role: String = ""

    lateinit var user: User

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
        nav_view.apply {
            setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
            itemIconTintList = null
        }
        setContentView(R.layout.activity_home)

        user = vm.getUserData()
        vm.isNormal(user)
        role = getRoleName(user.role_id)

        hasReportPresence = intent.getBooleanExtra("hasReportPresence", false)
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
        val time_pulang = if (vm.isNormal(user)) "16:00:00" else "17:00:00"

        val datang = Calendar.getInstance()
        val istirahat = Calendar.getInstance()
        val selesai_istirahat = Calendar.getInstance()
        val ashar = Calendar.getInstance()
        val pulang = Calendar.getInstance()

        datang.set(Calendar.HOUR_OF_DAY, 8)
        istirahat.set(Calendar.HOUR_OF_DAY, 12)
        selesai_istirahat.set(Calendar.HOUR_OF_DAY, 13)
        if (vm.isNormal(user)) pulang.set(Calendar.HOUR_OF_DAY, 16) else pulang.set(Calendar.HOUR_OF_DAY, 17)

        time_ashar?.let {
            if (it.isNotEmpty()) {
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
                nextTime = if (vm.isNormal(user)) "16:00" else "17:00"
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
