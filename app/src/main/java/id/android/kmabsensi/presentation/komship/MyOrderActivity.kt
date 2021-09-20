package id.android.kmabsensi.presentation.komship

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import id.android.kmabsensi.databinding.ActivityMyOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import org.jetbrains.anko.toast

class MyOrderActivity : BaseActivityRf<ActivityMyOrderBinding>(
    ActivityMyOrderBinding::inflate
) {
    private lateinit var pagerAdapter : MyOrderPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Orderku", isBackable = true, isOrder = true)
        setupPager()

    }

    private fun setupPager() {
        pagerAdapter = MyOrderPagerAdapter(supportFragmentManager)
        binding.viewPager.apply {
            adapter = pagerAdapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    when(position){
                        0 -> setupToolbar("Orderku", isBackable = true, isOrder = true)
                        1 -> setupToolbar("Orderku", isBackable = true, isSearch = true, isFilter = true)
                        2 -> setupToolbar("Orderku", isBackable = true, isFilter = true)
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
        }
        binding.tabLayout.apply {
            setupWithViewPager(binding.viewPager)
        }

    }
}