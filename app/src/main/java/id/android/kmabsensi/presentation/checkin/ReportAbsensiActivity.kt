package id.android.kmabsensi.presentation.checkin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.timePicker
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createAlertError
import id.android.kmabsensi.utils.createAlertSuccess
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_report_absensi.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class ReportAbsensiActivity : BaseActivity() {

    private val vm: CheckinViewModel by viewModel()

    private var dateFormattedSelected = ""

//    private lateinit var timeSelected: Date

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_absensi)

        myDialog = MyDialog(this)

        setToolbarTitle("Report Absensi")

        val date = Calendar.getInstance()
        val dateFormatInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormattedSelected = dateFormatInput.format(date.time)

//        edtTanggal.setOnClickListener {
//            MaterialDialog(this).show {
//                datePicker { dialog, date ->
//                    // Use date (Calendar)
//                    dialog.dismiss()
//
//                    dateSelected = date.time
//
//                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
//                    val dateFormatInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//                    dateFormattedSelected = dateFormatInput.format(date.time)
//                    val dateSelected: String = dateFormat.format(date.time)
//                    setDateUi(dateSelected)
//                }
//            }
//        }
//
//        edtTime.setOnClickListener {
//            MaterialDialog(this).show {
//                timePicker { dialog, datetime ->
//                    // Use date (Calendar)
//                    dialog.dismiss()
//
//                    timeSelected = datetime.time
//
//                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//                    val dateSelected: String = dateFormat.format(datetime.time)
//                    setTimeUi(dateSelected)
//                }
//            }
//        }

        vm.reportAbsenResponse.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
//                        createAlertSuccess(this, it.data.message)
////                        edtTanggal.setText("")
////                        edtTime.setText("")
//                        edtDeskripsi.setText("")
                        startActivity(
                            intentFor<HomeActivity>(
                                "hasReportPresence" to true,
                                "message" to it.data.message
                            ).clearTask().newTask()
                        )
                    } else {
                        createAlertError(this, "Failed", it.data.message)
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        btnSubmit.setOnClickListener {
            if (edtDeskripsi.text.isEmpty()){
                edtDeskripsi.error = "Deskripsi tidak boleh kosong."
                return@setOnClickListener
            } else {
                val user = vm.getUserData()
                vm.reportAbsen(user.id, dateFormattedSelected, edtDeskripsi.text.toString())
            }

        }

        edtDeskripsi.requestFocus()

    }

//    private fun setDateUi(dateSelected: String) {
//        edtTanggal.setText(dateSelected)
//    }
//
//    private fun setTimeUi(timeSelected: String) {
//        edtTime.setText(timeSelected)
//    }
}
