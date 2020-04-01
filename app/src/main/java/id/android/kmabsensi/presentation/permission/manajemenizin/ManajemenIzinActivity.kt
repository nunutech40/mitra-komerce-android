package id.android.kmabsensi.presentation.permission.manajemenizin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.permission.PermissionItem
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.presentation.permission.detailizin.DetailIzinActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_manajemen_izin.*
import kotlinx.android.synthetic.main.dialog_filter_permission.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.util.*

class ManajemenIzinActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val roles = mutableListOf("Management", "SDM")

    var roleId = 0

    var isManagement = false
    var userManagementId = 0

    lateinit var myDialog: MyDialog

    var REQUEST_PENGAJUAN_IZIN = 152

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manajemen_izin)

        setupToolbar("Manajemen Izin", isFilterVisible = true)

        myDialog = MyDialog(this)

        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)
        userManagementId = intent.getIntExtra(USER_ID_KEY, 0)

        if (isManagement) roles.removeAt(0)

        initRv()

        vm.listPermissionData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    progressBar.gone()
                    if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    it.data.data.forEach {
                        groupAdapter.add(PermissionItem(it){ permission ->
                            startActivityForResult<DetailIzinActivity>(REQUEST_PENGAJUAN_IZIN, PERMISSION_DATA_KEY to it,
                                IS_FROM_MANAJEMEN_IZI to true)
                        })
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })

        vm.approvePermissionResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) createAlertSuccess(this, it.data.message) else createAlertError(this, "Gagal", it.data.message)
                    vm.getListPermission(roleId = roleId, userManagementId = userManagementId)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEmployetype.adapter = adapter

            spinnerEmployetype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    roleId = if (isManagement) position+3 else position+2
                    vm.getListPermission(roleId = roleId, userManagementId = userManagementId)
                }

            }
        }

        btnFilter.setOnClickListener {
            showDialogFilter()
        }
    }

    private fun showDialogFilter(){
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_filter_permission, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val edtStartDate = customView.findViewById<AppCompatEditText>(R.id.edtStartDate)
        val edtEndDate = customView.findViewById<AppCompatEditText>(R.id.edtEndDate)
        val spinnerPermission = customView.findViewById<Spinner>(R.id.spinnerPermission)
        val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        edtStartDate.setOnClickListener { view ->
            showDatePicker(){
                edtStartDate.setText(getDateStringFormatted(it))
            }
        }

        edtEndDate.setOnClickListener { view ->
            showDatePicker(){
                edtEndDate.setText(getDateStringFormatted(it))
            }
        }

        // spinner izin
        ArrayAdapter.createFromResource(
            this,
            R.array.permission,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPermission.adapter = adapter

            spinnerPermission.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val permissionType = position + 1
                }

            }
        }




        dialog.show()

    }

    private fun showDatePicker(callback: (Date) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                callback(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

    }

    private fun initRv(){
        rvPermission.apply {
            layoutManager = LinearLayoutManager(this@ManajemenIzinActivity)
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_PENGAJUAN_IZIN && resultCode == Activity.RESULT_OK){
            vm.getListPermission(roleId = roleId, userManagementId = userManagementId)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
