package id.android.kmabsensi.presentation.sdm.laporan.advertiser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.AddAdvertiserReportParams
import id.android.kmabsensi.data.remote.body.EditAdvertiserReportParams
import id.android.kmabsensi.data.remote.response.AdvertiserReport
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.PartnerDetail
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_add_invoice.*
import kotlinx.android.synthetic.main.activity_add_laporan_advertiser.*
import kotlinx.android.synthetic.main.activity_add_laporan_advertiser.edtPilihPartner
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddLaporanAdvertiserActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()
    private val partnerVM: PartnerViewModel by viewModel()

    private val platforms = listOf("FB & IG Ads", "Google Ads", "TikTok Ads")
    private var platformSelected = 0

    private val cal = Calendar.getInstance()
    private var dateSelected: Date? = null

    private val RC_PICK_PARTNER = 122
    private var partnerSelected: Partner? = null

    private var totalView = 0
    private var totalAdClick = 0
    private var totalVisitor = 0
    private var totalContactClick = 0
    private var totalLeadsCs = 0
    private var totalAdCost = 0

    private var ctrLink = 0.0
    private var ratioLp = 0.0
    private var cpr = 0

    private var advertiserReport: AdvertiserReport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_laporan_advertiser)
        advertiserReport = intent.getParcelableExtra("report")
        setupToolbar(if (advertiserReport == null) "Tambah Laporan" else "Edit Laporan")
        initViewListener()
        initViews()
        observeResult()
        edtPilihPartner.setHint(getString(R.string.pilih_partner))
    }

    private fun initViews() {

        advertiserReport?.let {
            dateSelected = parseStringDate(it.date)
            cal.time = dateSelected
            totalView = it.totalView
            totalAdClick = it.totalAdClick
            totalVisitor = it.totalVisitor
            totalContactClick = it.totalContactClick
            totalLeadsCs = it.totalLeadsCs
            totalAdCost = it.adCost.toInt()
            ctrLink = it.ctrLink.toDouble()
            ratioLp = it.ratioLp.toDouble()
            cpr = it.cpr.toInt()

            edtJumlahTayangan.setText("$totalView")
            edtKlikIklan.setText("$totalAdClick")
            edtJumlahVisitor.setText("$totalVisitor")
            edtKlikKontak.setText("$totalContactClick")
            edtLeadsCS.setText("$totalLeadsCs")
            edtBiayaIklan.setText("$totalAdCost")
            edtCtrLink.setText("${ctrLink}%")
            edtRasioLP.setText("${ratioLp}%")
            edtCPR.setText("$cpr")
            edtCatatan.setText(it.notes)
            btnSave.text = getString(R.string.edit_laporan)

            partnerSelected = Partner(partnerDetail = PartnerDetail(noPartner = it.noPartner))

            partnerVM.getPartners()
        }

        lblTayangan.setOnClickListener { showInfo(R.string.info_tayangan) }
        lblKlikIklan.setOnClickListener { showInfo(R.string.info_klik_iklan) }
        lblVisitor.setOnClickListener { showInfo(R.string.info_visitor) }
        lblKlikKontak.setOnClickListener { showInfo(R.string.info_klik_kontak) }
        lblLeadsCs.setOnClickListener { showInfo(R.string.info_leads_cs) }
        lblBiayaIklan.setOnClickListener { showInfo(R.string.info_biaya_iklan) }
        lblCtrLink.setOnClickListener { showInfo(R.string.info_ctr_link) }
        lblRasioLP.setOnClickListener { showInfo(R.string.info_rasio_lp) }
        lblCpr.setOnClickListener { showInfo(R.string.info_cpr) }
        edtTanggal.setOnClickListener { showDatePicker() }

        ArrayAdapter(
            this,
            R.layout.spinner_item,
            platforms
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPlatform.adapter = it

            spinnerPlatform.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    platformSelected = p2 + 1
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
        }

        if (advertiserReport == null) dateSelected = cal.time
        edtTanggal.setText(getDateStringFormatted(dateSelected!!))
        edtTanggal.setOnClickListener {
            showDatePicker()
        }

        edtPilihPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(
                RC_PICK_PARTNER)
        }

        btnSave.setOnClickListener {
            if (!formValidation()) {
                return@setOnClickListener
            }

            if (advertiserReport == null) {
                val params = AddAdvertiserReportParams(
                    user_id = sdmVM.getUserData().id,
                    no_partner = partnerSelected!!.partnerDetail.noPartner,
                    date = getDateString(dateSelected!!),
                    platform_type = platformSelected,
                    total_view = edtJumlahTayangan.text.toString().toInt(),
                    total_ad_click = edtKlikIklan.text.toString().toInt(),
                    total_visitor = edtJumlahVisitor.text.toString().toInt(),
                    total_contact_click = edtKlikKontak.text.toString().toInt(),
                    total_leads_cs = edtLeadsCS.text.toString().toInt(),
                    ad_cost = edtBiayaIklan.text.toString().replace(".", "").toLong(),
                    ctr_link = edtCtrLink.text.toString().replace("%", "").toDouble(),
                    ratio_lp = edtRasioLP.text.toString().replace("%", "").toDouble(),
                    cpr = edtCPR.text.toString().replace(".", "").toLong(),
                    notes = edtCatatan.text.toString()
                )

                sdmVM.addAdvertiserReport(params)
            } else {
                val params = EditAdvertiserReportParams(
                    id = advertiserReport!!.id,
                    user_id = sdmVM.getUserData().id,
                    no_partner = partnerSelected!!.partnerDetail.noPartner,
                    date = getDateString(dateSelected!!),
                    platform_type = platformSelected,
                    total_view = edtJumlahTayangan.text.toString().toInt(),
                    total_ad_click = edtKlikIklan.text.toString().toInt(),
                    total_visitor = edtJumlahVisitor.text.toString().toInt(),
                    total_contact_click = edtKlikKontak.text.toString().toInt(),
                    total_leads_cs = edtLeadsCS.text.toString().toInt(),
                    ad_cost = edtBiayaIklan.text.toString().replace(".", "").toLong(),
                    ctr_link = edtCtrLink.text.toString().replace("%", "").toDouble(),
                    ratio_lp = edtRasioLP.text.toString().replace("%", "").toDouble(),
                    cpr = edtCPR.text.toString().replace(".", "").toLong(),
                    notes = edtCatatan.text.toString()
                )

                sdmVM.editAdvertiserReport(params)
            }
        }

    }

    private fun observeResult() {
        sdmVM.crudResponse.observe(this, { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status) {
                        createAlertSuccess(this, state.data.message)
                        val intent = Intent()
                        intent.putExtra("message", state.data.message)
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Gagal", state.data.message)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })

        partnerVM.partners.observe(this, { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    advertiserReport?.let { report ->
                        val partner =
                            state.data.partners.find { it.partnerDetail.noPartner == report.noPartner }
                        partnerSelected = partner
                        edtPilihPartner.setText(partner?.fullName)
                    }
                }
                is UiState.Error -> {

                }
            }
        })
    }

    private fun calculateCtr() {
        if (totalView == 0) {
            ctrLink = 0.0
        } else {
            val ctr = (totalAdClick.toDouble() / totalView.toDouble()) * 100
            ctrLink = ctr.rounTwoDigitDecimal()
        }
        edtCtrLink.setText("$ctrLink%")
    }

    private fun calculateRatioLP() {
        if (totalVisitor == 0) {
            ratioLp = 0.0
        } else {
            val rasioLP = (totalContactClick.toDouble() / totalVisitor.toDouble()) * 100
            ratioLp = rasioLP.rounTwoDigitDecimal()
        }
        edtRasioLP.setText("$ratioLp%")
    }

    private fun calculateCpr() {
        if (totalLeadsCs == 0) {
            cpr = 0
        } else {
            cpr = totalAdCost / totalLeadsCs
        }
        edtCPR.setText("$cpr")
    }

    private fun initViewListener() {
        val textWatcher = RupiahTextWatcher(edtBiayaIklan)
        edtBiayaIklan.addTextChangedListener(textWatcher)
        textWatcher.setListener(object : OnAfterTextChanged {
            override fun afterTextChanged(text: String?) {
                text?.let {
                    totalAdCost =
                        if (text.isNotEmpty()) text.toString().replace(".", "").toInt() else 0
                    calculateCpr()
                }
            }

        })
        edtCPR.addTextChangedListener(RupiahTextWatcher(edtCPR))

        edtJumlahTayangan.doAfterTextChanged {
            it?.let { text ->
                totalView = if (text.isNotEmpty()) text.toString().toInt() else 0
                calculateCtr()
            }
        }

        edtKlikIklan.doAfterTextChanged {
            it?.let { text ->
                totalAdClick = if (text.isNotEmpty()) text.toString().toInt() else 0
                calculateCtr()
            }
        }

        edtKlikKontak.doAfterTextChanged {
            it?.let { text ->
                totalContactClick = if (text.isNotEmpty()) text.toString().toInt() else 0
                calculateRatioLP()
            }
        }

        edtJumlahVisitor.doAfterTextChanged {
            it?.let { text ->
                totalVisitor = if (text.isNotEmpty()) text.toString().toInt() else 0
                calculateRatioLP()
            }
        }

        edtLeadsCS.doAfterTextChanged {
            it?.let { text ->
                totalLeadsCs = if (text.isNotEmpty()) text.toString().toInt() else 0
                calculateCpr()
            }
        }

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

    private fun showInfo(@StringRes info: Int) {
        MaterialDialog(this).show {
            title(text = "Informasi")
            message(info)
            positiveButton(text = "TUTUP")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_PICK_PARTNER && resultCode == Activity.RESULT_OK) {
            partnerSelected = data?.getParcelableExtra<Partner>(PARTNER_DATA_KEY)
            edtPilihPartner.setText(partnerSelected?.fullName)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun formValidation(): Boolean {
        var result = true

        if (partnerSelected == null) {
            result = false
            edtPilihPartner.error = getString(R.string.general_empty_message)
        } else {
            edtPilihPartner.error = null
        }

        if (platformSelected == 0) {
            result = false
            createAlertError(this, "Peringatan", "Pilih platform terlebih dahulu")
        }

        if (edtJumlahTayangan.text.toString().isEmpty()) {
            result = false
            edtJumlahTayangan.error = getString(R.string.general_empty_message)
        } else {
            edtJumlahTayangan.error = null
        }

        if (edtKlikIklan.text.toString().isEmpty()) {
            result = false
            edtKlikIklan.error = getString(R.string.general_empty_message)
        } else {
            edtKlikIklan.error = null
        }

        if (edtJumlahVisitor.text.toString().isEmpty()) {
            result = false
            edtJumlahVisitor.error = getString(R.string.general_empty_message)
        } else {
            edtJumlahVisitor.error = null
        }

        if (edtKlikKontak.text.toString().isEmpty()) {
            result = false
            edtKlikKontak.error = getString(R.string.general_empty_message)
        } else {
            edtKlikKontak.error = null
        }

        if (edtLeadsCS.text.toString().isEmpty()) {
            result = false
            edtLeadsCS.error = getString(R.string.general_empty_message)
        } else {
            edtLeadsCS.error = null
        }

        if (edtBiayaIklan.text.toString().isEmpty()) {
            result = false
            edtBiayaIklan.error = getString(R.string.general_empty_message)
        } else {
            edtBiayaIklan.error = null
        }

        if (edtCtrLink.text.toString().isEmpty()) {
            result = false
            edtCtrLink.error = getString(R.string.general_empty_message)
        } else {
            edtCtrLink.error = null
        }

        if (edtRasioLP.text.toString().isEmpty()) {
            result = false
            edtRasioLP.error = getString(R.string.general_empty_message)
        } else {
            edtRasioLP.error = null
        }

        if (edtCPR.text.toString().isEmpty()) {
            result = false
            edtCPR.error = getString(R.string.general_empty_message)
        } else {
            edtCPR.error = null
        }

        return result

    }
}