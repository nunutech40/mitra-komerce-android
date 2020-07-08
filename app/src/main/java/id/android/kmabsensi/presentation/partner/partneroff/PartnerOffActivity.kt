package id.android.kmabsensi.presentation.partner.partneroff

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseSearchActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.sdm.nonjob.SdmNonJobItem
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_partner_off.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartnerOffActivity : BaseSearchActivity() {

    private val partnerVM: PartnerViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val partners = mutableListOf<Partner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_off)
        setupSearchToolbar(getString(R.string.partner_off_title))

        initRv()

        observeResult()
        partnerVM.getPartnersOff()
    }

    private fun observeResult(){
        partnerVM.partnersOff.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    partners.clear()
                    partners.addAll(state.data.partners)
                    populateData(partners)
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })
    }

    private fun initRv(){
        rvPartnerOff.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(
                context, R.drawable.divider
            )))
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

    private fun populateData(partners: List<Partner>){
        groupAdapter.clear()
        partners.forEach {
            groupAdapter.add(PartnerOffItem(it))
        }
    }
}