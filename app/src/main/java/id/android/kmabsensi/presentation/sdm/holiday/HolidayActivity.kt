package id.android.kmabsensi.presentation.sdm.holiday

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.AddHolidayParams
import id.android.kmabsensi.data.remote.body.EditHolidayParams
import id.android.kmabsensi.data.remote.response.Holiday
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.HolidayViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_holiday.*
import org.joda.time.LocalDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HolidayActivity : BaseActivity() {

    private val holidayVM: HolidayViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val calendarDateStart = Calendar.getInstance()
    private val calendarDateEnd = Calendar.getInstance()
    private var dateStart = ""
    private var dateEnd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holiday)
        setupToolbar(getString(R.string.hari_libur_label))
        initRv()
        observeData()

        holidayVM.getHoliday()

        swipeRefresh.setOnRefreshListener {
            holidayVM.getHoliday()
        }

        fabAdd.setOnClickListener {
            showAddHolidayDialog(null)
        }
    }

    fun observeData(){
        holidayVM.holidays.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    groupAdapter.clear()
                    if (state.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    state.data.data.forEach {
                        groupAdapter.add(HolidayItem(it, object : OnHolidayListener {
                            override fun onDeleteClicked(holiday: Holiday) {
                                showDialogConfirmDelete(this@HolidayActivity, "Hapus Hari Libur"){
                                    holidayVM.deleteHoliday(holiday.id)
                                }
                            }

                            override fun onEditClicked(holiday: Holiday) {
                                showAddHolidayDialog(holiday)
                            }

                        }))
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })

        holidayVM.crudResponse.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status){
                        createAlertSuccess(this, state.data.message)
                    } else {
                        createAlertError(this, "Failed", state.data.message)
                    }
                    holidayVM.getHoliday()
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }
    fun initRv(){
        rvHoliday.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    /*
     * if holiday null thats is for add holiday, if not null for edit holiday
     */
    private fun showAddHolidayDialog(holiday: Holiday?){

        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_add_holiday, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val textTitle = customView.findViewById<AppCompatTextView>(R.id.textTitle)
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val btnSimpan = customView.findViewById<Button>(R.id.btnSimpan)
        val edtJudul = customView.findViewById<AppCompatEditText>(R.id.edtJudul)
        val edtStartDate = customView.findViewById<AppCompatEditText>(R.id.edtStartDate)
        val edtEndDate = customView.findViewById<AppCompatEditText>(R.id.edtEndDate)

        holiday?.let {
            dateStart = holiday.startDate
            dateEnd = holiday.endDate
            edtJudul.setText(it.eventName)
            val startDate: LocalDate = LocalDate.parse(holiday.startDate)
            val endDate: LocalDate = LocalDate.parse(holiday.endDate)
            edtStartDate.setText(localDateFormatter(startDate))
            edtEndDate.setText(localDateFormatter(endDate))
            btnSimpan.text = getString(R.string.ubah_label)
            textTitle.text = getString(R.string.ubah_hari_libur)
        } ?: kotlin.run {
            calendarDateStart.time = Calendar.getInstance().time
            calendarDateEnd.time = Calendar.getInstance().time
            dateStart = getDateString(calendarDateStart.time)
            dateEnd = getDateString(calendarDateEnd.time)
            edtStartDate.setText(getDateStringFormatted(calendarDateStart.time))
            edtEndDate.setText(getDateStringFormatted(calendarDateEnd.time))
            btnSimpan.text = getString(R.string.tambah)
            textTitle.text = getString(R.string.tambah_hari_libur)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        edtStartDate.setOnClickListener { view ->
            showDatePicker(true) {
                dateStart = getDateString(it)
                edtStartDate.setText(getDateStringFormatted(it))
            }
        }

        edtEndDate.setOnClickListener { view ->
            showDatePicker(false) {
                dateEnd = getDateString(it)
                edtEndDate.setText(getDateStringFormatted(it))
            }
        }

        btnSimpan.setOnClickListener {
            val judul = edtJudul.text.toString()
            if (judul.isEmpty()) {
                edtJudul.error = getString(R.string.judul_empty_message)
                return@setOnClickListener
            }
            if (holiday == null){
                holidayVM.addHoliday(AddHolidayParams(
                    judul,
                    dateStart,
                    dateEnd,
                    ""
                ))
            } else {
                holidayVM.editHoliday(EditHolidayParams(
                    holiday.id,
                    judul,
                    dateStart,
                    dateEnd,
                    ""
                ))
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDatePicker(isDateFrom: Boolean,callback: (Date) -> Unit) {

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                if (isDateFrom) {
                    calendarDateStart.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    callback(calendarDateStart.time)
                } else {
                    calendarDateEnd.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    callback(calendarDateEnd.time)
                }
            },
            (if (isDateFrom) calendarDateStart else calendarDateEnd).get(Calendar.YEAR),
            (if (isDateFrom) calendarDateStart else calendarDateEnd).get(Calendar.MONTH),
            (if (isDateFrom) calendarDateStart else calendarDateEnd).get(Calendar.DAY_OF_MONTH)
        )
        if (!isDateFrom) datePickerDialog.datePicker.minDate = calendarDateStart.timeInMillis
        datePickerDialog.show()

    }
}