package id.android.kmabsensi.presentation.sdm.device

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.list.listItems
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.github.ajalt.timberkt.d
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Device
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
    private var sdmIdSelected: Int = 0
    private var dateSelected: Date? = null

    private var device : Device? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        setupToolbar("Tambah Device")

        device = intent.getParcelableExtra(DEVICE_DATA)
        initView()

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

        imageDokumentasi1.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_1
            openImagePicker()
        }

        btnCancelDokumentasi1.setOnClickListener {
            files1 = null
            btnCancelDokumentasi1.gone()
            imageDokumentasi1.setImageResource(R.drawable.image_placeholder)
        }

        imageDokumentasi2.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_2
            openImagePicker()
        }

        btnCancelDokumentasi2.setOnClickListener {
            files3 = null
            btnCancelDokumentasi2.gone()
            imageDokumentasi2.setImageResource(R.drawable.image_placeholder)
        }

        imageDokumentasi3.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_3
            openImagePicker()
        }

        btnCancelDokumentasi3.setOnClickListener {
            files3 = null
            btnCancelDokumentasi3.gone()
            imageDokumentasi3.setImageResource(R.drawable.image_placeholder)
        }

        buttonAddDevice.setOnClickListener {
            if (!validationForm()){
                return@setOnClickListener
            }
            if (device == null){
                deviceVM.addDevice(
                    edtJenis.text.toString(),
                    edtMerek.text.toString(),
                    edtSpesifikasi.text.toString(),
                    partnerSelected?.noPartner.toString(),
                    sdmIdSelected.toString(),
                    getDateString(dateSelected ?: Calendar.getInstance().time),
                    files1,
                    files2,
                    files3
                )
            } else {
                deviceVM.editDevice(
                    device!!.id.toString(),
                    edtJenis.text.toString(),
                    edtMerek.text.toString(),
                    edtSpesifikasi.text.toString(),
                    partnerSelected?.noPartner.toString(),
                    sdmIdSelected.toString(),
                    getDateString(dateSelected ?: Calendar.getInstance().time),
                    files1,
                    files2,
                    files3
                )
            }
        }

        observeSdm()
        observeResult()
    }

    private fun initView(){
        device?.let {
            setupToolbar("Ubah Device")
            edtJenis.setText(it.deviceType)
            edtMerek.setText(it.brancd)
            edtSpesifikasi.setText(it.spesification)
            partnerSelected = SimplePartner(it.partner.id, it.noPartner, it.partner.fullName)
            sdmVM.getSdmByPartner(partnerSelected!!.noPartner.toInt())
            sdmIdSelected = it.sdm.id
            edtPilihPartner.setText(it.partner.fullName)
            edtPilihSDM.setText(it.sdm.fullName)
            dateSelected = parseStringDate(it.devicePickDate)
            edtTanggalDiterima.setText(getDateStringFormatted2(dateSelected!!))
            it.attachments.forEachIndexed { index, attachment ->
                when(index){
                    0 -> {
                        imageDokumentasi1.loadImageFromUrl(attachment.attachmentUrl)
                        btnCancelDokumentasi1.visible()
                    }
                    1 -> {
                        imageDokumentasi2.loadImageFromUrl(attachment.attachmentUrl)
                        btnCancelDokumentasi2.visible()
                    }
                    2 -> {
                        imageDokumentasi3.loadImageFromUrl(attachment.attachmentUrl)
                        btnCancelDokumentasi3.visible()
                    }
                }
            }
        }
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
                        sdmIdSelected = sdm[0].id
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
                        showDialogSuccess()
//                        val intent = Intent()
//                        intent.putExtra(MESSAGE_CRUD, state.data.message)
//                        setResult(Activity.RESULT_OK, intent)
//                        finish()
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
                }
                DOK_IMAGE.DOK_IMAGE_2 -> {
                    loadImage(imageDokumentasi2)
                    files2 = File(imageSelectedPath)
                    btnCancelDokumentasi2.visible()
                }
                DOK_IMAGE.DOK_IMAGE_3 -> {
                    loadImage(imageDokumentasi3)
                    files3 = File(imageSelectedPath)
                    btnCancelDokumentasi3.visible()
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

        dialog.onDismiss {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
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
                sdmIdSelected = sdm[index].id
                setSdm(sdm[index].full_name)
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

    private fun setSdm(name: String){
        edtPilihSDM.setText(name)
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