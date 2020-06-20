package id.android.kmabsensi.presentation.partner.sdm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.sdm.SdmItem
import id.android.kmabsensi.presentation.sdm.detail.DetailKaryawanActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.jetbrains.anko.startActivityForResult

class SdmPartnerActivity : BaseActivity() {

    private val vm: PartnerViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var noPartner = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdm_partner)

        noPartner = intent.getStringExtra(NO_PARTNER_KEY)
        val partnerName = intent.getStringExtra(NAME_PARTNER_KEY)

        setupToolbar("SDM $partnerName")
        initRv()

        vm.getSdmByPartner(noPartner.toInt())

        vm.sdmByPartner.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showSkeleton(rvSdm, R.layout.skeleton_list_sdm, groupAdapter)
                }
                is UiState.Success -> {
                    hideSkeleton()
                    if (state.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    state.data.data.forEach { sdm ->
                        groupAdapter.add(SdmItem(sdm) {
                            startActivityForResult<DetailKaryawanActivity>(
                                112,
                                USER_KEY to it,
                                IS_MANAGEMENT_KEY to false
                            )
                        })
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })

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
            vm.getSdmByPartner(noPartner.toInt())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}