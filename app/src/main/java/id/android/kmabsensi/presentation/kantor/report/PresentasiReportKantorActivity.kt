package id.android.kmabsensi.presentation.kantor.report

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.Timber.e
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import iammert.com.expandablelib.ExpandableLayout
import iammert.com.expandablelib.Section
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.ListAlphaParams
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.report.adapter.PresentasiAdapter
import id.android.kmabsensi.presentation.kantor.report.filter.FilterReportKantorActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_filter_report_kantor.*
import kotlinx.android.synthetic.main.activity_presentasi_report_kantor.*
import kotlinx.android.synthetic.main.activity_presentasi_report_kantor.toolbar
import kotlinx.android.synthetic.main.fragment_home_management.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class PresentasiReportKantorActivity : BaseActivity() {
    companion object{
        const val _errorkantor = "_errorkantor"
        const val _loadingkantor = "_loadingkantor"
    }
//    private val groupAdapter: GroupAdapter<GroupieViewHolder> by inject()

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

    //for expandable layout
    val section = Section<String, List<Alpha>>()
    var isSectionAdded = false
    private lateinit var presentasiAdapter : PresentasiAdapter
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

        initExpandableLayout()
        setupListener()
        setupObserver()

        when (categoryReport) {
            0 -> {
                txtReport.text = "Kantor Cabang"
                setupToolbarTitle("Presentasi Report Kantor")
                vm.getDataOffice()
            }
            1 -> {
                txtReport.text = "Kantor Cabang"
                txtSubReport.text = "Semua Kantor"
                vm.getPresenceReportFilteredPaging(roleId = 2, startDate = dateFrom, endDate = dateTo).observe(this, {
                    presentasiAdapter.submitList(it)
                })
                vm.getReportFiltered().observe(this, { Log.d("_errorPresentation", "data Report = $it") })
                vm.getPresenceReportFiltered(roleId = 2, startDate = dateFrom, endDate = dateTo)
                vm.getListAlpha(ListAlphaParams(role_id = 2, start_date = dateFrom, end_date = dateTo))
                setupToolbarTitle("Presentasi Report Manajemen")
            }
            2 -> {
                txtReport.text = "Manajemen"
                if (isManagement) {
                    user?.let {
                        txtSubReport.text = it.full_name
                        vm.getPresenceReportFilteredPaging(userManagementId = it.id, startDate = dateFrom, endDate = dateTo).observe(this, {
                            presentasiAdapter.submitList(it)
                        })
                        vm.getReportFiltered().observe(this, { Log.d("_errorPresentation", "data Report = $it") })
                        vm.getPresenceReportFiltered(userManagementId = it.id, startDate = dateFrom, endDate = dateTo)
                        vm.getListAlpha(ListAlphaParams(user_management_id = it.id, start_date = dateFrom, end_date = dateTo))
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

    private fun setupObserver() {
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
                    Log.d("_asda", "data filter = ${it.data.data.presence}")
//                    it.data.data.presence.forEach {
//                        groupAdapter.add(AbsensiReportItem(it))
//                    }
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
                        vm.getListAlpha(ListAlphaParams(user_management_id = it.data.data[0].id, start_date = dateFrom, end_date = dateTo))
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
                    vm.getListAlpha(ListAlphaParams(office_id = it.data.data[0].id, start_date = dateFrom, end_date = dateTo))
                    officeResponse = it.data
                }
                is UiState.Error -> {

                }
            }
        })

        vm.alphaAttendances.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> Log.d(_loadingkantor, "loadingkantor")
                is UiState.Success -> {
                    if (!isSectionAdded) {
                        expandableLayoutAlpha.addSection(getSectionAlpha(state.data.data))
                    } else {
                        expandableLayoutAlpha.sections[0].parent = state.data.data.size.toString()
                        expandableLayoutAlpha.sections[0].children.clear()
                        expandableLayoutAlpha.sections[0].children.add(state.data.data)
                        expandableLayoutAlpha.notifyParentChanged(0)
                    }
                }
                is UiState.Error -> Log.d(_errorkantor, "errorkantor")
            }
        })

    }

    private fun setupListener() {
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
    }

    private fun initExpandableLayout(){
        expandableLayoutAlpha.setRenderer(object : ExpandableLayout.Renderer<String, List<Alpha>> {
            override fun renderChild(
                view: View?,
                model: List<Alpha>?,
                parentPosition: Int,
                childPosition: Int
            ) {
                val groupAdapterAlpha = GroupAdapter<GroupieViewHolder>()
                view?.findViewById<RecyclerView>(R.id.rvAlpha)?.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = groupAdapterAlpha
                }
                groupAdapterAlpha.clear()
                model?.let {
                    it.forEachIndexed { index, alpha ->
                        groupAdapterAlpha.add(AlphaItem(alpha, index+1))
                    }
                }
            }

            override fun renderParent(
                view: View?,
                model: String?,
                isExpanded: Boolean,
                parentPosition: Int
            ) {
                view?.findViewById<ImageView>(R.id.arrow)
                    ?.setBackgroundResource(if (isExpanded) R.drawable.ic_keyboard_arrow_up else R.drawable.ic_keyboard_arrow_down)
                view?.findViewById<TextView>(R.id.textTotalTidakAbsen)?.text = model
            }
        })
        expandableLayoutAlpha.setExpandListener { parentIndex: Int, parent: String, view: View? ->
            view?.findViewById<ImageView>(R.id.arrow)
                ?.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
        }

        expandableLayoutAlpha.setCollapseListener { parentIndex: Int, parent: String, view: View? ->
            view?.findViewById<ImageView>(R.id.arrow)
                ?.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
        }
    }

    private fun getSectionAlpha(alpha: List<Alpha>): Section<String, List<Alpha>> {
        section.parent = alpha.size.toString()
        section.children.add(alpha)
        isSectionAdded = true
        return section
    }

    private fun setupToolbarTitle(title: String){
        txtTitle.text = title
    }

    private fun setDateText(date: String) {
        txtDate.text = date + " - " + date
    }


    private fun initRv() {
        presentasiAdapter = PresentasiAdapter(this)
        rvAbsensiPaged.apply {
            adapter = presentasiAdapter
            setHasFixedSize(true)
        }
//        val linearLayoutManager = LinearLayoutManager(this)
//        rvAbsensi.apply {
//            isNestedScrollingEnabled = false
//            layoutManager = linearLayoutManager
//            adapter = groupAdapter
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_FILTER && resultCode == Activity.RESULT_OK) {
            data?.let {

                dateFrom = it.getStringExtra(DATE_FILTER_KEY).toString()
                dateTo = it.getStringExtra(END_DATE_FILTER_KEY).toString()

                val dateFormat = SimpleDateFormat(DATE_FORMAT)
                val date = dateFormat.parse(dateFrom)
                val actualDateTo = dateFormat.parse(dateTo)

                txtDate.text = getDateStringFormatted(date) + " - " + getDateStringFormatted(actualDateTo)

                when (categoryReport) {
                    0 -> {
                        officeIdSelected = it.getIntExtra(OFFICE_ID_FILTER, 0)
                        val officaName = it.getStringExtra(OFFICE_NAME_FILTER)
                        txtSubReport.text = officaName
//                        groupAdapter.clear()
                        vm.getPresenceReportFilteredPaging(officeId = officeIdSelected, startDate = dateFrom, endDate = dateTo).observe(this, {
                            presentasiAdapter.submitList(it)
                        })
                        vm.getReportFiltered().observe(this, { Log.d("_errorPresentation", "data Report = $it") })
                        vm.getPresenceReportFiltered(officeId = officeIdSelected, startDate = dateFrom, endDate = dateTo)
                        vm.getListAlpha(ListAlphaParams(office_id = officeIdSelected, start_date = dateFrom, end_date = dateTo))
                    }
                    1 -> {
//                        groupAdapter.clear()
                        vm.getPresenceReportFilteredPaging(roleId = 2, startDate = dateFrom, endDate = dateTo).observe(this, {
                            presentasiAdapter.submitList(it)
                        })
                        vm.getReportFiltered().observe(this, { Log.d("_errorPresentation", "data Report = $it") })
                        vm.getPresenceReportFiltered(roleId = 2, startDate = dateFrom, endDate = dateTo)
                        vm.getListAlpha(ListAlphaParams(role_id = 2, start_date = dateFrom, end_date = dateTo))
                    }
                    2 -> {
                        val userManagementIdSelected = it.getIntExtra(USER_ID_KEY, 0)
                        val userManagementName = it.getStringExtra(USER_MANAGEMENT_NAME_KEY)
                        txtSubReport.text = userManagementName
//                        groupAdapter.clear()
                        vm.getPresenceReportFilteredPaging(
                                userManagementId = userManagementIdSelected,
                                startDate = dateFrom, endDate = dateTo
                        ).observe(this, {
                            presentasiAdapter.submitList(it)
                        })
                        vm.getReportFiltered().observe(this, { Log.d("_errorPresentation", "data Report = $it") })
                        vm.getPresenceReportFiltered(
                            userManagementId = userManagementIdSelected,
                            startDate = dateFrom, endDate = dateTo
                        )
                        vm.getListAlpha(ListAlphaParams(user_management_id = userManagementIdSelected, start_date = dateFrom, end_date = dateTo))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
