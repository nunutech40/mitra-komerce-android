package id.android.kmabsensi.presentation.invoice

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseFragment
import id.android.kmabsensi.presentation.invoice.detail.DetailInvoiceActivity
import id.android.kmabsensi.presentation.invoice.item.HistoryInvoiceItem
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.fragment_history_invoice.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryInvoiceFragment : BaseFragment() {

    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun getLayoutResId() = R.layout.fragment_history_invoice

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()
        observeData()
        invoiceVM.getMyInvoice(false)

        swipeRefresh.setOnRefreshListener {
            invoiceVM.getMyInvoice(false)
        }
    }

    private fun initRv(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        rvHistoryInvoice.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    private fun observeData(){
        invoiceVM.invoices.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    swipeRefresh.isRefreshing = false
                    val invoices = state.data.invoices
                    if (invoices.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    invoices.forEach {
                        groupAdapter.add(HistoryInvoiceItem(it){
                            activity?.startActivity<DetailInvoiceActivity>(
                                INVOICE_ID_KEY to it.id,
                                INVOICE_TYPE_KEY to it.invoiceType)
                        })
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })
    }

    fun filterInvoice(invoiceType: Int, userToId: Int, leaderIdSelected: Int){
        invoiceVM.filterMyInvoice(false, invoiceType, userToId, leaderIdSelected)
    }



}
