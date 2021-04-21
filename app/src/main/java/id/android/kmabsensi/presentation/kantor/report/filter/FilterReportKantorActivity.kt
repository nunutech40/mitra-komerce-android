package id.android.kmabsensi.presentation.kantor.report.filter

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
import org.jetbrains.anko.toast
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

    private val endDate = Calendar.getInstance()
    private val endyear = endDate.get(Calendar.YEAR)
    private val endmonth = endDate.get(Calendar.MONTH)
    private val endday = endDate.get(Calendar.DAY_OF_MONTH)

    private var startDate = Calendar.getInstance()
    private val startyear = startDate.get(Calendar.YEAR)
    private val startmonth = startDate.get(Calendar.MONTH)
    private val startday = startDate.get(Calendar.DAY_OF_MONTH)
    private var saveyear = 0
    private var savemonth = 0
    private var saveday = 0

    val offices = mutableListOf<Office>()
    val officeNames = mutableListOf<String>()
    var officeIdSelected = 0
    var officeNameSelected = ""

    private var categoryReport: Int = 0 // 0 -> office, 1 -> manajemen, 2 -> sdm

    val userManagement = mutableListOf<User>()
    val userManagementNames = mutableListOf<String>()
    var userManagementIdSelected = 0
    var userManagementNameSelected = ""

    var userResponse: UserResponse? = null
    var officeResponse: OfficeResponse? = null
    private var isFormStart = false
    private var isFormEnd = false

    private val binding by lazy { ActivityFilterReportKantorBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar("Filter Report")

        categoryReport = intent.getIntExtra(CATEGORY_REPORT_KEY, 0)
        userResponse = intent.getParcelableExtra("user_response")
        officeResponse = intent.getParcelableExtra("office_response")
        dateFrom = intent.getStringExtra("dateFrom") ?: ""
        dateTo = intent.getStringExtra("dateTo") ?: ""


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
                val month = if (monthOfYear.toString().count()==1) "0${monthOfYear+1}" else "${monthOfYear+1}"
                dateFromSelectedString = "$year-$month-$dayOfMonth"
                setDateToView(dateFromSelectedString)
                saveday = dayOfMonth
                savemonth = monthOfYear
                saveyear = year
                isFormStart = true
                isFormEnd = false
            }, startyear, startmonth, startday)
            startdatePick.setTitle(getString(R.string.pilih_tanggal_awal))
            startdatePick.datePicker.maxDate = startDate.timeInMillis
            startdatePick.show()
        }

        binding.edtEndDate.setOnClickListener {
            if (!isFormStart){
                toast("Anda belum memilih tanggal awal.")
            }else{
                endDate.set(saveyear, savemonth, saveday)
                val endDatePick = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
                    val month = if (monthOfYear.toString().count()==1) "0${monthOfYear+1}" else "${monthOfYear+1}"
                    dateToSelectedString = "$year-$month-$dayOfMonth"
                    setEndDateToView(dateToSelectedString)
                    isFormEnd = true
                }, endyear, endmonth, endday)
                endDatePick.setTitle(getString(R.string.pilih_tanggal_akhir))
                endDatePick.datePicker.minDate = endDate.timeInMillis
                endDate.add(Calendar.DATE, +7)
                endDatePick.datePicker.maxDate = endDate.timeInMillis
                endDatePick.show()
            }
        }

        binding.btnAktifkan.setOnClickListener {
            if (!isFormStart){
                toast("Anda belum memilih tanggal awal.")
            }else if (!isFormEnd){
                toast("Anda belum memilih tanggal akhir.")
            }else{
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
}
