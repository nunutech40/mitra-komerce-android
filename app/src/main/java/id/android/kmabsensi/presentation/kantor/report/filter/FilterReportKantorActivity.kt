package id.android.kmabsensi.presentation.kantor.report.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_filter_report_kantor.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.util.*

class FilterReportKantorActivity : BaseActivity() {

    private val vm: FilterReportViewModel by inject()

    private var dateSelectedString  = ""
    private var dateSelected = Calendar.getInstance()

    val offices = mutableListOf<Office>()
    val officeNames = mutableListOf<String>("Semua Kantor")
    var officeIdSelected = 0
    var officeNameSelected = ""

    private var categoryReport: Int = 0 // 0 -> office, 1 -> manajemen, 2 -> sdm

    val userManagement = mutableListOf<User>()
    val userManagementNames = mutableListOf<String>("Semua Manajemen")
    var userManagementIdSelected = 0
    var userManagementNameSelected = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_report_kantor)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Filter Report"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryReport = intent.getIntExtra(CATEGORY_REPORT_KEY, 0)

        edtDate.setText(getTodayDate())

        edtDate.setOnClickListener {
            MaterialDialog(this).show {
                datePicker(currentDate = dateSelected) { dialog, date ->
                    dialog.dismiss()
                    dateSelected = date
                    dateSelectedString = getDateString(date.time)
                    setDateToView(dateSelectedString)
                }
            }
        }

        btnAktifkan.setOnClickListener {
            val intent = Intent()
            intent.putExtra(DATE_FILTER_KEY, edtDate.text.toString())
            when(categoryReport){
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
            toast("Filter diaktifkan")
            finish()
        }


        vm.officeData.observe(this, androidx.lifecycle.Observer {
            when(it){
                is UiState.Loading -> {

                }
                is UiState.Success -> {

                    offices.addAll(it.data.data)

                    it.data.data.forEach {
                        officeNames.add(it.office_name)
                    }

                    ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, officeNames).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerKantorCabang.adapter = adapter

                        spinnerKantorCabang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position == 0){
                                    officeIdSelected = 0
                                    officeNameSelected = "Semua Kantor"
                                } else {
                                    officeIdSelected = offices[position-1].id
                                    officeNameSelected = offices[position-1].office_name
                                }
                            }

                        }
                    }
                }
                is UiState.Error -> {
                    e(it.throwable)
                }
            }
        })

        vm.userManagementData.observe(this, androidx.lifecycle.Observer {
            when(it){
                is UiState.Loading -> {

                }
                is UiState.Success -> {

                    userManagement.addAll(it.data.data)

                    it.data.data.forEach {
                        userManagementNames.add(it.full_name)
                    }

                    ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userManagementNames).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerPenanggungJawab.adapter = adapter

                        spinnerPenanggungJawab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position == 0){
                                    userManagementIdSelected = 0
                                    userManagementNameSelected = "Semua PJ"
                                } else {
                                    userManagementIdSelected = userManagement[position-1].id
                                    userManagementNameSelected = userManagement[position-1].full_name
                                }
                            }

                        }
                    }
                }
                is UiState.Error -> {
                    e(it.throwable)
                }
            }
        })

        when(categoryReport){
            0 -> {
                layoutPj.gone()
                vm.getDataOffice()
            }
            1 -> {
                layoutPj.gone()
                layoutKantorCabang.gone()
            }
            2 -> {
                layoutKantorCabang.gone()
                vm.getUserManagement(2)
            }
        }




    }

    private fun setDateToView(dateSelected: String) {
        edtDate.setText(dateSelected)
    }
}
