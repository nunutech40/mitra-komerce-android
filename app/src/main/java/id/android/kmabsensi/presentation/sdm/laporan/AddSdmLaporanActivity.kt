package id.android.kmabsensi.presentation.sdm.laporan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.AddSdmReportParams
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createAlertError
import id.android.kmabsensi.utils.getDateString
import id.android.kmabsensi.utils.getDateStringFormatted
import kotlinx.android.synthetic.main.activity_add_sdm_laporan.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddSdmLaporanActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()

    private val cal = Calendar.getInstance()
    private var dateSelected: Date? = null
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sdm_laporan)
        setupToolbar(getString(R.string.tambah_laporan_title))
        user = sdmVM.getUserData()
        initViews()

        sdmVM.crudResponse.observe(this, androidx.lifecycle.Observer {state ->
        when(state) {
            is UiState.Loading -> {
                showDialog()
            }
            is UiState.Success -> {
                hideDialog()
                if (state.data.status){
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
            if (!formValidation()){
                return@setOnClickListener
            }

            sdmVM.addSdmReport(
                AddSdmReportParams(
                    user.id,
                    getDateString(dateSelected!!),
                    edtLeads.text.toString().toInt(),
                    edtTransaksi.text.toString().toInt(),
                    edtOrder.text.toString().toInt(),
                    conversion_rate = edtRatingKonversi.text.toString().replace("%","").toDouble() / 100,
                    order_rate = edtRatingOrder.text.toString().replace("%","").toDouble() / 100,
                    notes = edtCatatan.text.toString()
                )
            )
        }
    }

    private fun initViews(){
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
        if (edtLeads.text.toString().isEmpty()){
            edtLeads.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtLeads.error = null
        }

        if (edtTransaksi.text.toString().isEmpty()){
            edtTransaksi.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtTransaksi.error = null
        }

        if (edtOrder.text.toString().isEmpty()){
            edtOrder.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtOrder.error = null
        }

        if (edtRatingKonversi.text.toString().isEmpty()){
            edtRatingKonversi.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtRatingKonversi.error = null
        }

        if (edtRatingOrder.text.toString().isEmpty()){
            edtRatingOrder.error = "Kolom tidak boleh kosong."
            result = false
        } else {
            edtRatingOrder.error = null
        }

        return result
    }
}