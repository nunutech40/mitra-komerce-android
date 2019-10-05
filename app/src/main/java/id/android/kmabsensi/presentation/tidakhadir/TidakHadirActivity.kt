package id.android.kmabsensi.presentation.tidakhadir

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
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.USER_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
import id.android.kmabsensi.utils.loadImageFromUrl
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_karyawan.toolbar
import kotlinx.android.synthetic.main.activity_tidak_hadir.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TidakHadirActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    private lateinit var myDialog: MyDialog

    var imagePath : String? = ""

    var permissionType = 0

    private lateinit var user: User

    private var compressedImage: File? = null

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tidak_hadir)

        myDialog = MyDialog(this)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Form Tidak Hadir"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = intent.getParcelableExtra(USER_KEY)

        // spinner jabatan
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
                    permissionType = position+1
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
                    toast(it.data.message)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        btnSubmit.setOnClickListener {
            compressedImage?.let {
                if (validation()){
                    vm.createPermission(
                        permissionType,
                        user.id,
                        user.office_id,
                        user.role_id,
                        user.user_management_id,
                        1,
                        edtDeskripsi.text.toString(),
                        edtDateFrom.text.toString(),
                        edtDateTo.text.toString(),
                        it
                        )
                }
            } ?: toast("Upload gambar terlebih dahulu.")

        }

        btnUpload.setOnClickListener {
            ImagePicker.create(this)
                .returnMode(ReturnMode.ALL)
                .folderMode(true)
                .toolbarFolderTitle("Images")
                .toolbarImageTitle("Ketuk untuk memilih")
                .toolbarArrowColor(Color.BLACK)
                .single()
                .enableLog(true)
                .start()
        }

        edtDateFrom.setOnClickListener {
            MaterialDialog(this).show {
                datePicker { dialog, date ->

                    // Use date (Calendar)

                    dialog.dismiss()

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

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateSelected: String = dateFormat.format(date.time)
                    setDateTo(dateSelected)
                }
            }
        }

    }

    private fun setDateFrom(dateFrom: String){
        edtDateFrom.setText(dateFrom)
    }

    private fun setDateTo(dateTo: String){
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


            imagePath = image.path

            imagePath?.let {
                imgUpload.loadImageFromUrl(it)
            }

            compress(File(imagePath))

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun compress(file: File) {
        disposables.add(
            Compressor(this)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).absolutePath
                )
                .compressToFileAsFlowable(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    compressedImage = it

                    Glide.with(this)
                        .load(compressedImage)
                        .into(imgUpload)

                }) { e { it.message.toString() }})
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
