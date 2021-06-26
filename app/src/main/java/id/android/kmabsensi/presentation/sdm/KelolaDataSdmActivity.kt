package id.android.kmabsensi.presentation.sdm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.ActivityKelolaDataSdmBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.adapter.KelolaSdmAdapter
import id.android.kmabsensi.presentation.sdm.detail.DetailKaryawanActivity
import id.android.kmabsensi.presentation.sdm.search.CariDataSdmActivity
import id.android.kmabsensi.presentation.sdm.tambahsdm.TambahSdmActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject

class KelolaDataSdmActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by inject()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val roles = mutableListOf<String>("Management", "SDM")
    private var userManagements = mutableListOf<User>()
    private var filteredLeaderList: List<User> = listOf()
    private var roleId = 0

    private var isManagement = false
    private var userManagementId = 0
    private var selectedUserManajemenLeaderId = 0
    private lateinit var kelolaSdmAdapter: KelolaSdmAdapter

    private val binding by lazy { ActivityKelolaDataSdmBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar(getString(R.string.text_kelola_sdm))
        binding.toolbar2.btnSearch.visible()

        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)
        userManagementId = intent.getIntExtra(USER_ID_KEY, 0)
        if (isManagement){
            roles.removeAt(0)
            binding.btnTambahSdm.gone()
        }
        initRv()
        initData()
        setListener()
    }

    private fun setListener() {
        binding.toolbar2.btnSearch.setOnClickListener {
            startActivityForResult<CariDataSdmActivity>(
                    121,
                    IS_MANAGEMENT_KEY to isManagement
            )
        }
        binding.btnTambahSdm.setOnClickListener {
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
                    it.data.data.forEach {
                        if (it.position_name != null) userManagements.add(it)
                    }

                    filteredLeaderList =
                        userManagements.filter {
                            it.position_name.toLowerCase().contains(getString(R.string.category_leader).toLowerCase()) }

                    val userManagementNames = mutableListOf<String>()
                    userManagementNames.add(getString(R.string.text_semua_leader_spinner))
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
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
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
                                    vm.getUserDataWithPagedList(roleId, selectedUserManajemenLeaderId, selectedUserManajemenLeaderId).observe(this@KelolaDataSdmActivity, {
                                        kelolaSdmAdapter.submitList(it)
                                    })
//                                    filterDataSdmByLeader()
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
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            spinnerFilterLeader.gone()
                            selectedUserManajemenLeaderId = 0
                        } else {
                            spinnerFilterLeader.visible()
                            selectedUserManajemenLeaderId = 0
                        }
                        groupAdapter.clear()
                        roleId = if (isManagement) position + 3 else position + 2
                        observeUserDao()
                    }

                }
        }
        vm.getUserManagement(2)
    }

    private fun observeUserDao(isFilter: Boolean = false) {

        vm.getUserDataWithPagedList(
                roleId,
                userManagementId,
                selectedUserManajemenLeaderId).observe(this@KelolaDataSdmActivity, Observer {
            kelolaSdmAdapter.submitList(it)
        })
        vm.getState().observe(this, Observer {state ->
            when(state){
                State.LOADING-> {
                    showSkeletonPaging(rvSdm, R.layout.skeleton_list_sdm, kelolaSdmAdapter)
                }
                State.DONE-> hideSkeletonPaging()
                State.ERROR-> {
                    Log.d("_state", "ERROR")
                    hideSkeletonPaging()
                }
            }
        })
        if (isFilter){
            vm.filterUser(selectedUserManajemenLeaderId).observe(this, Observer {state ->
                when(state){
                    is UiState.Loading-> {
                        Log.d("_filterManagement", "LOADING ISFILTER")
                        showSkeleton(rvSdmFilter, R.layout.skeleton_list_sdm, groupAdapter)
                    }
                    is UiState.Success ->{
                        Log.d("_filterManagement", "SUCCESS ISFILTER data = ${state.data}")
                        hideSkeleton()
                        if (state.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                        groupAdapter.clear()
                        state.data.data.reversed().forEach { sdm ->
                            groupAdapter.add(SdmItem(sdm) {
                                startActivityForResult<DetailKaryawanActivity>(
                                        121,
                                        USER_KEY to it,
                                        IS_MANAGEMENT_KEY to isManagement
                                )
                            })
                        }
                    }
                    is UiState.Error ->{
                        hideSkeleton()
                        Log.d("_filterManagement", state.throwable.message.toString())
                    }
                }
            })
        }
    }

    private fun filterDataSdmByLeader() {
        if (selectedUserManajemenLeaderId == 0) {
            observeUserDao(isFilter = false)
            binding.rvSdmFilter.gone()
            binding.rvSdm.visible()
        } else {
            observeUserDao(isFilter = true)
            binding.rvSdmFilter.visible()
            binding.rvSdm.gone()
        }
    }

    fun initRv() {
        kelolaSdmAdapter = KelolaSdmAdapter(this, object : KelolaSdmAdapter.onAdapterListener {
            override fun onClicked(user: User) {
                Log.d("TAGTAGTAG", "onClicked: $user")
                startActivityForResult<DetailKaryawanActivity>(
                        121,
                        IS_MANAGEMENT_KEY to isManagement,
                        USER_KEY to user
                )
            }
        })

        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvSdm.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = kelolaSdmAdapter
        }

        binding.rvSdmFilter.apply {
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 121 && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra("message")
            createAlertSuccess(this, message.toString())
            groupAdapter.clear()
            vm.getUserDataWithPagedList(roleId, userManagementId, selectedUserManajemenLeaderId)
            vm.getUserManagement(2)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
