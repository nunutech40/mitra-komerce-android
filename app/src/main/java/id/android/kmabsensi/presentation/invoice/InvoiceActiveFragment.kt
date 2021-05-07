package id.android.kmabsensi.presentation.invoice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseFragment
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.invoice.create.AddInvoiceActivity
import id.android.kmabsensi.presentation.invoice.detail.DetailInvoiceActivity
import id.android.kmabsensi.presentation.invoice.item.ActiveInvoiceItem
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.fragment_invoice_active.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class InvoiceActiveFragment : BaseFragment() {

    private val vm: HomeViewModel by inject()
    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val RC_ADD_INVOICE = 113
    private val RC_DETAIL_INVOICE = 115

    var role: String = ""

    lateinit var user: User

    override fun getLayoutResId() = R.layout.fragment_invoice_active

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        buttonTambahInvoice.setOnClickListener {
            val myItems = ArrayList<String>()
            when(role){
                ROLE_ADMIN ->{
                    myItems.add(getString(R.string.text_admin))
                    myItems.add(getString(R.string.text_gaji_sdm))
                }
                ROLE_MANAGEMEMENT ->{
                    if (user.position_name.toLowerCase().contains(getString(R.string.category_leader))){
                        myItems.add(getString(R.string.text_gaji_sdm))
                    }else if (user.position_name.toLowerCase().contains(getString(R.string.category_accountant))){
                        myItems.add(getString(R.string.text_admin))
                    }
                }
            }
            MaterialDialog(requireContext()).show {
                title(text = "Pilih Jenis Invoice")
                listItems(items = myItems) { dialog, index, text ->
                    when (text) {
                        getString(R.string.text_admin) -> {
                            val intent = Intent(requireContext(), AddInvoiceActivity::class.java)
                            intent.putExtra(IS_INVOICE_ADMIN_KEY, true)
                            startActivityForResult(intent, RC_ADD_INVOICE)
                        }
                        getString(R.string.text_gaji_sdm) -> {
                            val intent = Intent(requireContext(), AddInvoiceActivity::class.java)
                            intent.putExtra(IS_INVOICE_ADMIN_KEY, false)
                            startActivityForResult(intent, RC_ADD_INVOICE)
                        }
                    }
                }

            }
        }

        observeData()
        invoiceVM.getMyInvoice(true)

        user = vm.getUserData()
        Log.d("asda", user.toString())
        role = getRoleName(user.role_id)

        if (user.position_name.toLowerCase().contains(getString(R.string.category_leader)) || user.position_name.toLowerCase().contains(getString(R.string.category_accountant))){
            buttonTambahInvoice.visible()
        } else{
            buttonTambahInvoice.gone()
        }

        swipeRefresh.setOnRefreshListener {
            invoiceVM.getMyInvoice(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_ADD_INVOICE && resultCode == Activity.RESULT_OK) {
            invoiceVM.getMyInvoice(true)
            val message = data?.getStringExtra("message")
            createAlertSuccess(requireActivity(), message.toString())
        }

        if (requestCode == RC_DETAIL_INVOICE && resultCode == Activity.RESULT_OK){
            invoiceVM.getMyInvoice(true)
        }
    }

    private fun initRv() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        rvInvoiceActive.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    private fun observeData() {
        invoiceVM.invoices.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    groupAdapter.clear()
                    val invoices = state.data.invoices
                    if (invoices.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    invoices.forEach {
                        groupAdapter.add(ActiveInvoiceItem(it) {
                            val intent = Intent(requireContext(), DetailInvoiceActivity::class.java)
                            intent.putExtra(INVOICE_ID_KEY, it.id)
                            intent.putExtra(INVOICE_TYPE_KEY, it.invoiceType)
                            startActivityForResult(intent, RC_DETAIL_INVOICE)
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
        invoiceVM.filterMyInvoice(true, invoiceType, userToId, leaderIdSelected)
    }

}
