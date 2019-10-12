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
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.report.filter.FilterReportKantorActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_filter_report_kantor.*
import kotlinx.android.synthetic.main.activity_presentasi_report_kantor.*
import kotlinx.android.synthetic.main.activity_presentasi_report_kantor.toolbar
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class PresentasiReportKantorActivity : BaseActivity() {

    private val groupAdapter: GroupAdapter<ViewHolder> by inject()

    private val vm: PresenceReportViewModel by inject()

    private lateinit var myDialog: MyDialog

    private var dateSelected = ""
    private var officeIdSelected = 0

    private val REQUEST_FILTER = 120

    private var categoryReport: Int = 0 // 0 -> office, 1 -> manajemen, 2 -> sdm

    private var user : User? = null
    private var isManagement = false

    private lateinit var userResponse: UserResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentasi_report_kantor)

        myDialog = MyDialog(this)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryReport = intent.getIntExtra(CATEGORY_REPORT_KEY, 0)
        user = intent.getParcelableExtra(USER_KEY)
        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)

        initRv()

        dateSelected = getTodayDate()
        setDateText(getDateStringFormatted(Calendar.getInstance().time))

        btnFilter.setOnClickListener {
            if (!isManagement){
                startActivityForResult<FilterReportKantorActivity>(REQUEST_FILTER, CATEGORY_REPORT_KEY to categoryReport,
                    "user_response" to userResponse)
            } else {
                startActivityForResult<FilterReportKantorActivity>(REQUEST_FILTER, CATEGORY_REPORT_KEY to categoryReport)
            }
        }

        vm.presenceReportData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    if (isManagement) myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    val percentage = it.data.data.report.percentage.substring(0, it.data.data.report.percentage.length-1).toDouble().toInt()
                    circularProgressBar.progress = percentage.toFloat()
                    txtPercentage.text = percentage.toString() + "%"
                    if (it.data.data.presence.isEmpty()) txtEmpty.visible() else txtEmpty.gone()
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
            when(it){
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    txtSubReport.text = it.data.data[0].full_name
                    vm.getPresenceReport(userManagementId = it.data.data[0].id, date = dateSelected)
                    userResponse = it.data
                }
                is UiState.Error -> {
                    e(it.throwable)
                }
            }
        })

        when(categoryReport){
            0 -> {
                txtReport.text = "Kantor Cabang"
                txtSubReport.text = "Semua Kantor"
                vm.getPresenceReport(date = dateSelected)
                txtDaftarAbsensi.text = "Daftar absensi semua kantor cabang : "
                supportActionBar?.title = "Presentasi Report Kantor"
            }
            1 -> {
                txtReport.text = "Kantor Cabang"
                txtSubReport.text = "Semua Kantor"
                vm.getPresenceReport(roleId = 2, date = dateSelected)
                txtDaftarAbsensi.text = "Daftar absensi manajemen : "
                supportActionBar?.title = "Presentasi Report Manajemen"
            }
            2 -> {
                txtReport.text = "Manajemen"
                if (isManagement){
                    txtSubReport.text = user!!.full_name
                    vm.getPresenceReport(userManagementId = user!!.id, date = dateSelected)
                } else {
                    //get data user management
                    vm.getUserManagement()
                }
                txtDaftarAbsensi.text = "Daftar absensi semua manajemen : "
                supportActionBar?.title = "Presentasi Report SDM"

            }
        }

    }

    private fun setDateText(date: String) {
        txtDate.text = date
    }


    private fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvAbsensi.apply {
            isNestedScrollingEnabled = false
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_FILTER && resultCode == Activity.RESULT_OK) {
            data?.let {

                dateSelected = it.getStringExtra(DATE_FILTER_KEY).toString()

                val dateFormat = SimpleDateFormat(DATE_FORMAT)
                val date = dateFormat.parse(dateSelected)
                txtDate.text = getDateStringFormatted(date)

                btnFilter.setImageResource(R.drawable.ic_filter_on)

                when(categoryReport){
                    0 -> {
                        officeIdSelected = it.getIntExtra(OFFICE_ID_FILTER, 0)
                        val officaName = it.getStringExtra(OFFICE_NAME_FILTER)
                        txtSubReport.text = officaName


                        if (officeIdSelected != 0) txtDaftarAbsensi.text =
                            "Daftar absensi kantor cabang $officaName :" else
                            "Daftar absensi semua kantor cabang : "

                        groupAdapter.clear()
                        vm.getPresenceReport(officeId = officeIdSelected, date = dateSelected)
                    }
                    1 -> {
                        vm.getPresenceReport(roleId = 2, date = dateSelected)
                    }
                    2 -> {
                        val userManagementIdSelected = it.getIntExtra(USER_ID_KEY, 0)
                        val userManagementName = it.getStringExtra(USER_MANAGEMENT_NAME_KEY)
                        txtSubReport.text = userManagementName

                        if (userManagementIdSelected != 0) txtDaftarAbsensi.text =
                            "Daftar absensi manajemen $userManagementName :" else
                            "Daftar absensi semua manajemen : "
                        groupAdapter.clear()
                        vm.getPresenceReport(userManagementId = userManagementIdSelected, date = dateSelected)
                    }
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
