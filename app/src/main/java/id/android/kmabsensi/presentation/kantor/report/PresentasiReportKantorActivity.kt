package id.android.kmabsensi.presentation.kantor.report

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber.e
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.OfficeResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.report.filter.FilterReportKantorActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_filter_report_kantor.*
import kotlinx.android.synthetic.main.activity_presentasi_report_kantor.*
import kotlinx.android.synthetic.main.activity_presentasi_report_kantor.toolbar
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class PresentasiReportKantorActivity : BaseActivity() {

    private val groupAdapter: GroupAdapter<GroupieViewHolder> by inject()

    private val vm: PresenceReportViewModel by inject()

    private lateinit var myDialog: MyDialog

    private var dateFrom = ""
    private var dateTo = ""
    private var officeIdSelected = 0

    private val REQUEST_FILTER = 120

    private var categoryReport: Int = 0 // 0 -> office, 1 -> manajemen, 2 -> sdm

    private var user: User? = null
    private var isManagement = false

    private var userResponse: UserResponse? = null
    private var officeResponse: OfficeResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentasi_report_kantor)

        myDialog = MyDialog(this)

        btnFilter.visible()

        categoryReport = intent.getIntExtra(CATEGORY_REPORT_KEY, 0)
        user = intent.getParcelableExtra(USER_KEY)
        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)

        initRv()

        dateFrom = getTodayDate()
        dateTo = getTodayDate()
        setDateText(getDateStringFormatted(Calendar.getInstance().time))

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnFilter.setOnClickListener {
            startActivityForResult<FilterReportKantorActivity>(
                REQUEST_FILTER, CATEGORY_REPORT_KEY to categoryReport,
                "user_response" to userResponse,
                "office_response" to officeResponse,
                "dateFrom" to dateFrom,
                "dateTo" to dateTo
            )

        }

        vm.presenceReportData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    if (isManagement) myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    val percentage = it.data.data.report.percentage.substring(
                        0,
                        it.data.data.report.percentage.length - 1
                    ).toDouble().toInt()
                    circularProgressBar.progress = percentage.toFloat()
                    txtPercentage.text = percentage.toString() + "%"
//                    txtAngkaKehadiran.text = "${it.data.data.report.total_present}/${it.data.data.report.total_user}"
                    textTotalHadir.text = it.data.data.report.total_present.toString()
                    textTotalTerlamat.text = it.data.data.report.total_come_late.toString()
                    textTotalGagalAbsen.text = it.data.data.report.total_report_presence_failure.toString()
                    textTotalTidakAbsenPulang.text = it.data.data.report.total_not_checkout.toString()
                    if (it.data.data.presence.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    it.data.data.presence.forEach {
                        groupAdapter.add(AbsensiReportItem(it))
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e(it.throwable)
                }
            }
        })

        vm.userManagementData.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    if (!isManagement) {
                        txtSubReport.text = it.data.data[0].full_name
                        vm.getPresenceReportFiltered(userManagementId = it.data.data[0].id, startDate = dateFrom, endDate = dateTo)
                    }
                    userResponse = it.data
                }
                is UiState.Error -> {
                    e(it.throwable)
                }
            }
        })

        vm.officeData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    txtSubReport.text = it.data.data[0].office_name
                    vm.getPresenceReportFiltered(officeId = it.data.data[0].id, startDate = dateFrom, endDate = dateTo)
                    officeResponse = it.data
                }
                is UiState.Error -> {

                }
            }
        })

        when (categoryReport) {
            0 -> {
                txtReport.text = "Kantor Cabang"
                setupToolbarTitle("Presentasi Report Kantor")
                vm.getDataOffice()
            }
            1 -> {
                txtReport.text = "Kantor Cabang"
                txtSubReport.text = "Semua Kantor"
                vm.getPresenceReportFiltered(roleId = 2, startDate = dateFrom, endDate = dateTo)
                setupToolbarTitle("Presentasi Report Manajemen")
            }
            2 -> {
                txtReport.text = "Manajemen"
                if (isManagement) {
                    user?.let {
                        txtSubReport.text = it.full_name
                        vm.getPresenceReportFiltered(userManagementId = it.id, startDate = dateFrom, endDate = dateTo)
                        vm.getUserManagement()
                    }
                } else {
                    //get data user management
                    vm.getUserManagement()
                }
                setupToolbarTitle("Presentasi Report SDM")

            }
        }

    }

    private fun setupToolbarTitle(title: String){
        txtTitle.text = title
    }

    private fun setDateText(date: String) {
        txtDate.text = date
    }


    private fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvAbsensi.apply {
            isNestedScrollingEnabled = false
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_FILTER && resultCode == Activity.RESULT_OK) {
            data?.let {

                dateFrom = it.getStringExtra(DATE_FILTER_KEY).toString()
                dateTo = it.getStringExtra(END_DATE_FILTER_KEY).toString()

                val dateFormat = SimpleDateFormat(DATE_FORMAT)
                val date = dateFormat.parse(dateFrom)
                txtDate.text = getDateStringFormatted(date)

                when (categoryReport) {
                    0 -> {
                        officeIdSelected = it.getIntExtra(OFFICE_ID_FILTER, 0)
                        val officaName = it.getStringExtra(OFFICE_NAME_FILTER)
                        txtSubReport.text = officaName
                        groupAdapter.clear()
                        vm.getPresenceReportFiltered(officeId = officeIdSelected, startDate = dateFrom, endDate = dateTo)
                    }
                    1 -> {
                        groupAdapter.clear()
                        vm.getPresenceReportFiltered(roleId = 2, startDate = dateFrom, endDate = dateTo)
                    }
                    2 -> {
                        val userManagementIdSelected = it.getIntExtra(USER_ID_KEY, 0)
                        val userManagementName = it.getStringExtra(USER_MANAGEMENT_NAME_KEY)
                        txtSubReport.text = userManagementName
                        groupAdapter.clear()
                        vm.getPresenceReportFiltered(
                            userManagementId = userManagementIdSelected,
                            startDate = dateFrom, endDate = dateTo
                        )
                    }
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
