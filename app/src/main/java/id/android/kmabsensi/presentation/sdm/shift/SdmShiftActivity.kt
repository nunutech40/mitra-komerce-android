package id.android.kmabsensi.presentation.sdm.shift

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.UpdateSdmShiftConfigParam
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseSearchActivity
import id.android.kmabsensi.presentation.viewmodels.UserConfigurationViewModel
import id.android.kmabsensi.utils.UiState
import kotlinx.android.synthetic.main.activity_sdm_shift.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SdmShiftActivity : BaseSearchActivity() {
    private val userConfigurationVm: UserConfigurationViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var user: User
    private var selectedPosition: Int = 0
    private var selectedUser: User? = null
    private var sdmShiftList: MutableList<User> = mutableListOf()

    companion object {
        const val SHIFT_PAGI = "PAGI"
        const val SHIFT_SIANG = "SIANG"
    }

    override fun search(keyword: String) {
        val listSdm = sdmShiftList.filter {
            it.full_name.toLowerCase().contains(keyword.toLowerCase())
        }
        populateData(listSdm)
    }

    override fun restoreData() {
        populateData(sdmShiftList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdm_shift)
        setupSearchToolbar(getString(R.string.shift))
        user = userConfigurationVm.getUserData()
        initRv()
        observeData()
        userConfigurationVm.getsSmShiftConfiguration(userManagementId = user.id)
    }

    fun observeData() {
        userConfigurationVm.sdmShiftConfigurationResult.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showSkeleton(rvSdmShiftConfig, R.layout.skeleton_list_sdm, groupAdapter)
                }
                is UiState.Success -> {
                    hideSkeleton()
                    sdmShiftList.clear()
                    sdmShiftList.addAll(state.data.data)
                    populateData(sdmShiftList)
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })

        userConfigurationVm.updateSdmShiftResult.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status){

                        userConfigurationVm.getsSmShiftConfiguration(userManagementId = user.id)

//                        selectedUser?.sdm_config = state.data.data
//                        selectedUser?.let {
//                            sdmShiftList.set(selectedPosition, it)
//                        }
//                        populateData(sdmShiftList)
                    }
                }
                is UiState.Error -> {
                    hideDialog()

                }

            }

        })

    }

    private fun populateData(list: List<User>){
        groupAdapter.clear()
        list.forEach {
            groupAdapter.add(SdmShiftItem(this, it, object : OnSdmShiftListener {

                override fun onShiftPagiSelected(position: Int, user: User) {
                    selectedPosition = position
                    selectedUser = user
                    val param = UpdateSdmShiftConfigParam(
                        id = user.sdm_config?.id,
                        user_id = user.id,
                        shift_mode = SHIFT_PAGI
                    )
                    userConfigurationVm.updateSdmShiftResult(param)
                }

                override fun onShiftSiangSelected(position: Int, user: User) {
                    selectedPosition = position
                    selectedUser = user
                    val param = UpdateSdmShiftConfigParam(
                        id = user.sdm_config!!.id,
                        user_id = user.id,
                        shift_mode = SHIFT_SIANG
                    )
                    userConfigurationVm.updateSdmShiftResult(param)
                }
            }))
        }
    }

    fun initRv() {
        rvSdmShiftConfig.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }
}