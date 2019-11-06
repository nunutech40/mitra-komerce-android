package id.android.kmabsensi.presentation.sdm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.detail.DetailKaryawanActivity
import id.android.kmabsensi.presentation.sdm.search.CariDataSdmActivity
import id.android.kmabsensi.presentation.sdm.tambahsdm.TambahSdmActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject

class KelolaDataSdmActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val roles = mutableListOf<String>("Management", "SDM")
    var userManagements = mutableListOf<User>()
    var filteredLeaderList: List<User> = listOf()
    var roleId = 0

    var isManagement = false
    var userManagementId = 0
    var selectedUserManajemenLeaderId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_data_sdm)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kelola Data Karyawan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)
        userManagementId = intent.getIntExtra(USER_ID_KEY, 0)
        if (isManagement) roles.removeAt(0)

        initRv()
        initData()
        setListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search -> {
                startActivity<CariDataSdmActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setListener() {
        btnTambahSdm.setOnClickListener {
            startActivityForResult<TambahSdmActivity>(
                121, IS_MANAGEMENT_KEY to isManagement,
                USER_ID_KEY to userManagementId
            )
        }
    }

    private fun initData() {
        observeUserDao()
        vm.userManagementData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    userManagements.addAll(it.data.data)

                    filteredLeaderList =
                        userManagements.filter { it.position_name.toLowerCase().contains("leader") }

                    val userManagementNames = mutableListOf<String>()
                    userManagementNames.add("- Semua Leader -")
                    filteredLeaderList.forEach { userManagementNames.add(it.full_name) }
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        userManagementNames
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerFilterLeader.adapter = adapter

                        spinnerFilterLeader.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    if (position == 0) {
                                        selectedUserManajemenLeaderId = 0
                                    } else {
                                        selectedUserManajemenLeaderId =
                                            filteredLeaderList[position - 1].id
                                    }

                                    filterDataSdmByLeader()
                                }

                            }
                    }
                }
                is UiState.Error -> {
                    Timber.e { it.throwable.message.toString() }
                }
            }
        })

        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEmployetype.adapter = adapter

            spinnerEmployetype.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            spinnerFilterLeader.gone()
                        } else {
                            spinnerFilterLeader.visible()

                        }

                        groupAdapter.clear()
                        roleId = if (isManagement) position + 3 else position + 2
                        vm.getUserData(roleId, userManagementId)
                    }

                }
        }
        vm.getUserManagement(2)

    }

    private fun observeUserDao(isFilter: Boolean = false) {
        vm.userData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()

                    val dataFilter: List<User>

                    if (isFilter) {
                         dataFilter = it.data.data.filter { it.user_management_id == selectedUserManajemenLeaderId }
                    } else {
                        dataFilter = it.data.data
                    }

                    if (dataFilter.isEmpty()) layout_empty.visible() else layout_empty.gone()

                    groupAdapter.clear()
                    dataFilter.forEach { sdm ->
                        groupAdapter.add(SdmItem(sdm) {
                            startActivityForResult<DetailKaryawanActivity>(
                                121,
                                USER_KEY to it,
                                IS_MANAGEMENT_KEY to isManagement
                            )
                        })
                    }

                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })

    }

    private fun filterDataSdmByLeader() {
        if (selectedUserManajemenLeaderId == 0) {
            observeUserDao()
        } else {
            observeUserDao(isFilter = true)
        }
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvSdm.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 121 && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra("message")
            createAlertSuccess(this, message.toString())
            groupAdapter.clear()
            vm.getUserData(roleId, userManagementId)
            vm.getUserManagement(2)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
