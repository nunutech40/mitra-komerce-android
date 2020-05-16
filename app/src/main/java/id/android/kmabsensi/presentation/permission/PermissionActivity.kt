package id.android.kmabsensi.presentation.permission

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
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
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.permission.detailizin.DetailIzinActivity
import id.android.kmabsensi.presentation.permission.tambahizin.FormIzinActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_permission.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.util.*

class PermissionActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    lateinit var user: User

    private val REQ_FORM_IZIN = 212

    private val calendarDateForm = Calendar.getInstance()
    private val calendarDateTo = Calendar.getInstance()
    private var dateFrom: String = ""
    private var dateTo: String = ""
    private var permissionType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        setupToolbar("History Izin", isFilterVisible = true)

        user = intent.getParcelableExtra(USER_KEY)

        initRv()

        vm.listPermissionData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    if (layout_empty.isVisible) layout_empty.gone()
                    showSkeleton(rvPermission, R.layout.skeleton_list_permission, groupAdapter)
                }
                is UiState.Success -> {
                    hideSkeleton()
                    if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    it.data.data.forEach {
                        groupAdapter.add(PermissionItem(it) {
                            startActivity<DetailIzinActivity>(
                                PERMISSION_DATA_KEY to it,
                                IS_FROM_MANAJEMEN_IZI to false
                            )
                        })
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })

        fabFromIzin.setOnClickListener {
            startActivityForResult<FormIzinActivity>(REQ_FORM_IZIN, USER_KEY to user)
        }

        dateFrom = getTodayDate()
        dateTo = getTodayDate()

//        vm.getListPermission(userId = user.id)
        vm.filterPermission(
            userId = user.id,
            dateFrom = dateFrom,
            dateTo = dateTo,
            permissionType = permissionType
        )
        btnFilter.setOnClickListener {
            showDialogFilter()
        }

    }

    fun initRv() {
        rvPermission.apply {
            layoutManager = LinearLayoutManager(this@PermissionActivity)
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_FORM_IZIN && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra("message")
            createAlertSuccess(this, message.toString())
            groupAdapter.clear()
//            vm.getListPermission(userId = user.id)
            vm.filterPermission(
                userId = user.id,
                dateFrom = dateFrom,
                dateTo = dateTo,
                permissionType = permissionType
            )
        }
        super.onActivityResult(requestCode, resultCode, data)
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
                    permissionType = position + 1
                }

            }
        }

        buttonFilter.setOnClickListener {
            dialog.dismiss()
            vm.filterPermission(
                userId = user.id,
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
}
