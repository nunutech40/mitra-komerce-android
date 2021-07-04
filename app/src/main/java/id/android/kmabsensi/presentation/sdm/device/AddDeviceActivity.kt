package id.android.kmabsensi.presentation.sdm.device

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.presentation.viewmodels.AttachmentViewModel
import id.android.kmabsensi.presentation.viewmodels.DeviceViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_add_device.*
import kotlinx.android.synthetic.main.activity_add_device.edtPilihPartner
import kotlinx.android.synthetic.main.activity_add_invoice.*
import kotlinx.android.synthetic.main.activity_add_laporan_advertiser.*
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
    private val attachmentVM: AttachmentViewModel by viewModel()

    private var files1: File? = null
    private var files2: File? = null
    private var files3: File? = null
    private var DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_1

    private val PICK_PARTNER_RC = 100
    private var partnerSelected: Partner? = null
    private var sdm: List<User> = listOf()
    private var sdmIdSelected: Int = 0
    private var dateSelected: Date? = null

    private var device : Device? = null

    private val deviceOwner = listOf("SDM", "IT Support")
    private var deviceOwnerSelected = 1

    private var hasChangePartner = false

    private var partners = mutableListOf<Partner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        setupToolbar("Tambah Device")
        observePartner()

        device = intent.getParcelableExtra(DEVICE_DATA)
        initSpinner()
        initView()

        edtJenis.setOnClickListener {
            showDialogJenisDevice()
        }

        edtPilihPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(
                PICK_PARTNER_RC,
                    "listPartner" to partners
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
            device?.let {
                showDialogConfirmDelete(this, "Hapus Gambar") {
                    attachmentVM.deleteAttachment(it.attachments[0].id)
                    btnCancelDokumentasi1.gone()
                    imageDokumentasi1.setImageResource(R.drawable.image_placeholder)
                }
            } ?: kotlin.run {
                files1 = null
                btnCancelDokumentasi1.gone()
                imageDokumentasi1.setImageResource(R.drawable.image_placeholder)
            }



        }

        imageDokumentasi2.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_2
            openImagePicker()
        }

        btnCancelDokumentasi2.setOnClickListener {
            device?.let {
                showDialogConfirmDelete(this, "Hapus Gambar") {
                    attachmentVM.deleteAttachment(it.attachments[1].id)
                    btnCancelDokumentasi2.gone()
                    imageDokumentasi2.setImageResource(R.drawable.image_placeholder)
                }
            } ?: kotlin.run {
                files2 = null
                btnCancelDokumentasi2.gone()
                imageDokumentasi2.setImageResource(R.drawable.image_placeholder)
            }

        }

        imageDokumentasi3.setOnClickListener {
            DOK_IMAGE_SELECTED = DOK_IMAGE.DOK_IMAGE_3
            openImagePicker()
        }

        btnCancelDokumentasi3.setOnClickListener {
            device?.let {
                showDialogConfirmDelete(this, "Hapus Gambar") {
                    attachmentVM.deleteAttachment(it.attachments[2].id)
                    btnCancelDokumentasi3.gone()
                    imageDokumentasi3.setImageResource(R.drawable.image_placeholder)
                }
            } ?: kotlin.run {
                files3 = null
                btnCancelDokumentasi3.gone()
                imageDokumentasi3.setImageResource(R.drawable.image_placeholder)
            }

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
                    partnerSelected?.partnerDetail?.noPartner.toString(),
                    sdmIdSelected.toString(),
                    getDateString(dateSelected ?: Calendar.getInstance().time),
                    files1,
                    files2,
                    files3,
                    deviceOwnerSelected
                )
            } else {
                deviceVM.editDevice(
                    device!!.id.toString(),
                    edtJenis.text.toString(),
                    edtMerek.text.toString(),
                    edtSpesifikasi.text.toString(),
                    partnerSelected?.partnerDetail?.noPartner.toString(),
                    sdmIdSelected.toString(),
                    getDateString(dateSelected ?: Calendar.getInstance().time),
                    files1,
                    files2,
                    files3,
                    deviceOwnerSelected
                )
            }
        }

        observeSdm()
        observeResult()
        observeDeleteAttachment()
    }

    private fun initSpinner(){
        ArrayAdapter(this, R.layout.spinner_item, deviceOwner).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOwner.adapter = it
            spinnerOwner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    deviceOwnerSelected = p2 + 1
                    if (deviceOwnerSelected == 2){
                        labelSDM.gone()
                        edtPilihSDM.gone()
                        sdmIdSelected = 0
                    } else {
                        labelSDM.visible()
                        edtPilihSDM.visible()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
        }
    }

    private fun initView(){
        device?.let {
            val isEdit = intent.getBooleanExtra("isEdit", false)
            setupToolbar(if (isEdit) "Ubah Device" else "Detail Device")
            if (!isEdit) {
                buttonAddDevice.gone()
                edtJenis.isEnabled = false
                edtMerek.isEnabled = false
                edtSpesifikasi.isEnabled = false
                edtPilihPartner.isEnabled = false
                edtPilihSDM.isEnabled = false
                edtTanggalDiterima.isEnabled = false
                imageDokumentasi1.isEnabled = false
                imageDokumentasi2.isEnabled = false
                imageDokumentasi3.isEnabled = false
                spinnerOwner.isEnabled = false
            }
            edtJenis.setText(it.deviceType)
            edtMerek.setText(it.brancd)
            edtSpesifikasi.setText(it.spesification)
            val partnerDetail = PartnerDetail(noPartner = it.noPartner)
            partnerSelected = Partner(id = it.partner.id, fullName = it.partner.fullName, partnerDetail = partnerDetail)
            sdmVM.getSdmByPartner(partnerSelected!!.partnerDetail.noPartner.toInt())
            sdmIdSelected = if (it.sdm == null) 0 else it.sdm.id
            edtPilihPartner.setText(it.partner.fullName)
            edtPilihSDM.setText(it.sdm?.fullName)
            dateSelected = parseStringDate(it.devicePickDate)
            edtTanggalDiterima.setText(getDateStringFormatted2(dateSelected!!))
            if (it.deviceOwnerType == 2){ spinnerOwner.setSelection(1) }
            it.attachments.forEachIndexed { index, attachment ->
                when(index){
                    0 -> {
                        imageDokumentasi1.loadImageFromUrl(attachment.attachmentUrl)
                        if (isEdit) btnCancelDokumentasi1.visible()
                    }
                    1 -> {
                        imageDokumentasi2.loadImageFromUrl(attachment.attachmentUrl)
                        if (isEdit) btnCancelDokumentasi2.visible()
                    }
                    2 -> {
                        imageDokumentasi3.loadImageFromUrl(attachment.attachmentUrl)
                        if (isEdit) btnCancelDokumentasi3.visible()
                    }
                }
            }
        }
    }

    private fun observePartner(){
        sdmVM.getDataPartners().observe(this, Observer {
            when(it){
                is UiState.Loading -> {
                    edtPilihPartner.isEnabled = false
                    edtPilihPartner.setHint(getString(R.string.text_loading))
                    Log.d("_Partner", "LOADING")
                }
                is UiState.Success -> {
                    edtPilihPartner.isEnabled = true
                    edtPilihPartner.setHint(getString(R.string.pilih_partner))
                    partners.addAll(it.data.partners)
                }
                is UiState.Error -> Log.d("_Partner", "ERROR ${it.throwable.message}")
            }
        })
    }

    private fun observeSdm(){
        sdmVM.sdmByPartner.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    sdm = state.data.data
                    if (hasChangePartner){
                        if (sdm.isNotEmpty()) {
                            edtPilihSDM.setText(sdm[0].full_name)
                            sdmIdSelected = sdm[0].id
                        } else {
                            edtPilihSDM.setText("")
                            sdmIdSelected = 0
                        }
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

    fun observeDeleteAttachment(){
        attachmentVM.deleteResult.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
            }
            is UiState.Success -> {
                d { state.data.message }
            }
            is UiState.Error -> {
                Timber.e(state.throwable)
            }
        } })
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
            partnerSelected = data?.getParcelableExtra<Partner>(PARTNER_DATA_KEY)
            edtPilihPartner.error = null
            edtPilihPartner.setText(partnerSelected?.fullName)

            hasChangePartner = true
            sdmVM.getSdmByPartner(partnerSelected!!.partnerDetail.noPartner.toInt())
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