package id.android.kmabsensi.presentation.sdm.laporan

import android.app.Activity
import android.content.Intent
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
import id.android.kmabsensi.data.remote.body.FilterSdmReportParams
import id.android.kmabsensi.data.remote.response.CsPerformance
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_sdm_laporan.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SdmLaporanActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var RC_CRUD = 120

    private lateinit var dialogFilter: MaterialDialog
    private var monthFromSelected = 0
    private var yearFromSelected = 0
    private var monthToSelected = 0
    private var yearToSelected = 0
    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance()
    private var startPeriod = ""
    private var endPeriod = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdm_laporan)
        setupToolbar(getString(R.string.laporan_title), isFilterVisible = true)

        initRv()
        observeResult()
        sdmVM.getSdmReports()

        fabAdd.setOnClickListener {
            startActivityForResult<AddSdmLaporanActivity>(RC_CRUD)
        }

        btnFilter.setOnClickListener {
            showFilter()
        }

        swipeRefresh.setOnRefreshListener {
            sdmVM.getSdmReports()
        }

    }

    private fun observeResult(){
        sdmVM.csPerformances.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    groupAdapter.clear()
                    val data = state.data.data
                    if (data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    data.forEach {
                        groupAdapter.add(SdmReportItem(it, object : OnSdmReportListener {
                            override fun onDeleteClicked(report: CsPerformance) {
                                showDialogConfirmDelete(
                                    this@SdmLaporanActivity,
                                    "Hapus Data Laporan"
                                ) {
                                    sdmVM.deleteSdmReport(report.id)
                                }
                            }

                            override fun onEditClicked(report: CsPerformance) {
                                startActivityForResult<AddSdmLaporanActivity>(
                                    RC_CRUD,
                                    "report" to report
                                )
                            }

                        }))
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })

        sdmVM.crudResponse.observe(this, Observer { state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status){
                        createAlertSuccess(this, state.data.message)
                        sdmVM.getSdmReports()
                    } else {
                        createAlertError(this, "Gagal", state.data.message)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    private fun initRv(){
        rvLaporan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CRUD){
            if (resultCode == Activity.RESULT_OK){
                val message = data?.getStringExtra("message")
                createAlertSuccess(this, message.toString())
                groupAdapter.clear()
                sdmVM.getSdmReports()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showFilter(){
        if (!::dialogFilter.isInitialized){
            dialogFilter = MaterialDialog(this)
                .customView(R.layout.dialog_filter_sdm_report, noVerticalPadding = true)
            val customView = dialogFilter.getCustomView()
            val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
            val spinnerBulanDari = customView.findViewById<Spinner>(R.id.spinnerBulanDari)
            val spinnerTahunDari = customView.findViewById<Spinner>(R.id.spinnerTahunDari)
            val spinnerBulanKe = customView.findViewById<Spinner>(R.id.spinnerBulanKe)
            val spinnerTahunKe = customView.findViewById<Spinner>(R.id.spinnerTahunKe)
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
                endPeriod = "$yearToSelected-$endMonth-31"

                sdmVM.filterSdmReports(
                    FilterSdmReportParams(
                        sdmVM.getUserData().id,
                        0,
                        0,
                        startPeriod,
                        endPeriod
                    )
                )
            }
        }

        dialogFilter.show()
    }

    private fun initSpinnerFilter(spinnerBulanDari: Spinner, spinnerTahunDari: Spinner, spinnerBulanKe: Spinner,
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
                spinnerBulanDari.setSelection(monthFromSelected)
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

                spinnerBulanKe.setSelection(monthToSelected)
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

                spinnerTahunDari.setSelection(getYearData().indexOfFirst { it == yearFromSelected.toString() })
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

                spinnerTahunKe.setSelection(getYearData().indexOfFirst { it == yearToSelected.toString() })
            }
    }
}