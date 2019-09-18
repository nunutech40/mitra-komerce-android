package id.android.kmabsensi.presentation.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragmentList = mutableListOf<Fragment>()

    override fun getItem(p0: Int): Fragment = mFragmentList[p0]

    override fun getCount(): Int = mFragmentList.size

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }
}