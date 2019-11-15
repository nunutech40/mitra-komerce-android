package id.android.kmabsensi.presentation.permission.manajemenizin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject

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

//        setSupportActionBar(toolbar)
//        supportActionBar?.title = "Manajemen Izin"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setToolbarTitle("Manajemen Izin")

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
