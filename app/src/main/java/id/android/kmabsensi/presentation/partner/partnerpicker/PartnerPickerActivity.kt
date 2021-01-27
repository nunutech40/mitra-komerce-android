package id.android.kmabsensi.presentation.partner.partnerpicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.presentation.base.BaseSearchActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_cari_data_sdm.*
import kotlinx.android.synthetic.main.activity_partner_picker.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartnerPickerActivity : BaseSearchActivity() {

    private val vm: PartnerViewModel by viewModel()
    private val groupAdapter: GroupAdapter<GroupieViewHolder> by inject()

    private var partners = mutableListOf<Partner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_picker)

        setupSearchToolbar("Pilih Partner")

        initRv()
        observePartners()
    }

    private fun observePartners(){
        vm.getPartners()
        vm.partners.observe(this,Observer { state ->
        when(state) {
            is UiState.Loading -> {
                showSkeleton(rvPartners, R.layout.skeleton_list_jabatan, groupAdapter)
            }
            is UiState.Success -> {
                hideSkeleton()
                partners.addAll(state.data.partners)
                if (state.data.status){
                    populateData(partners)
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
            groupAdapter.add(PartnerPickerItem(it){
                val intent = Intent()
                intent.putExtra(PARTNER_DATA_KEY, it)
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }

        if (partners.isEmpty()){
            layout_empty.visible()
        } else {
            layout_empty.gone()
        }
    }

    fun initRv(){
        rvPartners.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider)))
            adapter = groupAdapter
        }
    }

    override fun search(keyword: String) {
        val partners = partners.filter {
            it.fullName.toLowerCase().contains(keyword.toLowerCase()) || it.partnerDetail.noPartner == keyword
        }
        populateData(partners)
    }


    override fun restoreData() {
        populateData(partners)
    }
}
