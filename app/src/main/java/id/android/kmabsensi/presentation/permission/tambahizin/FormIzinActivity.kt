package id.android.kmabsensi.presentation.permission.tambahizin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.github.ajalt.timberkt.Timber.e
import com.stfalcon.imageviewer.StfalconImageViewer
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tidak_hadir.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FormIzinActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    private lateinit var myDialog: MyDialog

    var imagePathPersetujuanPartner: String? = ""
    var imagePathLaporanLeader: String? = ""

    var isPersetujuanPartner = false

    var permissionType = 0

    private lateinit var user: User

    private var compressedImagePersetujuanPartner: File? = null
    private var compressedImageLaporanLeader: File? = null

    private val disposables = CompositeDisposable()

    lateinit var dateFrom: Date
    lateinit var dateTo: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tidak_hadir)

        myDialog = MyDialog(this)

        setSupportActionBar(toolbar)

        setupToolbar("Form Izin")

        user = intent.getParcelableExtra(USER_KEY)

        if (user.role_id == 2) {
            txt_label_bukti_2.text = "Lampiran Bukti Izin"
            layoutPersetujuanPartner.gone()
            dividerPersetujuanPartner.gone()

        } else if (user.role_id == 3) {
            if (user.division_id == 2) {
                layoutPersetujuanPartner.gone()
                dividerPersetujuanPartner.gone()
            }
        }

        // spinner izin
        ArrayAdapter.createFromResource(
            this,
            R.array.permission,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPermission.adapter = adapter

            spinnerPermission.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    permissionType = position + 1
                }

            }
        }

        vm.createPermissionResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {

                    myDialog.dismiss()
                    if (it.data.status) {
                        compressedImagePersetujuanPartner?.delete()
                        setResult(Activity.RESULT_OK, Intent().putExtra("message", it.data.message))
                        finish()
                    } else {
                        createAlertError(this, "Gagal", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        btnSubmit.setOnClickListener {

            if (user.division_id == 1 && user.role_id == 3) {
                if (compressedImagePersetujuanPartner == null) {
                    createAlertError(this, "Peringatan", "Upload persetujuan partner dahulu.")
                    return@setOnClickListener
                }
            }

            if (validation()) {
                compressedImageLaporanLeader?.let { leader ->
                    vm.createPermission(
                        permissionType,
                        user.id,
                        user.office_id,
                        user.role_id,
                        user.user_management_id,
                        edtDeskripsi.text.toString(),
                        edtDateFrom.text.toString(),
                        edtDateTo.text.toString(),
                        leader,
                        compressedImagePersetujuanPartner?.let { it }
                    )
                } ?: run {

                    if (user.role_id == 2){
                        createAlertError(this, "Peringatan", "Upload dokumen terkait dahulu.")
                    } else {
                        createAlertError(this, "Peringatan", "Upload laporan leader dahulu.")
                    }

                }
            }
        }

        btnUploadPersetujuanPartner.setOnClickListener {
            isPersetujuanPartner = true
            startImagePicker()
        }

        btnUploadLaporanLeader.setOnClickListener {
            isPersetujuanPartner = false
            startImagePicker()
        }

        imgPersetujuanPartner.setOnClickListener {
            StfalconImageViewer.Builder<String>(
                this,
                listOf(compressedImagePersetujuanPartner?.path)
            ) { view, image ->
                Glide.with(this)
                    .load(image).into(view)
            }.show()
        }

        imgLaporanLeader.setOnClickListener {
            StfalconImageViewer.Builder<String>(
                this,
                listOf(compressedImageLaporanLeader?.path)
            ) { view, image ->
                Glide.with(this)
                    .load(image).into(view)
            }.show()
        }

        edtDateFrom.setOnClickListener {
            MaterialDialog(this).show {
                datePicker { dialog, date ->
                    // Use date (Calendar)
                    dialog.dismiss()

                    dateFrom = date.time

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateSelected: String = dateFormat.format(date.time)
                    setDateFrom(dateSelected)
                }
            }
        }

        edtDateTo.setOnClickListener {
            MaterialDialog(this).show {
                datePicker { dialog, date ->

                    // Use date (Calendar)
                    dialog.dismiss()

                    dateTo = date.time
                    if (!dateFrom.before(dateTo)) {
                        createAlertError(
                            this@FormIzinActivity,
                            "Peringatan",
                            "dateTo kurang dari dateFrom",
                            3000
                        )
                    } else {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateSelected: String = dateFormat.format(date.time)
                        setDateTo(dateSelected)
                    }


                }
            }
        }

    }

    private fun startImagePicker() {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .folderMode(true)
            .toolbarFolderTitle("Folder")
            .toolbarImageTitle("Ketuk untuk memilih")
            .toolbarArrowColor(Color.WHITE)
            .single()
            .theme(R.style.ImagePickerTheme)
            .enableLog(true)
            .start()
    }

    private fun setDateFrom(dateFrom: String) {
        edtDateFrom.setText(dateFrom)
    }

    private fun setDateTo(dateTo: String) {
        edtDateTo.setText(dateTo)
    }

    fun validation(): Boolean {

        val dateFrom = ValidationForm.validationInput(edtDateFrom, "Tanggal harus di isi")
        val dateTo = ValidationForm.validationInput(edtDateTo, "Tanggal harus di isi")
        val deskripsi = ValidationForm.validationInput(edtDeskripsi, "Deskripsi harus di isi")

        return dateFrom && dateTo && deskripsi

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)

            if (isPersetujuanPartner) {
                imagePathPersetujuanPartner = image.path
            } else {
                imagePathLaporanLeader = image.path
            }

            compress(File(image.path))

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun compress(file: File) {
        disposables.add(
            Compressor(this)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                .compressToFileAsFlowable(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if (isPersetujuanPartner) {
                        compressedImagePersetujuanPartner = it
                        layoutImgPersetujuanPartner.visible()

                        Glide.with(this)
                            .load(compressedImagePersetujuanPartner)
                            .into(imgPersetujuanPartner)

                    } else {
                        compressedImageLaporanLeader = it
                        layoutImgLaporanLeader.visible()

                        Glide.with(this)
                            .load(compressedImageLaporanLeader)
                            .into(imgLaporanLeader)
                    }

                }) { e { it.message.toString() } })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
