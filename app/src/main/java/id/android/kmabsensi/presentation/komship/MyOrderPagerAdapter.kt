package id.android.kmabsensi.presentation.komship

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import id.android.kmabsensi.presentation.komship.dataorder.HistoryOrderFragment
import id.android.kmabsensi.presentation.komship.leads.LeadsOrderFragment
import id.android.kmabsensi.presentation.komship.myorder.MyOrderFragment

class MyOrderPagerAdapter(fm : FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // sebuah list yang menampung objek Fragment
    private val pages = listOf(
        MyOrderFragment(),
        HistoryOrderFragment(),
        LeadsOrderFragment()
    )

    override fun getCount(): Int = pages.size

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "Orderku"
            1 -> "Data Order"
            else -> "Leads"
        }

    }
}