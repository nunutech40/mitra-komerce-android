package id.android.kmabsensi.presentation.sdm.holiday

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.HolidayViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_holiday.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HolidayActivity : BaseActivity() {

    private val holidayVM: HolidayViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holiday)
        setupToolbar(getString(R.string.hari_libur_label))
        observeData()

        holidayVM.getHoliday()

        swipeRefresh.setOnRefreshListener {
            holidayVM.getHoliday()
        }
    }

    fun observeData(){
        holidayVM.holidays.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    if (state.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    state.data.data.forEach {
                        groupAdapter.add(HolidayItem(it))
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })
    }

    fun initRv(){
        rvHoliday.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }
}