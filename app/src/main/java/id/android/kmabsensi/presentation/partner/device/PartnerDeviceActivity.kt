package id.android.kmabsensi.presentation.partner.device

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.FilterDeviceParams
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.device.item.PartnerDeviceItem
import id.android.kmabsensi.presentation.sdm.device.AddDeviceActivity
import id.android.kmabsensi.presentation.viewmodels.DeviceViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_partner_device.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartnerDeviceActivity : BaseActivity() {

    private val deviceVM: DeviceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var sdm: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_device)
        sdm = intent.getParcelableExtra(USER_KEY)

        setupToolbar("Device ${sdm?.full_name}")

        initRv()

        observeDevice()
        sdm?.let {
            deviceVM.filterDevice(FilterDeviceParams(user_sdm_id = it.id))
        }

        swipeRefresh.setOnRefreshListener {
            sdm?.let {
                deviceVM.filterDevice(FilterDeviceParams(user_sdm_id = it.id))
            }
        }

    }

    private fun observeDevice(){
        deviceVM.devices.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                swipeRefresh.isRefreshing = true
            }
            is UiState.Success -> {
                swipeRefresh.isRefreshing = false
                if (state.data.devices.isEmpty()) layout_empty.visible() else layout_empty.gone()
                state.data.devices.forEach {
                    groupAdapter.add(PartnerDeviceItem(it){
                        startActivity<AddDeviceActivity>(DEVICE_DATA to it, "isEdit" to false)
                    })
                }

            }
            is UiState.Error -> {
                swipeRefresh.isRefreshing = false
            }
        } })
    }

    fun initRv(){
        rvDevice.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }
}