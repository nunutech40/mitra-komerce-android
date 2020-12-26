package id.android.kmabsensi.presentation.report.performa

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.FilterSdmReportParams
import id.android.kmabsensi.data.remote.response.CsPerformance
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_performa.*
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*



class PerformaActivity : BaseActivity() {

    private val sdmVm: SdmViewModel by viewModel()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var noPartner: String = "0"
    private lateinit var user: User

    private val performa = mutableListOf<CsPerformance>()

    private var isRateConversionToLowest = true
    private var isRateOrderToLowest = true
    private var isOrderToLowest = true
    private var isTransactionToLowest = true
    private var isLeadsToLowest = true

    private var filters =
        listOf("Today", "Yesterday", "Last 7 Days", "This Month", "Last Month", "Custom")
    //for custom filter
    private val calendarDateForm = Calendar.getInstance()
    private val calendarDateTo = Calendar.getInstance()
    private var dateFrom: String = ""
    private var dateTo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performa)
        setupToolbar("Performa")

        noPartner = intent.getStringExtra(NO_PARTNER_KEY) ?: "0"
        user = sdmVm.getUserData()

        dateFrom = getDateString(Calendar.getInstance().time)
        dateTo = getDateString(Calendar.getInstance().time)

        initRv()
        observeData()
        initSpinner()

        btnRatingKonversi.setOnClickListener {
            icSortRatingKonversi.setImageResource(
                if (isRateConversionToLowest) R.drawable.ic_baseline_arrow_upward_24 else R.drawable.ic_baseline_arrow_downward_24
            )
            isRateConversionToLowest = !isRateConversionToLowest
            sortPerforma(SORT_TYPE.RATE_CONVERSION)
        }

        btnRatingOrder.setOnClickListener {
            icSortRatingOrder.setImageResource(
                if (isRateOrderToLowest) R.drawable.ic_baseline_arrow_upward_24 else R.drawable.ic_baseline_arrow_downward_24
            )
            isRateOrderToLowest = !isRateOrderToLowest
            sortPerforma(SORT_TYPE.RATE_ORDER)
        }

        btnTransaski.setOnClickListener {
            icSortTransaksi.setImageResource(
                if (isTransactionToLowest) R.drawable.ic_baseline_arrow_upward_24 else R.drawable.ic_baseline_arrow_downward_24
            )
            isTransactionToLowest = !isTransactionToLowest
            sortPerforma(SORT_TYPE.TRANSACTION)
        }

        btnOrder.setOnClickListener {
            icSortOrder.setImageResource(
                if (isOrderToLowest) R.drawable.ic_baseline_arrow_upward_24 else R.drawable.ic_baseline_arrow_downward_24
            )
            isOrderToLowest = !isOrderToLowest
            sortPerforma(SORT_TYPE.ORDER)
        }

        btnLeads.setOnClickListener {
            icSortLeads.setImageResource(
                if (isLeadsToLowest) R.drawable.ic_baseline_arrow_upward_24 else R.drawable.ic_baseline_arrow_downward_24
            )
            isLeadsToLowest = !isLeadsToLowest
            sortPerforma(SORT_TYPE.LEADS)
        }

        txtCustomDate.setOnClickListener {
            showDialogFilter()
        }

    }

    fun getPerformances(startDate: String, endDate: String) {
        sdmVm.filterCsReportSummary(
            FilterSdmReportParams(
                user_id = 0,
                user_management_id = user.id,
                no_partner = noPartner,
                start_date = startDate,
                end_date = endDate
            )
        )
    }

    private fun initSpinner() {
        //spinner bulan dari
        ArrayAdapter<String>(this, R.layout.spinner_item, filters)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFilter.adapter = adapter

                spinnerFilter.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position < 5) {
                                txtCustomDate.gone()
                            }

                            when (position) {
                                0 -> { // today
                                    getPerformances(
                                        getDateString(DateHelper.getTodayDate()),
                                        getDateString(DateHelper.getTodayDate())
                                    )
                                }
                                1 -> {
                                    getPerformances(
                                        getDateString(DateHelper.getYesterdayDate()),
                                        getDateString(DateHelper.getYesterdayDate())
                                    )
                                }
                                2 -> {
                                    getPerformances(
                                        getDateString(DateHelper.get7DaysAgoDate()),
                                        getDateString(DateHelper.getTodayDate())
                                    )
                                }
                                3 -> {
                                    getPerformances(
                                        getDateString(
                                            DateHelper.getFirstDateOfMonth(
                                                DateHelper.getCurrentMonth(),
                                                DateHelper.getCurrentYear()
                                            )
                                        ),
                                        getDateString(
                                            DateHelper.getLastDateOfMonth(
                                                DateHelper.getCurrentMonth(),
                                                DateHelper.getCurrentYear()
                                            )
                                        )
                                    )
                                }
                                4 -> {
                                    getPerformances(
                                        getDateString(
                                            DateHelper.getFirstDateOfMonth(
                                                DateHelper.getLastMonth(),
                                                DateHelper.getCurrentYear()
                                            )
                                        ),
                                        getDateString(
                                            DateHelper.getLastDateOfMonth(
                                                DateHelper.getLastMonth(),
                                                DateHelper.getCurrentYear()
                                            )
                                        )
                                    )
                                }
                                5 -> showDialogFilter()
                            }
                        }
                    }
            }
    }

    private fun observeData() {
        sdmVm.csReportSummary.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    groupAdapter.clear()
                    performa.clear()
                    performa.addAll(state.data.data)
                    if (performa.isEmpty()) txtEmpty.visible() else txtEmpty.gone()
                    state.data.data.forEach {
                        groupAdapter.add(PerformaItem(it))
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    private fun sortPerforma(type: SORT_TYPE) {
        groupAdapter.clear()
        val performaSorted: List<CsPerformance>
        when(type){
            SORT_TYPE.RATE_CONVERSION -> {
                performaSorted =
                    if (isRateConversionToLowest) performa.sortedBy { it.conversionRate } else performa.sortedByDescending { it.conversionRate }
            }
            SORT_TYPE.RATE_ORDER -> {
                performaSorted =
                    if (isRateOrderToLowest) performa.sortedBy { it.orderRate } else performa.sortedByDescending { it.orderRate }
            }
            SORT_TYPE.ORDER -> {
                performaSorted =
                    if (isOrderToLowest) performa.sortedBy { it.totalOrder } else performa.sortedByDescending { it.totalOrder }
            }
            SORT_TYPE.TRANSACTION -> {
                performaSorted =
                    if (isTransactionToLowest) performa.sortedBy { it.totalTransaction } else performa.sortedByDescending { it.totalTransaction }
            }
            SORT_TYPE.LEADS -> {
                performaSorted =
                    if (isLeadsToLowest) performa.sortedBy { it.totalLeads } else performa.sortedByDescending { it.totalLeads }
            }
        }

        performaSorted.forEach {
            groupAdapter.add(PerformaItem(it))
        }
    }

    private fun initRv() {
        rvPerforma.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun showDialogFilter() {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_custom_filter_date, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val edtStartDate = customView.findViewById<AppCompatEditText>(R.id.edtStartDate)
        val edtEndDate = customView.findViewById<AppCompatEditText>(R.id.edtEndDate)
        val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        edtStartDate.setText(getDateStringFormatted(calendarDateForm.time))
        edtEndDate.setText(getDateStringFormatted(calendarDateTo.time))

        edtStartDate.setOnClickListener { view ->
            showDatePicker(true) {
                dateFrom = getDateString(it)
                edtStartDate.setText(getDateStringFormatted(it))
            }
        }

        edtEndDate.setOnClickListener { view ->
            showDatePicker(false) {
                dateTo = getDateString(it)
                edtEndDate.setText(getDateStringFormatted(it))
            }
        }

        buttonFilter.setOnClickListener {
            dialog.dismiss()
            txtCustomDate.text = "${getDateStringFormatted(calendarDateForm.time)} - ${getDateStringFormatted(calendarDateTo.time)}"
            txtCustomDate.visible()
            getPerformances(dateFrom, dateTo)
        }
        dialog.show()
    }


    private fun showDatePicker(isDateFrom: Boolean, callback: (Date) -> Unit) {

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                if (isDateFrom) {
                    calendarDateForm.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    callback(calendarDateForm.time)
                } else {
                    calendarDateTo.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    callback(calendarDateTo.time)
                }
            },
            (if (isDateFrom) calendarDateForm else calendarDateTo).get(Calendar.YEAR),
            (if (isDateFrom) calendarDateForm else calendarDateTo).get(Calendar.MONTH),
            (if (isDateFrom) calendarDateForm else calendarDateTo).get(Calendar.DAY_OF_MONTH)
        )
        if (!isDateFrom) datePickerDialog.datePicker.minDate = calendarDateForm.timeInMillis
        datePickerDialog.show()
    }
}