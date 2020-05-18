package id.android.kmabsensi.presentation.partner.grafik

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Dashboard
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.grafik.tab.GrafikByCategoryFragment
import id.android.kmabsensi.presentation.partner.grafik.tab.GrafikByCityFragment
import id.android.kmabsensi.utils.DASHBOARD_DATA_KEY
import kotlinx.android.synthetic.main.activity_grafik_partner.*

class GrafikPartnerActivity : BaseActivity() {

    // tab titles
    private val tabTitles = arrayOf("Provinsi", "Kategori")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafik_partner)
        setupToolbar("Grafik Persebaran Partner")

        val dashboard: Dashboard? = intent.getParcelableExtra(DASHBOARD_DATA_KEY)

        val fragments = mutableListOf<Fragment>()
        fragments.add(GrafikByCityFragment.newInstance(dashboard))
        fragments.add(GrafikByCategoryFragment.newInstance(dashboard))

        val viewPagerAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        viewPagerAdapter.fragments = fragments
        pager.isUserInputEnabled = false
        pager.adapter = viewPagerAdapter

        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        textJumlahPartner.text = dashboard?.total_partner.toString()

    }
}

class ViewPagerFragmentAdapter(fm: FragmentManager,
                                    lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle){

    var fragments = listOf<Fragment>()

    /* because this is static, i think its ok if i hardcode that size */
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}

