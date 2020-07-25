package id.android.kmabsensi.presentation.sdm.device

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.FilterDeviceParams
import id.android.kmabsensi.data.remote.response.Device
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.sdm.device.item.DeviceItem
import id.android.kmabsensi.presentation.sdm.device.item.OnDeviceListener
import id.android.kmabsensi.presentation.viewmodels.DeviceViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_data_device.*
import kotlinx.android.synthetic.main.activity_data_device.btnAddDevice
import kotlinx.android.synthetic.main.activity_data_device.rvDevice
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class DataDeviceActivity : BaseActivity() {
    private val deviceVM: DeviceViewModel by viewModel()
    private val partnerVM: PartnerViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val CRUD_RC = 110

    private var noPartnerFilterSelected = 0
    private val partners = mutableListOf<SimplePartner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_device)
        setupToolbar("Data Device", isFilterVisible = true)

        rvDevice.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        observeDevices()
        observeCrudResult()
        observePartners()
        deviceVM.getListDevice()

        btnAddDevice.setOnClickListener {
            startActivityForResult<AddDeviceActivity>(CRUD_RC)
        }

        swipeRefresh.setOnRefreshListener {
            deviceVM.getListDevice()
        }

        btnFilter.setOnClickListener {
            showDialogFilter()
        }
    }

    private fun observePartners() {
        partnerVM.getSimplePartners()
        partnerVM.simplePartners.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    hideSkeleton()
                    if (state.data.status) {
                        partners.addAll(state.data.partners)
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })
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
                        groupAdapter.add(DeviceItem(device, object : OnDeviceListener {
                            override fun onDeleteClicked(id: Int) {
                                showDialogConfirmDelete(this@DataDeviceActivity, "Hapus Device"){
                                    deviceVM.deleteDevice(id)
                                }
                            }

                            override fun onEditClicked(device: Device) {
                                startActivityForResult<AddDeviceActivity>(CRUD_RC, DEVICE_DATA to device, "isEdit" to true)
                            }

                            override fun onDetailClicked(device: Device) {
                                startActivityForResult<AddDeviceActivity>(CRUD_RC, DEVICE_DATA to device, "isEdit" to false)
                            }
                        }))
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })
    }

    private fun observeCrudResult(){
        deviceVM.crudResult.observe(this, Observer { state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status){
                        createAlertSuccess(this, state.data.message)
                        deviceVM.getListDevice()
                    } else {
                        createAlertError(this, "Failed", state.data.message)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CRUD_RC && resultCode == Activity.RESULT_OK){
            deviceVM.getListDevice()
        }
    }



    private fun showDialogFilter() {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_filter_device, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val spinnerPartner = customView.findViewById<Spinner>(R.id.spinnerPartner)
        val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

        val partnerName = mutableListOf<String>()
        partnerName.add("Semua")
        partners.forEach { partnerName.add(it.noPartner + " - " + it.fullName) }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        ArrayAdapter(this, R.layout.spinner_item, partnerName).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPartner.adapter = adapter
            spinnerPartner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    noPartnerFilterSelected = if (position == 0) {
                        0
                    } else {
                        partners[position - 1].noPartner.toInt()
                    }
                }

            }
        }

        buttonFilter.setOnClickListener {
            dialog.dismiss()
            deviceVM.filterDevice(FilterDeviceParams(
                no_partner = noPartnerFilterSelected
            ))
        }

        dialog.show()

    }
}