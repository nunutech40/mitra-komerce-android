package id.android.kmabsensi.presentation.partner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import kotlinx.android.synthetic.main.activity_cari_data_sdm.*
import kotlinx.android.synthetic.main.activity_partner.*
import kotlinx.android.synthetic.main.activity_partner.layout_empty
import kotlinx.android.synthetic.main.activity_partner.rvPartner
import kotlinx.android.synthetic.main.activity_search_partner.*
import kotlinx.android.synthetic.main.edittext_search.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Double.parseDouble

class PartnerActivity : BaseActivity() {

    private val vm: PartnerViewModel by viewModel()

    private val partners = mutableListOf<Partner>()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var mHandler = Handler()

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

        searchView.et_search.addTextChangedListener {
            /* Ketika query kosong, maka tampil view not found */
            if (searchView.et_search.text.isNullOrEmpty()) {
                populateData(partners)
                return@addTextChangedListener
            }

            layout_empty.visibility = View.GONE
            rvPartner.visibility = View.VISIBLE
            handleOnTextChange(searchView.et_search.text.toString())
        }

    }

    private fun handleOnTextChange(keyword: String) {
        mHandler.removeCallbacksAndMessages(null)
        if (keyword.isNotEmpty()) {
            mHandler.postDelayed({
                search(keyword)
            }, 500)
        }
    }

    private fun search(keyword: String) {
        val partners = partners.filter {
            it.fullName.toLowerCase().contains(keyword.toLowerCase()) || it.noPartner == keyword
        }
        populateData(partners)
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
//                    btnSearch.setOnClickListener {
//                        startActivityForResult<SearchPartnerActivity>(CRUD_PARTNER_RC,
//                            PARTNER_RESPONSE_KEY to state.data
//                        )
//                    }
                }

            }
            is UiState.Error -> {
                hideSkeleton()
            }
        } })
    }

    private fun populateData(partners: List<Partner>){
        groupAdapter.clear()
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
                buttonFilter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter_grey, 0, 0, 0)
                buttonSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_active, 0, 0, 0)
                val sortPartners= partners.sortedByDescending { it.totalSdmAssigned }
                populateData(sortPartners)
            } else if (content == sortData[1]){ /* urut berdasarkan jumlah terkceil */
                buttonFilter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter_grey, 0, 0, 0)
                buttonSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_active, 0, 0, 0)
                val sortPartners = partners.sortedBy { it.totalSdmAssigned }
                populateData(sortPartners)
            } else if (isNumeric){
                buttonFilter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter_active, 0, 0, 0)
                buttonSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_grey, 0, 0, 0)
                val filteredPartnerByLeader = partners.filter { it.leadersAssigned.find { it.id == content!!.toInt() } != null }
                populateData(filteredPartnerByLeader)
            } else if (content != "Semua"){
                buttonFilter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter_active, 0, 0, 0)
                buttonSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_grey, 0, 0, 0)
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
            adapter = groupAdapter
        }
    }

    companion object {
        const val CUSTOMIZE_RC = 123
        const val CRUD_PARTNER_RC = 122
    }

    override fun onBackPressed() {
        if (isSearchMode){
            isSearchMode = false
            populateData(partners)
            toolbarContent.visibility = View.GONE
            toolbarContent.removeView(searchView)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        /* hide keyboard when leave */
        val imm: InputMethodManager? =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(searchView.windowToken, 0)
        super.onStop()
    }
}
