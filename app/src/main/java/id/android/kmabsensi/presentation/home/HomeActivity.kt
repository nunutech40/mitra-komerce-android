package id.android.kmabsensi.presentation.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
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
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private val vm: HomeViewModel by inject()

    var prevMenuItem: MenuItem? = null

    var role: String = ""

    lateinit var user: User

    var hasCheckin = false

    private val FORMAT = "(- %02d:%02d:%02d )"

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewpager.currentItem = 0
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

    fun countDownTimer(ms: Long) {
        try {
            object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {
//                    d { millisUntilFinished.toString() }
//                    if (txtCountdown != null){
//                        txtCountdown.text =
//                        )
//                    }

                    vm.setTimer(String.format(
                        FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                        ),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                        ))
                    )


                }

                override fun onFinish() {
//                    txtCountdown.text = "Waktu Tiba!"
                    vm.setTimer( "Waktu Tiba!")
                }

            }.start()
        } catch (e: Exception) {
            Timber.e(e)
        }

    }
}
