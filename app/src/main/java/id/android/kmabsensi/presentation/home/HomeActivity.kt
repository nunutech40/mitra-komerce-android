package id.android.kmabsensi.presentation.home

import android.os.Bundle
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
