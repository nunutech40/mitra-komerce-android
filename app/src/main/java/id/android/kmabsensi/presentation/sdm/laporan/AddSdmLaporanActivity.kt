package id.android.kmabsensi.presentation.sdm.laporan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.AddSdmReportParams
import id.android.kmabsensi.data.remote.body.EditSdmReportParams
import id.android.kmabsensi.data.remote.response.CsPerformance
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class AddSdmLaporanActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()

    private val cal = Calendar.getInstance()
    private var dateSelected: Date? = null
    private lateinit var user: User

    private var totalLeads: Double = 0.0
    private var totalTransaction: Double = 0.0
    private var totalOrder: Double = 0.0
    private var ratingConversion: Double = 0.0
    private var ratingOrder: Double = 0.0

    private var report: CsPerformance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sdm_laporan)

        report = intent.getParcelableExtra("report")
        setupToolbar(if (report != null) "Edit Laporan" else getString(R.string.tambah_laporan_title))

        user = sdmVM.getUserData()
        initViews()
        initListener()

        sdmVM.crudResponse.observe(this, androidx.lifecycle.Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status) {
                        val intent = Intent()
                        intent.putExtra("message", state.data.message)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Gagal", state.data.message)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                    createAlertError(this, "Gagal", state.throwable.localizedMessage)
                }
            }
        })

        btnSave.setOnClickListener {
            if (!formValidation()) {
                return@setOnClickListener
            }

            if (report != null) {
                sdmVM.editSdmReport(
                    EditSdmReportParams(
                        report!!.id,
                        user.id,
                        getDateString(dateSelected!!),
                        edtLeads.text.toString().toInt(),
                        edtTransaksi.text.toString().toInt(),
                        edtOrder.text.toString().toInt(),
                        conversion_rate = roundOffDecimal(ratingConversion),
                        order_rate = roundOffDecimal(ratingOrder),
                        notes = edtCatatan.text.toString()
                    )
                )
            } else {
                sdmVM.addSdmReport(
                    AddSdmReportParams(
                        user.id,
                        getDateString(dateSelected!!),
                        edtLeads.text.toString().toInt(),
                        edtTransaksi.text.toString().toInt(),
                        edtOrder.text.toString().toInt(),
                        conversion_rate = roundOffDecimal(ratingConversion),
                        order_rate = roundOffDecimal(ratingOrder),
                        notes = edtCatatan.text.toString()
                    )
                )
            }

        }
    }

    private fun initListener() {
        edtLeads.textChanged {
            if (it.isNotEmpty()) {
                totalLeads = it.toString().toDouble()
                calculateRatingConversion()
            } else {
                clearRaatingConversion()
            }
        }

        edtTransaksi.textChanged {
            if (it.isNotEmpty()) {
                totalTransaction = it.toString().toDouble()
                if (totalLeads > 0) calculateRatingConversion() else clearRaatingConversion()
                if (totalTransaction > 0) calculateRatingOrder() else clearRatingOrder()
            } else {
                clearAllRatingField()
            }
        }

        edtOrder.textChanged {
            if (it.isNotEmpty() && totalTransaction > 0) {
                totalOrder = it.toString().toDouble()
                calculateRatingOrder()
            } else {
                clearRatingOrder()
            }
        }
    }

    private fun calculateRatingOrder() {
        ratingOrder = (totalOrder / totalTransaction)
        val formattedRatingOrder = roundOffDecimal(ratingOrder) * 100
        edtRatingOrder.setText("${(formattedRatingOrder)}%")
    }

    private fun calculateRatingConversion() {
        ratingConversion = (totalTransaction / totalLeads)
        val formattedRatingKonversi = roundOffDecimal(ratingConversion ) * 100
        edtRatingKonversi.setText("${(formattedRatingKonversi)}%")
    }

    private fun clearRaatingConversion() {
        totalLeads = 0.0
        totalTransaction = 0.0
        edtRatingKonversi.setText("0.0%")
    }

    private fun clearRatingOrder() {
        totalOrder = 0.0
        totalTransaction = 0.0
        edtRatingOrder.setText("0.0%")
    }

    private fun clearAllRatingField() {
        totalLeads = 0.0
        totalOrder = 0.0
        totalTransaction = 0.0
        edtRatingOrder.setText("0.0%")
        edtRatingKonversi.setText("0.0%")
    }

    private fun initViews() {
        report?.let {

            dateSelected = parseStringDate(it.date)
            totalLeads = it.totalLeads
            totalTransaction = it.totalTransaction
            totalOrder = it.totalOrder
            ratingConversion =
                roundOffDecimal(it.conversionRate) * 100
            ratingOrder = roundOffDecimal(it.orderRate) * 100


            edtTanggal.setText(getDateString(dateSelected!!))
            edtLeads.setText(totalLeads.toString())
            edtTransaksi.setText(totalTransaction.toString())
            edtOrder.setText(totalOrder.toString())
            edtRatingKonversi.setText("${(ratingConversion)}%")
            edtRatingOrder.setText("${(ratingOrder)}%")
            edtCatatan.setText(it.notes)

            btnSave.text = getString(R.string.edit_laporan)
        }
        dateSelected = cal.time
        edtTanggal.setText(getDateStringFormatted(dateSelected!!))
        edtTanggal.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        MaterialDialog(this).show {
            datePicker { dialog, date ->
                // Use date (Calendar)
                dateSelected = date.time
                setDateText(getDateStringFormatted(date.time))
            }
        }
    }

    private fun setDateText(dateString: String) {
        edtTanggal.setText(dateString)
        edtTanggal.error = null
    }

    private fun formValidation(): Boolean {
        var result = true
        if (edtLeads.text.toString().isEmpty()) {
            edtLeads.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtLeads.error = null
        }

        if (edtTransaksi.text.toString().isEmpty()) {
            edtTransaksi.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtTransaksi.error = null
        }

        if (edtOrder.text.toString().isEmpty()) {
            edtOrder.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtOrder.error = null
        }

        if (edtRatingKonversi.text.toString().isEmpty()) {
            edtRatingKonversi.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtRatingKonversi.error = null
        }

        if (edtRatingOrder.text.toString().isEmpty()) {
            edtRatingOrder.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtRatingOrder.error = null
        }

        return result
    }
}