package id.android.kmabsensi.presentation.permission.manajemenizin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Permission
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.permission.PermissionItem
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.presentation.permission.detailizin.DetailIzinActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_manajemen_izin.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class ManajemenIzinActivity : BaseActivity() {
    private val homeViewModel: HomeViewModel by viewModel()
    private var user: User? = null

    private val vm: PermissionViewModel by inject()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    val roles = mutableListOf("Management", "SDM")

    var roleId = 0

    var isManagement = false
    var userManagementId = 0

    lateinit var myDialog: MyDialog

    var REQUEST_PENGAJUAN_IZIN = 152

    private val calendarDateForm = Calendar.getInstance()
    private val calendarDateTo = Calendar.getInstance()
    private val calendarDateAYearsLater = Calendar.getInstance()
    private var dateFrom: String = ""
    private var dateTo: String = ""
    private var permissionType: Int = 0

    private val permissions = mutableListOf<Permission>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manajemen_izin)

        var message = ""
        message = intent.getStringExtra("message") ?: ""
        if (!message.isNullOrBlank()) {
            createAlertSuccess(this, message)
        }

        setupToolbar("Manajemen Izin", isFilterVisible = true)

        myDialog = MyDialog(this)

        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)
        userManagementId = intent.getIntExtra(USER_ID_KEY, 0)

        if (isManagement) roles.removeAt(0)

        initRv()
        dateFrom = getTodayDate()
        dateTo = getTodayDate()

        vm.listPermissionData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    if (layout_empty.isVisible) layout_empty.gone()
                    showSkeleton(rvPermission, R.layout.skeleton_list_permission, groupAdapter)
                }
                is UiState.Success -> {
                    hideSkeleton()
                    groupAdapter.clear()
                    permissions.clear()
                    permissions.addAll(it.data.data.filter {
                        it.status == 0 || it.status == 2
                    })
                    if (permissions.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    permissions.forEach {
                        groupAdapter.add(PermissionItem(this, it) { permission ->
//                            login as sdm
                            if (user!!.role_id != 1){
                                startActivity(Intent(this, DetailIzinActivity::class.java)
                                    .putExtra(IS_FROM_MANAJEMEN_IZI, false)
                                    .putExtra(PERMISSION_DATA_KEY, it))
                            }else{
//                             login as admin
                                startActivityForResult<DetailIzinActivity>(
                                    REQUEST_PENGAJUAN_IZIN, PERMISSION_DATA_KEY to it,
                                    IS_FROM_MANAJEMEN_IZI to true
                                )
                            }
                        })
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })

        vm.approvePermissionResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) createAlertSuccess(
                        this,
                        it.data.message
                    ) else createAlertError(this, "Gagal", it.data.message)
                    vm.getListPermission(
                        roleId = roleId, userManagementId = userManagementId
                    )
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEmployetype.adapter = adapter

            spinnerEmployetype.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        roleId = if (isManagement) position + 3 else position + 2
//                        vm.getListPermission(
//                            roleId = roleId,
//                            userManagementId = userManagementId
//                        )
                        user = homeViewModel.getUserData()
                        if (user!!.role_id != 1){
                            btnFilter.invis()
                            menu_role.gone()
                            calendarDateAYearsLater.add(Calendar.YEAR, Calendar.YEAR)
                            val dateAYearsLater = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendarDateAYearsLater.time)
                            Log.d("userTAG", "onItemSelected: $dateAYearsLater")
                            user?.let { user ->
                                vm.filterPermission(
                                    userId = user.id,
                                    dateFrom = dateFrom,
                                    dateTo = dateAYearsLater,
                                    permissionType = permissionType
                                )
                            }
                        }else{
                            btnFilter.visible()
                            menu_role.visible()
                            vm.filterPermission(
                                roleId = roleId, userManagementId = userManagementId,
                                dateFrom = dateFrom,
                                dateTo = dateTo,
                                permissionType = permissionType
                            )
                        }
                    }

                }
        }

        btnFilter.setOnClickListener {
            showDialogFilter()
        }

        btnCopyClipboard.setOnClickListener {
            val dateFromFormatted = getDateString(SimpleDateFormat(DATE_FORMAT).parse(dateFrom))
            val dateToFormatted = getDateString(SimpleDateFormat(DATE_FORMAT).parse(dateFrom))
            val header =
                "[Confidential] \nRekap Data Izin Mitra Kampung Marketer\n=============================\n\n"
            val filter =
                "Filter::\nTgl Awal: ${dateFromFormatted}\nTgl Akhir: ${dateToFormatted}\n\n"
            var dataizin = "Data Izin\n=============================\n\n"
            val footer = "============================="

            val stringBuilder = StringBuilder()
            stringBuilder.append(header)
                .append(filter)
                .append(dataizin)

            permissions.forEach {
                val tipeIzin = when (it.permissionType) {
                    1 -> "Izin"
                    2 -> "Sakit"
                    else -> "Cuti"
                }
                val status = when (it.status) {
                    0 -> "REQUESTED"
                    2 -> "DISETUJUI"
                    else -> "DITOLAK"
                }

                stringBuilder.append(
                    "Nama Pemohon : ${it.user?.fullName}" +
                            "\nJenis Izin : $tipeIzin" +
                            "\nTgl Mulai : ${it.dateFrom}" +
                            "\nTgl Akhir : ${it.dateTo}" +
                            "\nNama Leader : ${it.management?.fullName}" +
                            "\nNama Kantor : ${it.user?.officeName}" +
                            "\nKeterangan : ${it.explanation}" +
                            "\nStatus Permintaan : $status\n\n\n"
                )
            }
            stringBuilder.append(footer)

            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("data izin", stringBuilder.toString())
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Data Izin copied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialogFilter() {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_filter_permission, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val edtStartDate = customView.findViewById<AppCompatEditText>(R.id.edtStartDate)
        val edtEndDate = customView.findViewById<AppCompatEditText>(R.id.edtEndDate)
        val spinnerPermission = customView.findViewById<Spinner>(R.id.spinnerPermission)
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

        // spinner izin
        ArrayAdapter.createFromResource(
            this,
            R.array.permission,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPermission.adapter = adapter

            spinnerPermission.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    permissionType = position
                }

            }
        }

        buttonFilter.setOnClickListener {
            dialog.dismiss()
            vm.filterPermission(
                roleId = roleId, userManagementId = userManagementId,
                dateFrom = dateFrom,
                dateTo = dateTo,
                permissionType = permissionType
            )
        }

        dialog.show()

    }

    private fun showDatePicker(isDateFrom: Boolean,callback: (Date) -> Unit) {

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

    private fun initRv() {
        rvPermission.apply {
            layoutManager = LinearLayoutManager(this@ManajemenIzinActivity)
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PENGAJUAN_IZIN && resultCode == Activity.RESULT_OK) {
//            vm.getListPermission(
//                roleId = roleId, userManagementId = userManagementId
//            )
            vm.filterPermission(
                roleId = roleId, userManagementId = userManagementId,
                dateFrom = dateFrom,
                dateTo = dateTo,
                permissionType = permissionType
            )
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, HomeActivity::class.java))
        finishAffinity()
        super.onBackPressed()
    }
}
