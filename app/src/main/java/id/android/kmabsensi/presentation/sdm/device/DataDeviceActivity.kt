package id.android.kmabsensi.presentation.sdm.device

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.device.AddPartnerDeviceActivity
import id.android.kmabsensi.presentation.sdm.device.item.DeviceItem
import id.android.kmabsensi.presentation.viewmodels.DeviceViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_data_device.*
import kotlinx.android.synthetic.main.activity_data_device.btnAddDevice
import kotlinx.android.synthetic.main.activity_data_device.rvDevice
import kotlinx.android.synthetic.main.activity_partner_device.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class DataDeviceActivity : BaseActivity() {
    private val deviceVM: DeviceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_device)
        setupToolbar("Data Device")

        rvDevice.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        observeDevices()
        deviceVM.getListDevice()

        btnAddDevice.setOnClickListener {
            startActivity<AddDeviceActivity>()
        }
    }

    private fun observeDevices(){
        deviceVM.devices.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    groupAdapter.clear()
                    if (state.data.devices.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    for (device in state.data.devices) {
                        groupAdapter.add(DeviceItem(device))
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })
    }
}