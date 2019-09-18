package id.android.kmabsensi.presentation.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.admin.HomeAdminFragment
import id.android.kmabsensi.presentation.profile.MyProfileFragment
import id.android.kmabsensi.presentation.report.ReportFragment
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    var prevMenuItem: MenuItem? = null

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewpager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_report -> {

                    viewpager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    viewpager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setupViewPager()

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    nav_view.menu.getItem(0).isChecked = false
                }
                Timber.d("onPageSelected: $position")
                nav_view.menu.getItem(position).isChecked = true
                prevMenuItem = nav_view.menu.getItem(position)

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun setupViewPager() {
        viewpager.offscreenPageLimit = 3
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeAdminFragment.newInstance())
        adapter.addFragment(ReportFragment.newInstance())
        adapter.addFragment(MyProfileFragment.newInstance())
        viewpager.adapter = adapter
    }
}
