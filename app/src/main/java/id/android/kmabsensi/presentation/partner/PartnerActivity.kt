package id.android.kmabsensi.presentation.partner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.CustomizePartnerActivity.Companion.sortData
import id.android.kmabsensi.presentation.partner.detail.DetailPartnerActivity
import id.android.kmabsensi.presentation.partner.sdm.SdmPartnerActivity
import id.android.kmabsensi.presentation.partner.search.SearchPartnerActivity
import id.android.kmabsensi.presentation.partner.tambahpartner.FormPartnerActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_partner.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Double.parseDouble

class PartnerActivity : BaseActivity() {

    private val vm: PartnerViewModel by viewModel()

    private val partners = mutableListOf<Partner>()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner)

        setupToolbar(getString(R.string.text_data_partner), isSearchVisible = true)

        initRv()

        buttonSort.setOnClickListener {
            startActivityForResult<CustomizePartnerActivity>(
                CUSTOMIZE_RC, IS_SORT_KEY to true
            )
        }

        buttonFilter.setOnClickListener {
            startActivityForResult<CustomizePartnerActivity>(
                CUSTOMIZE_RC, IS_SORT_KEY to false
            )
        }

        btnAddPartner.setOnClickListener {
            startActivityForResult<FormPartnerActivity>(CRUD_PARTNER_RC)
        }

        vm.getPartners()
        observeData()

    }

    private fun observeData(){
        vm.partners.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                showSkeleton(rvPartner, R.layout.skeleton_list_partner, groupAdapter)
            }
            is UiState.Success -> {
                hideSkeleton()
                if (state.data.status){
                    if (partners.isNotEmpty()) partners.clear()
                    if (state.data.partners.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    partners.addAll(state.data.partners)
                    populateData(partners)
                    btnSearch.setOnClickListener {
                        startActivityForResult<SearchPartnerActivity>(CRUD_PARTNER_RC,
                            PARTNER_RESPONSE_KEY to state.data
                        )
                    }
                }

            }
            is UiState.Error -> {
                hideSkeleton()
            }
        } })
    }

    private fun populateData(partners: List<Partner>){
        partners.forEach {
            groupAdapter.add(PartnerItem(it, object: OnParterItemClicked{
                override fun onPartnerClicked(partner: Partner) {
                    startActivityForResult<DetailPartnerActivity>(CRUD_PARTNER_RC, PARTNER_DATA_KEY to it)
                }

                override fun onBtnTotalSdmClicked(noPartner: String, partnerName: String) {
                    startActivity<SdmPartnerActivity>(NO_PARTNER_KEY to noPartner,
                        NAME_PARTNER_KEY to partnerName)
                }

            }))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CUSTOMIZE_RC && resultCode == Activity.RESULT_OK){
            var isNumeric = true
            val content = data?.getStringExtra("content")
            groupAdapter.clear()
            layout_empty.gone()
            try {
                content?.let {
                    val num = parseDouble(content)
                }
            } catch (e: NumberFormatException) {
                isNumeric = false
            }

            if (content == sortData[0]) { /* urut berdasarkan jumlah terbanyak */
                val sortPartners= partners.sortedByDescending { it.totalSdmAssigned }
                populateData(sortPartners)
            } else if (content == sortData[1]){ /* urut berdasarkan jumlah terkceil */
                val sortPartners = partners.sortedBy { it.totalSdmAssigned }
                populateData(sortPartners)
            } else if (isNumeric){
                val filteredPartnerByLeader = partners.filter { it.userManagementId == content!!.toInt() }
                populateData(filteredPartnerByLeader)
            } else if (content != "Semua"){
                val filteredPartner = partners.filter { it.partnerDetail.partnerCategoryName == content }
                if (filteredPartner.isNotEmpty()){
                    populateData(filteredPartner)
                } else {
                    layout_empty.visible()
                }
            }  else {
                populateData(partners)
            }


        } else if (requestCode == CRUD_PARTNER_RC && resultCode == Activity.RESULT_OK){
            val message = data?.getStringExtra("message")
            createAlertSuccess(this, message.toString())
            groupAdapter.clear()
            vm.getPartners()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun initRv() {
        rvPartner.apply {
            layoutManager = LinearLayoutManager(this@PartnerActivity)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.divider
                    ), true
                )
            )
            adapter = groupAdapter
        }
    }

    companion object {
        const val CUSTOMIZE_RC = 123
        const val CRUD_PARTNER_RC = 122
    }
}
