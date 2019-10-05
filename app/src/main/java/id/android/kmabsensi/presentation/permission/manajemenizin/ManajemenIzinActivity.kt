package id.android.kmabsensi.presentation.permission.manajemenizin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.permission.PermissionItem
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.utils.IS_MANAGEMENT_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.ui.MyDialog
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_manajemen_izin.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class ManajemenIzinActivity : AppCompatActivity() {

    private val vm: PermissionViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val roles = mutableListOf("Management", "SDM")

    val actions = listOf("Approved", "Rejected")

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

        initRv()

        vm.listPermissionData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    progressBar.gone()
                    it.data.data.forEach {
                        groupAdapter.add(PermissionItem(it){ permission ->
                            MaterialDialog(this).show {
                                cornerRadius(10f)
                                title(text = "Action")
                                listItems(items = actions){ dialog, index, text ->
                                    if (index == 0){
                                        vm.approvePermission(permission.id, 2)
                                    } else {
                                        vm.approvePermission(permission.id, 3)
                                    }

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
                    vm.getListPermission(roleId = roleId)
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
