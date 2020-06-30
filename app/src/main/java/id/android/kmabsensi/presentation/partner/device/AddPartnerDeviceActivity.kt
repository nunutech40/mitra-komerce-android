package id.android.kmabsensi.presentation.partner.device

import android.os.Bundle
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.list.listItems
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.utils.getDateStringFormatted
import kotlinx.android.synthetic.main.activity_add_partner_device.*
import org.jetbrains.anko.startActivity

class AddPartnerDeviceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_partner_device)
        setupToolbar(getString(R.string.tambah_device_partner))

        edtJenis.setOnClickListener {
            showDialogJenisDevice()
        }

        edtPilihPartner.setOnClickListener {
            startActivity<PartnerPickerActivity>()
        }

        edtTanggalDiterima.setOnClickListener {
            showDatePicker()
        }

        buttonAddDevice.setOnClickListener {
            showDialogSuccess()
        }

    }

    private fun showDialogJenisDevice() {
        MaterialDialog(this).show {
            title(text = "Pilih Jenis Device")
            listItems(R.array.device_type) { dialog, index, text ->
                edtJenis.setText(text)
            }
        }
    }

    private fun showDatePicker() {
        MaterialDialog(this).show {
            datePicker { dialog, date ->
                // Use date (Calendar)
                setDateText(getDateStringFormatted(date.time))
            }
        }

    }

    private fun setDateText(dateString: String) {
        edtTanggalDiterima.setText(dateString)
    }

    private fun showDialogSuccess(){
        val dialog = MaterialDialog(this).show {
            cornerRadius(16f)
            customView(
                R.layout.dialog_success,
                scrollable = false,
                horizontalPadding = true,
                noVerticalPadding = true
            )
        }
        val customView = dialog.getCustomView()
        val close = customView.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dialog.dismiss()
        }
    }
}