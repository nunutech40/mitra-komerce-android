package id.android.kmabsensi.presentation.sdm.device

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.list.listItems
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.github.ajalt.timberkt.d
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.presentation.viewmodels.DeviceViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_add_device.*
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

enum class DOK_IMAGE {
    DOK_IMAGE_1, DOK_IMAGE_2, DOK_IMAGE_3
}

class AddDeviceActivity : BaseActivity() {

    private val deviceVM: DeviceViewModel by viewModel()
    private val sdmVM: PartnerViewModel by viewModel()

    private var files1: File? = null
    private var files2: File? = null
    private var files3: File? = null
    private var DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_1

    private val PICK_PARTNER_RC = 100
    private var partnerSelected: SimplePartner? = null
    private var sdm: List<User> = listOf()
    private var sdmSelected: User? = null
    private var dateSelected: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        setupToolbar("Tambah Device")

        edtJenis.setOnClickListener {
            showDialogJenisDevice()
        }

        edtPilihPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(
                PICK_PARTNER_RC
            )
        }

        edtPilihSDM.setOnClickListener {
            showDialogSdm()
        }

        edtTanggalDiterima.setOnClickListener {
            showDatePicker()
        }

        buttonAddDevice.setOnClickListener {
            showDialogSuccess()
        }

        imageDokumentasi1.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_1
            openImagePicker()
        }

        imageDokumentasi2.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_2
            openImagePicker()
        }

        imageDokumentasi3.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_3
            openImagePicker()
        }

        observeSdm()

        buttonAddDevice.setOnClickListener {
            if (!validationForm()){
                return@setOnClickListener
            }

            deviceVM.addDevice(
                edtJenis.text.toString(),
                edtMerek.text.toString(),
                edtSpesifikasi.text.toString(),
                partnerSelected?.noPartner.toString(),
                sdmSelected?.id.toString(),
                getDateString(dateSelected ?: Calendar.getInstance().time),
                files1,
                files2,
                files3
            )
        }

        observeResult()
    }

    private fun observeSdm(){
        sdmVM.sdmByPartner.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    sdm = state.data.data
                    if (sdm.isNotEmpty()) {
                        edtPilihSDM.setText(sdm[0].full_name)
                        sdmSelected = sdm[0]
                    }
                }
                is UiState.Error -> {

                }
            }
        })
    }

    private fun observeResult(){
        deviceVM.crudResult.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    files1?.delete()
                    files2?.delete()
                    files3?.delete()
                    if (state.data.status){
                        val intent = Intent()
                        intent.putExtra(MESSAGE_CRUD, state.data.message)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Failed", state.data.message)
                    }

                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    private fun openImagePicker() {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .folderMode(true)
            .toolbarFolderTitle("Folder")
            .toolbarImageTitle("ketuk untuk memilih")
            .toolbarArrowColor(Color.WHITE)
            .single()
            .theme(R.style.ImagePickerTheme)
            .enableLog(true)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)
            val imageSelectedPath = compressCustomerCaptureImage(this, image.path)

            fun loadImage(view: ImageView){
                view.loadImageFromUrl(imageSelectedPath.toString())
            }

            when(DOK_IMAGE_SELECTED){
                DOK_IMAGE.DOK_IMAGE_1 -> {
                    loadImage(imageDokumentasi1)
                    files1 = File(imageSelectedPath)
                    btnCancelDokumentasi1.visible()
                    btnCancelDokumentasi1.setOnClickListener {
                        files1 = null
                        btnCancelDokumentasi1.gone()
                        imageDokumentasi1.setImageResource(R.drawable.image_placeholder)
                    }
                }
                DOK_IMAGE.DOK_IMAGE_2 -> {
                    loadImage(imageDokumentasi2)
                    files2 = File(imageSelectedPath)
                    btnCancelDokumentasi2.visible()
                    btnCancelDokumentasi2.setOnClickListener {
                        files3 = null
                        btnCancelDokumentasi2.gone()
                        imageDokumentasi2.setImageResource(R.drawable.image_placeholder)
                    }
                }
                DOK_IMAGE.DOK_IMAGE_3 -> {
                    loadImage(imageDokumentasi3)
                    files3 = File(imageSelectedPath)
                    btnCancelDokumentasi3.visible()
                    btnCancelDokumentasi3.setOnClickListener {
                        files3 = null
                        btnCancelDokumentasi3.gone()
                        imageDokumentasi3.setImageResource(R.drawable.image_placeholder)
                    }
                }
            }

        }

        if (requestCode == PICK_PARTNER_RC && resultCode == Activity.RESULT_OK) {
            partnerSelected = data?.getParcelableExtra<SimplePartner>(SIMPLE_PARTNER_DATA_KEY)
            edtPilihPartner.error = null
            edtPilihPartner.setText(partnerSelected?.fullName)

            sdmVM.getSdmByPartner(partnerSelected!!.noPartner.toInt())
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDialogJenisDevice() {
        MaterialDialog(this).show {
            title(text = "Pilih Jenis Device")
            listItems(R.array.device_type) { dialog, index, text ->
                d { text.toString() }
                setJenis(text.toString())
            }
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

    private fun showDialogSuccess() {
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

    private fun showDialogSdm(){
        val sdmName = mutableListOf<String>()
        sdm.forEach {
            sdmName.add(it.full_name)
        }

        MaterialDialog(this).show {
            title(text = "Pilih SDM")
            listItems(items = sdmName) { dialog, index, text ->
                sdmSelected = sdm[index]
                setSdm()
            }
        }
    }

    private fun setDateText(dateString: String) {
        edtTanggalDiterima.setText(dateString)
        edtTanggalDiterima.error = null
    }

    private fun setJenis(text: String){
        edtJenis.setText(text)
        edtJenis.error = null
    }

    private fun setSdm(){
        edtPilihSDM.setText(sdmSelected!!.full_name)
        edtPilihSDM.error = null
    }

    private fun validationForm(): Boolean  {
        val jenis = ValidationForm.validationInput(edtJenis, "Pilih jenis terlebih dahulu")
        val merek = ValidationForm.validationInput(edtMerek, "Input merek terlebih dahulu")
        val spesifikasi = ValidationForm.validationInput(edtJenis, "Input spesifikasi terlebih dahulu")
        val noPartner = ValidationForm.validationInput(edtPilihPartner, "Pilih partner terlebih dahulu")
        val sdm = ValidationForm.validationInput(edtPilihSDM, "Pilih sdm terlebih dahulu")
        val date = ValidationForm.validationInput(edtTanggalDiterima, "Pilih tangga diterima terlebih dahulu")

        return jenis && merek && spesifikasi && noPartner && sdm && date
    }
}