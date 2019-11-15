package id.android.kmabsensi.presentation.kantor.report.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.data.remote.response.OfficeResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_filter_report_kantor.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class FilterReportKantorActivity : BaseActivity() {

    private val vm: FilterReportViewModel by inject()

    private var dateSelectedString = ""
    private var dateSelected = ""
    private var dateCalendarSelected = Calendar.getInstance()

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_report_kantor)

//        setSupportActionBar(toolbar)
//        supportActionBar?.title = "Filter Report"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setToolbarTitle("Filter Report")

        categoryReport = intent.getIntExtra(CATEGORY_REPORT_KEY, 0)
        userResponse = intent.getParcelableExtra("user_response")
        officeResponse = intent.getParcelableExtra("office_response")
        dateSelected = intent.getStringExtra("date")


        val dateFormat = SimpleDateFormat(DATE_FORMAT)
        val date: Date = dateFormat.parse(dateSelected)
        dateCalendarSelected.time = date

        edtDate.setText(dateSelected)

        edtDate.setOnClickListener {
            MaterialDialog(this).show {
                datePicker(currentDate = dateCalendarSelected) { dialog, date ->
                    dialog.dismiss()
//                    dateSelected = date
                    dateSelectedString = getDateString(date.time)
                    setDateToView(dateSelectedString)
                }
            }
        }

        btnAktifkan.setOnClickListener {
            val intent = Intent()
            intent.putExtra(DATE_FILTER_KEY, edtDate.text.toString())
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
//            toast("Filter diaktifkan")
            finish()
        }

        vm.userManagementData.observe(this, androidx.lifecycle.Observer {
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

        when (categoryReport) {
            0 -> {
                layoutPj.gone()
                officeResponse?.let {
                    setSpinnerOffice(it.data)
                }
            }
            1 -> {
                layoutPj.gone()
                layoutKantorCabang.gone()
            }
            2 -> {
                layoutKantorCabang.gone()
                userResponse?.let {
                    val userManajemenJabatanLeader =
                        it.data.filter { it.position_name.toLowerCase().contains("leader") }
                    d { userManajemenJabatanLeader.toString() }
                    setSpinnerManajemen(it.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })
                } ?: kotlin.run {
                    vm.getUserManagement(2)
                }

            }
        }
    }

    fun setSpinnerOffice(data: List<Office>) {
        offices.addAll(data)

        data.forEach {
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
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

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
        userManagement.addAll(manajemen)

        manajemen.forEach {
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
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

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
        edtDate.setText(dateSelected)
    }
}
