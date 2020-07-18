package id.android.kmabsensi.presentation.sdm.modekerja

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils.split
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.suke.widget.SwitchButton
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.WorkConfigParams
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.WorkConfigViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_mode_kerja.*
import kotlinx.android.synthetic.main.item_row_partner_off.*
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class ModeKerjaActivity : BaseActivity() {

    private val workConfigVM: WorkConfigViewModel by viewModel()

    private val calendarDateForm = Calendar.getInstance()
    private val calendarDateTo = Calendar.getInstance()
    private var dateFrom: String = ""
    private var dateTo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_kerja)
        setupToolbar(getString(R.string.mode_kerja))

        dateFrom = getTodayDate()
        dateTo = getTodayDate()

        switchButton.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
            //TODO do your job
            showDialogModeKerja(isChecked)
        })

        observeResult()
    }

    private fun observeResult(){
        workConfigVM.workConfigUpdateResult.observe(this, androidx.lifecycle.Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    createAlertSuccess(this, state.data.message)
                    state.data.data.apply {
                        if (workMode == "WFH"){
                            layoutDetailWorkConfig.visible()
                            val dateStart: LocalDate =  LocalDate.parse(wfhStartDate.split(" ")[0])
                            val dateEnd: LocalDate =  LocalDate.parse(wfhEndDate.split(" ")[0])
                            edtStartDate.setText(localDateFormatter(dateStart))
                            edtEndDate.setText(localDateFormatter(dateEnd))
                            txtWorkScope.text = workScope
                        } else {
                            layoutDetailWorkConfig.gone()
                        }
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                    createAlertError(this, "Failed", state.throwable.message.toString())
                }
            }
        })
    }

    private fun showDialogModeKerja(isWFH: Boolean){
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_mode_kerja, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val edtStartDate = customView.findViewById<AppCompatEditText>(R.id.edtStartDate)
        val edtEndDate = customView.findViewById<AppCompatEditText>(R.id.edtEndDate)
        val spinnerRole = customView.findViewById<Spinner>(R.id.spinnerRole)
        val btnSimpan = customView.findViewById<Button>(R.id.btnSimpan)

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

        // spinner role
        ArrayAdapter.createFromResource(
            this,
            R.array.work_scope,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRole.adapter = adapter

            spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
//                    permissionType = position
                }

            }
        }

        btnSimpan.setOnClickListener {
            dialog.dismiss()
            workConfigVM.updateWorkConfig(
                workConfigParams = WorkConfigParams(
                    work_mode = if (isWFH) "WFH" else "WFO",
                    work_scope = spinnerRole.selectedItem.toString(),
                    wfh_start_date = dateFrom,
                    wfh_end_date = dateTo
                )
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