package id.android.kmabsensi.presentation.invoice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseFragment
import id.android.kmabsensi.presentation.invoice.item.ActiveInvoiceItem
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.fragment_invoice_active.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class InvoiceActiveFragment : BaseFragment() {

    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun getLayoutResId() = R.layout.fragment_invoice_active

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        observeData()

        buttonTambahInvoice.setOnClickListener {
            val myItems = listOf("Admin", "Gaji SDM")

            MaterialDialog(requireContext()).show {
                title(text = "Pilih Jenis Invoice")
                listItems(items = myItems) { dialog, index, text ->
                    when(index){
                        0 -> {
                            activity?.startActivity<AddInvoiceActivity>()
                        }
                        1 -> {

                        }
                    }
                }

            }

        }

    }

    private fun initRv(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        rvInvoiceActive.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    private fun observeData(){
        invoiceVM.getMyInvoice(true)
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
                        groupAdapter.add(ActiveInvoiceItem(it){
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
