package id.android.kmabsensi.presentation.invoice.report

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.InvoiceViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.getYearData
import kotlinx.android.synthetic.main.activity_invoice_report.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class InvoiceReportActivity : BaseActivity() {

    private val sdmVM: KelolaDataSdmViewModel by viewModel()
    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var monthFromSelected = 0
    private var yearFromSelected = 0
    private var monthToSelected = 0
    private var yearToSelected = 0
    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance()
    private var startPeriod = ""
    private var endPeriod = ""
    private var monthFromSelectedLabel = ""
    private var monthToSelectedLabel = ""

    var leaders = mutableListOf<User>()
    var leaderIdSelected = 0
    private var spinnerLeader: Spinner? = null

    private lateinit var dialogFilter: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_report)
        setupToolbar("Invoice Report", isFilterVisible = true)
        initRv()

        btnFilter.setOnClickListener {
            showFilterDialog()
            if (leaders.isEmpty()) sdmVM.getUserManagement(2)
        }

        monthFromSelected = startCalendar.get(Calendar.MONTH)+1
        monthToSelected = startCalendar.get(Calendar.MONTH)+1
        yearFromSelected = startCalendar.get(Calendar.YEAR)
        yearToSelected = endCalendar.get(Calendar.YEAR)

        startPeriod = "$yearFromSelected-$monthFromSelected-01"
        endPeriod = "$yearToSelected-$monthToSelected-01"

        observeData()
        invoiceVM.getInvoiceReport(startPeriod, endPeriod, leaderIdSelected)
    }

    private fun observeData(){
        invoiceVM.invoiceReports.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                showDialog()
            }
            is UiState.Success -> {
                hideDialog()
                monthFromSelectedLabel = "${resources.getStringArray(R.array.month_array).toList()[monthFromSelected]} $yearFromSelected"
                monthToSelectedLabel = "${resources.getStringArray(R.array.month_array).toList()[monthToSelected]} $yearToSelected"

                textLeaderName.text = if (leaderIdSelected == 0) "Semua" else leaders.first { it.id == leaderIdSelected }.full_name
                textPeriod.text = "$monthFromSelectedLabel - $monthToSelectedLabel"

                val data = state.data.invoiceReport
                groupAdapter.clear()
                for(x in 1..3) {
                    when(x){
                        1 -> groupAdapter.add(InvoiceReportItem("Total Invoice Dibuat", data.totalInvoice, data.sumOfInvoice))
                        2 -> groupAdapter.add(InvoiceReportItem("Total Invoice Lunas", data.totalPaidInvoice, data.sumOfPaidInvoice))
                        3 -> groupAdapter.add(InvoiceReportItem("Total Invoice Belum Dibayar", data.totalUnpaidInvoice, data.sumOfUnpaidInvoice))
                    }
                }
            }
            is UiState.Error -> {
                hideDialog()
            }
        } })

        sdmVM.userManagementData.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {

            }
            is UiState.Success -> {
                leaders.addAll(state.data.data)

                val userManagementNames = mutableListOf<String>()
                userManagementNames.add("Semua")
                leaders.forEach { userManagementNames.add(it.full_name) }
                ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item,
                    userManagementNames
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    spinnerLeader?.adapter = adapter

                    spinnerLeader?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                leaderIdSelected = if (position == 0){
                                    0
                                } else {
                                    leaders[position-1].id
                                }
                            }

                        }
                }
            }
            is UiState.Error -> {

            }
        } })
    }

    fun initRv() {
        rvInvoiceReport.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun showFilterDialog() {
        if (!::dialogFilter.isInitialized){
            dialogFilter = MaterialDialog(this)
                .customView(R.layout.dialog_invoice_report_filter_layout, noVerticalPadding = true)
            val customView = dialogFilter.getCustomView()
            val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
            val spinnerBulanDari = customView.findViewById<Spinner>(R.id.spinnerBulanDari)
            val spinnerTahunDari = customView.findViewById<Spinner>(R.id.spinnerTahunDari)
            val spinnerBulanKe = customView.findViewById<Spinner>(R.id.spinnerBulanKe)
            val spinnerTahunKe = customView.findViewById<Spinner>(R.id.spinnerTahunKe)
            spinnerLeader = customView.findViewById(R.id.spinnerLeader)
            val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

            initSpinnerFilter(spinnerBulanDari, spinnerTahunDari, spinnerBulanKe, spinnerTahunKe)

            btnClose.setOnClickListener {
                dialogFilter?.dismiss()
            }

            buttonFilter.setOnClickListener {
                dialogFilter?.dismiss()
                val startMonth = if (monthFromSelected < 10) "0$monthFromSelected" else "$monthFromSelected"
                val endMonth = if (monthToSelected < 10) "0$monthToSelected" else "$monthToSelected"
                startPeriod = "$yearFromSelected-$startMonth-01"
                endPeriod = "$yearToSelected-$endMonth-01"

                invoiceVM.getInvoiceReport(startPeriod, endPeriod, leaderIdSelected)
            }
        }

        dialogFilter.show()
    }

    private fun initSpinnerFilter(
        spinnerBulanDari: Spinner, spinnerTahunDari: Spinner, spinnerBulanKe: Spinner,
        spinnerTahunKe: Spinner
    ) {
        //spinner bulan dari
        ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerBulanDari.adapter = adapter

                spinnerBulanDari.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) monthFromSelected = position
                        }

                    }
            }

        //spinner bulan ke
        ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerBulanKe.adapter = adapter

                spinnerBulanKe.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) monthToSelected = position
                        }

                    }
            }

        //spinner tahun dari
        ArrayAdapter(this, R.layout.spinner_item, getYearData())
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTahunDari.adapter = adapter

                spinnerTahunDari.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) yearFromSelected = spinnerTahunDari.selectedItem.toString().toInt()

                        }

                    }
            }

        //spinner tahun ke
        ArrayAdapter(this, R.layout.spinner_item, getYearData())
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTahunKe.adapter = adapter

                spinnerTahunKe.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) yearToSelected = spinnerTahunKe.selectedItem.toString().toInt()

                        }

                    }
            }
    }
}
