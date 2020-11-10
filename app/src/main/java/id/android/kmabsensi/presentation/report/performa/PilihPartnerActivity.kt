package id.android.kmabsensi.presentation.report.performa

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.presentation.base.BaseSearchActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.partnerpicker.SimplePartnerItem
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_pilih_partner.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PilihPartnerActivity : BaseSearchActivity() {

    private val vm: PartnerViewModel by viewModel()
    private val groupAdapter: GroupAdapter<GroupieViewHolder> by inject()

    private var partners = mutableListOf<SimplePartner>()

    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_partner)
        setupSearchToolbar("Pilih Partner")

        userId = intent.getIntExtra(USER_ID_KEY, 0)
        initRv()
        observePartners()
    }

    private fun observePartners(){
        vm.getPartnerByManagement(userId)
        vm.partnerByManagement.observe(this, Observer { state ->
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

    private fun populateData(partners: List<SimplePartner>){
        groupAdapter.clear()
        partners.forEach {
            groupAdapter.add(SimplePartnerItem(it){
                startActivity<PerformaActivity>(NO_PARTNER_KEY to it.noPartner)
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
            it.fullName.toLowerCase().contains(keyword.toLowerCase()) || it.noPartner == keyword
        }
        populateData(partners)
    }


    override fun restoreData() {
        populateData(partners)
    }
}