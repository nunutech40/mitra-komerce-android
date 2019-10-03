package id.android.kmabsensi.presentation.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.github.ajalt.timberkt.i
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.admin.HomeAdminFragment
import id.android.kmabsensi.presentation.management.HomeManagementFragment
import id.android.kmabsensi.presentation.profile.MyProfileFragment
import id.android.kmabsensi.presentation.report.ReportFragment
import id.android.kmabsensi.presentation.riwayat.RiwayatFragment
import id.android.kmabsensi.presentation.sdm.home.HomeSdmFragment
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    private val vm: HomeViewModel by inject()

    var prevMenuItem: MenuItem? = null

    var role : String = ""

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
                    viewpager.currentItem = if (role == "management") 2 else 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    viewpager.currentItem = if (role == "management") 3 else 2
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

//        role = intent.getStringExtra("role") as String
        role = "sdm"

        when(role){
            "admin" -> {
                nav_view.inflateMenu(R.menu.bottom_nav_menu)
            }
            "management" -> {
                nav_view.inflateMenu(R.menu.bottom_nav_menu_management)
            }
            "sdm" -> {
                nav_view.inflateMenu(R.menu.bottom_nav_menu_sdm)
            }
        }


        setupViewPager()

    }

    private fun setupViewPager() {
        viewpager.offscreenPageLimit = if (role == "management") 4 else 3
        val adapter = ViewPagerAdapter(supportFragmentManager)

        when(role){
            "admin" -> {
                adapter.addFragment(HomeAdminFragment.newInstance())
                adapter.addFragment(ReportFragment.newInstance())
                adapter.addFragment(MyProfileFragment.newInstance())
            }
            "management" -> {
                adapter.addFragment(HomeManagementFragment.newInstance())
                adapter.addFragment(RiwayatFragment.newInstance())
                adapter.addFragment(ReportFragment.newInstance())
                adapter.addFragment(MyProfileFragment.newInstance())
            }
            "sdm" -> {
                adapter.addFragment(HomeSdmFragment.newInstance())
                adapter.addFragment(RiwayatFragment.newInstance())
                adapter.addFragment(MyProfileFragment.newInstance())
            }
        }

        viewpager.adapter = adapter
    }
}
