package id.android.kmabsensi.presentation.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_invoice.*

class InvoiceActivity : BaseActivity() {

    // tab titles
    private val tabTitles = arrayOf("Aktif", "Riwayat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        setupToolbar("Invoice", isFilterVisible = true)

        pager.isUserInputEnabled = false
        pager.adapter = ViewPagerOrderFragmentAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}

class ViewPagerOrderFragmentAdapter(fm: FragmentManager,
                                    lifecycle: Lifecycle
): FragmentStateAdapter(fm, lifecycle){

    /* because this is static, i think its ok if i hardcode that size */
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> InvoiceActiveFragment()
            else -> HistoryInvoiceFragment()
        }
    }

}
