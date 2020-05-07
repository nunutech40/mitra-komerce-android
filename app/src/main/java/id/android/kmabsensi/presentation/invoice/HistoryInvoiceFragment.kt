package id.android.kmabsensi.presentation.invoice

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseFragment
import id.android.kmabsensi.presentation.invoice.detail.DetailInvoiceActivity
import id.android.kmabsensi.presentation.invoice.item.HistoryInvoiceItem
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.fragment_history_invoice.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryInvoiceFragment : BaseFragment() {

    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun getLayoutResId() = R.layout.fragment_history_invoice

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        observeData()

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
        invoiceVM.getMyInvoice(false)
        invoiceVM.invoices.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is UiState.Loading -> {
                    showLoadingDialog()
                }
                is UiState.Success -> {
                    hideLoadingDialog()
                    val invoices = state.data.invoices
                    if (invoices.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    invoices.forEach {
                        groupAdapter.add(HistoryInvoiceItem(it){
                            activity?.startActivity<DetailInvoiceActivity>()
                        })
                    }
                }
                is UiState.Error -> {
                    hideLoadingDialog()
                }
            }
        })
    }



}
