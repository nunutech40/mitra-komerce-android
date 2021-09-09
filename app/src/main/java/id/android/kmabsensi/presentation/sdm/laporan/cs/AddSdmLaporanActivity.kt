package id.android.kmabsensi.presentation.sdm.laporan.cs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.AddSdmReportParams
import id.android.kmabsensi.data.remote.body.EditSdmReportParams
import id.android.kmabsensi.data.remote.response.CsPerformance
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.PartnerDetail
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_add_invoice.*
import kotlinx.android.synthetic.main.activity_add_laporan_advertiser.*
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.*
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.btnSave
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.edtCatatan
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.edtPilihPartner
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.edtTanggal
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.view_nb
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddSdmLaporanActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()
    private val partnerVM: PartnerViewModel by viewModel()

//    private var partners = mutableListOf<Partner>()

    private val cal = Calendar.getInstance()
    private var dateSelected: Date? = null
    private lateinit var user: User

    private var totalLeads: Int = 0
    private var totalTransaction: Int = 0
    private var totalOrder: Int = 0

    private val RC_PICK_PARTNER = 122
    private var partnerSelected: Partner? = null

    //    private var ratingConversion: Double = 0.0
//    private var ratingOrder: Double = 0.0
    private var percentageRatingConversion: Double = 0.0
    private var percentageRatingOrder: Double = 0.0

    private var report: CsPerformance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sdm_laporan)

        report = intent.getParcelableExtra("report")
        setupToolbar(if (report != null) "Edit Laporan" else getString(R.string.tambah_laporan_title))

        user = sdmVM.getUserData()
        initViews()
        initListener()
        setupObserver()
        edtPilihPartner.setHint(getString(R.string.pilih_partner))

        btnSave.setOnClickListener {
            if (!formValidation()) {
                return@setOnClickListener
            }

            if (partnerSelected == null){
                createAlertError(this, "Peringatan", "Pilih partner terlebih dahulu.", 3000)
                return@setOnClickListener
            }

            if (report != null) {
                sdmVM.editSdmReport(
                    EditSdmReportParams(
                        report!!.id,
                        user.id,
                        partnerSelected!!.noPartner,
                        getDateString(dateSelected!!),
                        edtLeads.text.toString().toInt(),
                        edtTransaksi.text.toString().toInt(),
                        edtOrder.text.toString().toInt(),
                        conversion_rate = percentageRatingConversion,
                        order_rate = percentageRatingOrder,
                        notes = edtCatatan.text.toString()
                    )
                )
            } else {
                sdmVM.addSdmReport(
                    AddSdmReportParams(
                        user.id,
                        partnerSelected!!.noPartner,
                        getDateString(dateSelected!!),
                        edtLeads.text.toString().toInt(),
                        edtTransaksi.text.toString().toInt(),
                        edtOrder.text.toString().toInt(),
                        conversion_rate = percentageRatingConversion,
                        order_rate = percentageRatingOrder,
                        notes = edtCatatan.text.toString()
                    )
                )
            }

        }

        partnerVM.partners.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    report?.let { report ->
                        val partner = state.data.partners.find { it.partnerDetail.noPartner == report.noPartner }
                        partnerSelected = partner
                        edtPilihPartner.setText(partner?.fullName)
                    }
                }
                is UiState.Error -> {

                }
            }
        })
    }

    private fun setupObserver() {
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
    }

    private fun initListener() {

        edtLeads.doAfterTextChanged {
            try {
                it?.let {
                    if (it.isNotEmpty()) {
                        totalLeads = it.toString().toInt()
                    } else {
                        totalLeads = 0;
                    }
                    calculateRatingConversion()
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        edtTransaksi.doAfterTextChanged {
            try {
                it?.let {

                    if (it.isNotEmpty()) {
                        totalTransaction = it.toString().toInt()
                    } else {
                        totalTransaction = 0
                    }
                    edtOrder.isEnabled = totalTransaction > 0
                    calculateRatingConversion()
                    calculateRatingOrder()

                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        edtOrder.doAfterTextChanged {
            try {
                it?.let {
                    if (it.isNotEmpty()) {
                        totalOrder = it.toString().toInt()
                    } else {
                        totalOrder = 0
                    }
                    edtRatingOrder.setText("${(percentageRatingOrder)}%")
                    calculateRatingOrder()
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun calculateRatingOrder() {
        try {
            Log.d("mitraKm", "calculate rating order")
            if (totalTransaction <= 0) {
                percentageRatingOrder = 0.0
                edtRatingOrder.setText("${(percentageRatingOrder)}%")
                return
            }
            val ratingOrder = (totalOrder.toDouble() / totalTransaction.toDouble()) * 100
            percentageRatingOrder = ratingOrder.rounTwoDigitDecimal()
            edtRatingOrder.setText("${(percentageRatingOrder)}%")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun calculateRatingConversion() {
        try {
            Log.d("mitraKm", "calculate rating conversion")
            if (totalLeads <= 0) {
                percentageRatingConversion = 0.0
                edtRatingKonversi.setText("${(percentageRatingConversion)}%")
                return
            }
            val ratingConversion = (totalTransaction.toDouble() / totalLeads.toDouble()) * 100
            percentageRatingConversion = ratingConversion.rounTwoDigitDecimal()
            if (percentageRatingConversion < 30) {
                view_nb.visible()
            } else {
                view_nb.gone()
            }
            edtRatingKonversi.setText("${(percentageRatingConversion)}%")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun clearRaatingConversion() {
        totalLeads = 0
        totalTransaction = 0
        edtLeads.setText("")
        edtTransaksi.setText("")
        edtRatingKonversi.setText("0.0%")
    }

    private fun clearRatingOrder() {
        totalOrder = 0
        totalTransaction = 0
        edtOrder.setText("")
        edtTransaksi.setText("")
        edtRatingOrder.setText("0.0%")
    }

    private fun clearAllRatingField() {
        Log.d("mitraKm", "clear all")
        totalLeads = 0
        totalOrder = 0
        totalTransaction = 0
        edtLeads.setText("")
        edtOrder.setText("")
        edtTransaksi.setText("")
        edtRatingOrder.setText("0.0%")
        edtRatingKonversi.setText("0.0%")
    }

    private fun initViews() {
        dateSelected = cal.time
        edtTanggal.setText(getDateStringFormatted(dateSelected!!))
        edtTanggal.setOnClickListener {
            showDatePicker()
        }
        report?.let {
            dateSelected = parseStringDate(it.date)
            cal.time = dateSelected
            totalLeads = it.totalLeads
            totalTransaction = it.totalTransaction
            totalOrder = it.totalOrder
            percentageRatingConversion =
                it.conversionRate
            percentageRatingOrder =  it.orderRate


            edtTanggal.setText(getDateStringFormatted(dateSelected!!))
            edtLeads.setText(totalLeads.toString())
            edtTransaksi.setText(totalTransaction.toString())
            edtOrder.setText(totalOrder.toString())
            edtRatingKonversi.setText("${(percentageRatingConversion)}%")
            edtRatingOrder.setText("${(percentageRatingOrder)}%")

            partnerSelected = Partner(partnerDetail = PartnerDetail(noPartner = it.noPartner))
            partnerVM.getPartners()

            if (percentageRatingConversion < 30) {
                view_nb.visible()
            } else {
                view_nb.gone()
            }
            edtCatatan.setText(it.notes)

            edtOrder.isEnabled = totalTransaction > 0

            btnSave.text = getString(R.string.edit_laporan)
        }

        edtPilihPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(
                RC_PICK_PARTNER)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_PICK_PARTNER && resultCode == Activity.RESULT_OK) {
            partnerSelected = data?.getParcelableExtra<Partner>(PARTNER_DATA_KEY)
            edtPilihPartner.setText(partnerSelected?.fullName)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDatePicker() {
        MaterialDialog(this).show {
            datePicker(currentDate = cal) { dialog, date ->
                // Use date (Calendar)
                dateSelected = date.time
                cal.time = dateSelected
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

        if (view_nb.visibility == View.VISIBLE) {
            if (edtCatatan.text.toString().isEmpty()) {
                edtCatatan.error = "Kolom tidak boleh kosong."
                result = false
            } else {
                edtCatatan.error = null
            }
        }

        return result
    }
}