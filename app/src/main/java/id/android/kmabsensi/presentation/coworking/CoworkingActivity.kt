package id.android.kmabsensi.presentation.coworking

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_coworking.*
import java.text.SimpleDateFormat
import java.util.*

class CoworkingActivity : BaseActivity() {

    lateinit var dateFrom: Date
    private var statusSelected = 1
    private var jumlahKursi = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coworking)

        setToolbarTitle("Tambahkan List")

        viewListener()

    }

    private fun viewListener() {

        btnPlus.setOnClickListener {
            jumlahKursi += 1
            txtJumlahKursi.text = "$jumlahKursi"
        }

        btnMinus.setOnClickListener {
            if (jumlahKursi > 1) jumlahKursi -= 1
            txtJumlahKursi.text = "$jumlahKursi"
        }

        edtTanggal.setOnClickListener {
            MaterialDialog(this).show {
                datePicker { dialog, date ->

                    // Use date (Calendar)

                    dialog.dismiss()

                    dateFrom = date.time

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val dateSelected: String = dateFormat.format(date.time)
                    setDateUi(dateSelected)
                }
            }
        }

        // spinner status
        ArrayAdapter.createFromResource(
            this,
            R.array.status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter = adapter

            spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    statusSelected = position + 1
                }

            }
        }
    }

    private fun setDateUi(dateFrom: String) {
        edtTanggal.setText(dateFrom)
    }
}
