package id.android.kmabsensi.presentation.kantor.report.filter

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.data.remote.response.OfficeResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.databinding.ActivityFilterReportKantorBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_filter_report_kantor.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class FilterReportKantorActivity : BaseActivity() {

    private val vm: FilterReportViewModel by inject()

    private var dateFromSelectedString = ""
    private var dateToSelectedString = ""
    private var dateFrom = ""
    private var dateTo = ""
    private var dateFromCalendarSelected = Calendar.getInstance()
    private var dateToCalendarSelected = Calendar.getInstance()

    private var today = Calendar.getInstance()
    private val thisyear = today.get(Calendar.YEAR)
    private val thismonth = today.get(Calendar.MONTH)
    private val thisday = today.get(Calendar.DAY_OF_MONTH)
    private var startDate = Calendar.getInstance()
    private var startyear = 0
    private var startmonth = 0
    private var startday = 0
    private val endDate = Calendar.getInstance()
    private var endyear = 0
    private var endmonth = 0
    private var endday = 0

    private val offices = mutableListOf<Office>()
    private val officeNames = mutableListOf<String>()
    private var officeIdSelected = 0
    private var officeNameSelected = ""

    private var categoryReport: Int = 0 // 0 -> office, 1 -> manajemen, 2 -> sdm

    private val userManagement = mutableListOf<User>()
    private val userManagementNames = mutableListOf<String>()
    private var userManagementIdSelected = 0
    private var userManagementNameSelected = ""

    private var userResponse: UserResponse? = null
    private var officeResponse: OfficeResponse? = null
    private var isToday = true
//    private var isFormEnd = false

    private val binding by lazy { ActivityFilterReportKantorBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(getString(R.string.filter_report))

        categoryReport = intent.getIntExtra(CATEGORY_REPORT_KEY, 0)
        userResponse = intent.getParcelableExtra("user_response")
        officeResponse = intent.getParcelableExtra("office_response")
        dateFrom = intent.getStringExtra("dateFrom") ?: ""
        dateTo = intent.getStringExtra("dateTo") ?: ""

        getDateStarted(dateFrom, dateTo)

        val dateFormat = SimpleDateFormat(DATE_FORMAT)
        val dateFromParsed: Date = dateFormat.parse(dateFrom)
        val dateToParsed: Date = dateFormat.parse(dateTo)
        dateFromCalendarSelected.time = dateFromParsed
        dateToCalendarSelected.time = dateToParsed

        binding.edtStartDate.setText(dateFrom)
        binding.edtEndDate.setText(dateTo)
        setupListener()
        setupObserver()

        when (categoryReport) {
            0 -> {
                binding.layoutPj.gone()
                officeResponse?.let {
                    setSpinnerOffice(it.data)
                }
            }
            1 -> {
                binding.layoutPj.gone()
                binding.layoutKantorCabang.gone()
            }
            2 -> {
                binding.layoutKantorCabang.gone()
                userResponse?.let {
                    setSpinnerManajemen(it.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })
                } ?: kotlin.run {
                    vm.getUserManagement(2)
                }

            }
        }
    }

    private fun getDateStarted(dtFrom: String, dtTo: String) {
        startyear = dtFrom.split("-")[0].toInt()
        startmonth = dtFrom.split("-")[1].toInt()-1
        startday = dtFrom.split("-")[2].toInt()
        checkToday(startyear, startmonth, startday)

        endyear = dtTo.split("-")[0].toInt()
        endmonth = dtTo.split("-")[1].toInt()-1
        endday = dtTo.split("-")[2].toInt()
        endDate.set(endyear, endmonth, endday)
    }

    private fun setupObserver() {
        vm.userManagementData.observe(this, {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    setSpinnerManajemen(it.data.data)
                }
                is UiState.Error -> {
                    e(it.throwable)
                }
            }
        })
    }

    private fun setupListener() {
        binding.edtStartDate.setOnClickListener {
            val startdatePick = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
                val month = if (monthOfYear.toString()
                        .count() == 1
                ) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
                dateFromSelectedString = "$year-$month-$dayOfMonth"
                setDateToView(dateFromSelectedString)
                updateStartDate(year, monthOfYear, dayOfMonth)

                endDate.set(year, monthOfYear, dayOfMonth)
                endDate.add(Calendar.DATE, +7)
                checkToday(year, monthOfYear, dayOfMonth)
                val getEndDate = if (endDate.timeInMillis > today.timeInMillis) today else endDate

                setEndDateToView("${getEndDate.get(Calendar.YEAR)}-$month-${getEndDate.get(Calendar.DAY_OF_MONTH)}")
            }, startyear, startmonth, startday)
            startdatePick.setTitle(getString(R.string.pilih_tanggal_awal))
            startdatePick.datePicker.maxDate = today.timeInMillis
            startdatePick.show()
        }

        binding.edtEndDate.setOnClickListener {
            val endDatePick = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
                val month = if (monthOfYear.toString().count()==1) "0${monthOfYear+1}" else "${monthOfYear+1}"
                dateToSelectedString = "$year-$month-$dayOfMonth"
                setEndDateToView(dateToSelectedString)
                updateEndDate(year, monthOfYear, dayOfMonth)
            }, endyear, endmonth, endday)
            endDatePick.setTitle(getString(R.string.pilih_tanggal_akhir))
            endDate.add(Calendar.DATE, -7)
            endDatePick.datePicker.minDate = if (isToday) startDate.timeInMillis else endDate.timeInMillis
            endDate.add(Calendar.DATE, +7)
            val maxdate = if (endDate.timeInMillis > today.timeInMillis) today.timeInMillis else endDate.timeInMillis
            endDatePick.datePicker.maxDate = maxdate
            endDatePick.show()
        }

        binding.btnAktifkan.setOnClickListener {
            val intent = Intent()
            intent.putExtra(DATE_FILTER_KEY, binding.edtStartDate.text.toString())
            intent.putExtra(END_DATE_FILTER_KEY, binding.edtEndDate.text.toString())
            when (categoryReport) {
                0 -> {
                    intent.putExtra(OFFICE_ID_FILTER, officeIdSelected)
                    intent.putExtra(OFFICE_NAME_FILTER, officeNameSelected)
                }
                2 -> {
                    intent.putExtra(USER_ID_KEY, userManagementIdSelected)
                    intent.putExtra(USER_MANAGEMENT_NAME_KEY, userManagementNameSelected)
                }
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setSpinnerOffice(data: List<Office>) {
        val sortedList = data.sortedWith(compareBy { it.office_name }).reversed()
        offices.addAll(sortedList)

        sortedList.forEach {
            officeNames.add(it.office_name)
        }

        ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            officeNames
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerKantorCabang.adapter = adapter

            spinnerKantorCabang.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        officeIdSelected = offices[position].id
                        officeNameSelected = offices[position].office_name
                    }
                }
        }
    }

    fun setSpinnerManajemen(manajemen: List<User>) {
        val sortedList = manajemen.sortedWith(compareBy { it.full_name })

        userManagement.addAll(sortedList)

        sortedList.forEach {
            userManagementNames.add(it.full_name)
        }

        ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            userManagementNames
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerPenanggungJawab.adapter = adapter

            spinnerPenanggungJawab.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        userManagementIdSelected = userManagement[position].id
                        userManagementNameSelected = userManagement[position].full_name
                    }
                }
        }
    }

    private fun setDateToView(dateSelected: String) {
        binding.edtStartDate.setText(dateSelected)
    }

    private fun setEndDateToView(dateSelected: String) {
        binding.edtEndDate.setText(dateSelected)
    }

    private fun checkToday(year: Int, month: Int, day: Int) {
        isToday = year == thisyear && month == thismonth && day == thisday
    }

    private fun updateStartDate(year: Int, month: Int, day: Int){
        startyear = year
        startmonth = month
        startday = day
    }

    private fun updateEndDate(year: Int, month: Int, day: Int){
        endyear = year
        endmonth = month
        endday = day
    }
}
