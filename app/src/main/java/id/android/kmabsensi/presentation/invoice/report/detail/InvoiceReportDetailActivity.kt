package id.android.kmabsensi.presentation.invoice.report.detail

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.InvoiceViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_invoice_report_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvoiceReportDetailActivity : BaseActivity() {

    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var startPeriod = ""
    private var endPeriod = ""
    private var invoiceType = 0
    private var invoiceStatus = 0
    private var leaderIdSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_report_detail)
        setupToolbar("Invoice Detail Report")
        initRv()
        getBundle()
        observeData()

        invoiceVM.getInvoiceReportDetail(
            startPeriod,
            endPeriod,
            leaderIdSelected,
            invoiceType,
            invoiceStatus
        )
    }

    private fun getBundle() {
        startPeriod = intent.getStringExtra(START_PERIOD)
        endPeriod = intent.getStringExtra(END_PERIOD)
        invoiceType = intent.getIntExtra(INVOICE_TYPE, 0)
        invoiceStatus = intent.getIntExtra(INVOICE_STATUS, 0)
        leaderIdSelected = intent.getIntExtra(LEADER_ID, 0)
    }

    private fun observeData() {
        invoiceVM.invoiceReportDetail.observe(this, Observer { state ->
            when(state) {
                is UiState.Loading ->  {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    groupAdapter.clear()
                    state.data.data?.forEach {
                        groupAdapter.add(InvoiceReportDetailitem(it))
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    fun initRv(){
        rvInvoiceReportDetail.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(
                context, R.drawable.divider
            )))
        }


    }


}