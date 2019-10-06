package id.android.kmabsensi.presentation.permission.manajemenizin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.permission.PermissionItem
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_manajemen_izin.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class ManajemenIzinActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val roles = mutableListOf("Management", "SDM")

    var roleId = 0

    var isManagement = false
    var userManagementId = 0

    lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manajemen_izin)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Manajemen Izin"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
                    if (it.data.data.isEmpty()) txtEmpty.visible() else txtEmpty.gone()
                    it.data.data.forEach {
                        groupAdapter.add(PermissionItem(it){ permission ->
                            MaterialDialog(this).show {
                                cornerRadius(10f)
                                message(text = "Approve this permission request?")
                                positiveButton(text = "Approve"){
                                    it.dismiss()
                                    vm.approvePermission(permission.id, 2)
                                }
                                negativeButton(text = "Rejected"){
                                    it.dismiss()
                                    vm.approvePermission(permission.id, 3)

                                }
                            }
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
                    toast(it.data.message)
                    vm.getListPermission(roleId = roleId)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
}
