package id.android.kmabsensi.presentation.permission.manajemenizin

import android.R.attr.label
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Permission
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.permission.PermissionItem
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.presentation.permission.detailizin.DetailIzinActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_manajemen_izin.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class ManajemenIzinActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val roles = mutableListOf("Management", "SDM")

    var roleId = 0

    var isManagement = false
    var userManagementId = 0

    lateinit var myDialog: MyDialog

    var REQUEST_PENGAJUAN_IZIN = 152

    private val calendarDateForm = Calendar.getInstance()
    private val calendarDateTo = Calendar.getInstance()
    private var dateFrom: String = ""
    private var dateTo: String = ""
    private var status: Int = 0

    private val permissions = mutableListOf<Permission>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manajemen_izin)

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
                    groupAdapter.clear()
                    hideSkeleton()
                    if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    if (permissions.isNotEmpty()) permissions.clear()
                    permissions.addAll(it.data.data)
                    it.data.data.forEach {
                        groupAdapter.add(PermissionItem(it) { permission ->
                            startActivityForResult<DetailIzinActivity>(
                                REQUEST_PENGAJUAN_IZIN, PERMISSION_DATA_KEY to it,
                                IS_FROM_MANAJEMEN_IZI to true
                            )
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
                        vm.getListPermission(
                            roleId = roleId,
                            userManagementId = userManagementId
                        )
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
                val tipeIzin = when (it.permission_type) {
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
                    "Nama Pemohon : ${it.user?.full_name}" +
                            "\nJenis Izin : $tipeIzin" +
                            "\nTgl Mulai : ${it.date_from}" +
                            "\nTgl Akhir : ${it.date_to}" +
                            "\nNama Leader : ${it.management?.full_name}" +
                            "\nNama Kantor : ${it.user?.office_name}" +
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
                    status = position + 1
                }

            }
        }

        buttonFilter.setOnClickListener {
            dialog.dismiss()
            vm.filterPermission(
                roleId = roleId, userManagementId = userManagementId,
                dateFrom = dateFrom,
                dateTo = dateTo,
                status = status
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
                    calendarDateTo.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
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
            vm.getListPermission(
                roleId = roleId, userManagementId = userManagementId
            )
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
