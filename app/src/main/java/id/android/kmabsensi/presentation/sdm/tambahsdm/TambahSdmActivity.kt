package id.android.kmabsensi.presentation.sdm.tambahsdm

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
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.data.remote.response.Position
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tambah_sdm.*
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TambahSdmActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by inject()

    var imagePath: String? = ""

    var role = mutableListOf("Management", "SDM")

    var offices = mutableListOf<Office>()
    var userManagements = mutableListOf<User>()
    var jabatans = mutableListOf<Position>()

    var roleSelectedId = 0
    var genderSelectedId = 0
    var divisiSelectedId = 0
    var jabatanSelectedId = 0
    var userManagementId = 0
    var officeId = 0

    var isManagement = false


    private lateinit var myDialog: MyDialog

    private val disposables = CompositeDisposable()

    private var compressedImage: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_sdm)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Tambah SDM"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)
        userManagementId = intent.getIntExtra(USER_ID_KEY, 0)


        myDialog = MyDialog(this)
        initViews()
        observeData()

        vm.getDataOffice()
        vm.getUserManagement(2)
        vm.getPositions()

        btnSimpan.setOnClickListener {

            if (validation()) {
                vm.tambahSdm(
                    edtUsername.text.toString(),
                    edtEmail.text.toString(),
                    edtPassword.text.toString(),
                    edtKonfirmasiPassword.text.toString(),
                    roleSelectedId.toString(),
                    edtNamaLengkap.text.toString(),
                    "0",
                    divisiSelectedId.toString(),
                    officeId.toString(),
                    jabatanSelectedId.toString(),
                    edtNoPartner.text.toString(),
                    edtTempatLahir.text.toString(),
                    edtNoHp.text.toString(),
                    edtAddress.text.toString(),
                    edtTanggalLahir.text.toString(),
                    genderSelectedId.toString(),
                    userManagementId.toString(),
                    compressedImage
                )
            }

        }


    }

    fun initViews() {
        

        // spinner divisi
        ArrayAdapter.createFromResource(
            this,
            R.array.division,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDivisi.adapter = adapter

            spinnerDivisi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    divisiSelectedId = position + 1
                }

            }
        }

        //spinner jenis kelamin
        ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerJenisKelamin.adapter = adapter

                spinnerJenisKelamin.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            genderSelectedId = position + 1
                        }

                    }
            }

        //spinner role
        if (isManagement) role.removeAt(0)
        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, role).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRole.adapter = adapter

            spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    roleSelectedId = if (isManagement) position + 3 else position + 2
                    if (position == 0) {
                        if (!isManagement) userManagementId = 0
                        layout_spinner_management.gone()
                    } else {
                        layout_spinner_management.visible()
                    }
                }

            }
        }

        imgProfile.setOnClickListener {
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


        edtTanggalLahir.setOnClickListener {
            MaterialDialog(this).show {
                datePicker { dialog, date ->

                    // Use date (Calendar)

                    dialog.dismiss()

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateSelected: String = dateFormat.format(date.time)
//                    toast(dateSelected)
                    setDateToView(dateSelected)
                }
            }
        }
    }

    fun setDateToView(date: String) {
        edtTanggalLahir.setText(date)
    }

    fun observeData() {
        vm.officeData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    offices.addAll(it.data.data)

                    val officeNames = mutableListOf<String>()
                    offices.forEach { officeNames.add(it.office_name) }
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        officeNames
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerKantorCabang.adapter = adapter

                        spinnerKantorCabang.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    officeId = offices[position].id
                                }

                            }
                    }

                }
                is UiState.Error -> {
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.userManagementData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    userManagements.addAll(it.data.data)

                    val userManagementNames = mutableListOf<String>()
                    userManagements.forEach { userManagementNames.add(it.full_name) }
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        userManagementNames
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerManagement.adapter = adapter

                        spinnerManagement.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    userManagementId = userManagements[position].id
                                }

                            }
                    }
                }
                is UiState.Error -> {
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.crudResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        val intent = Intent()
                        intent.putExtra("message", it.data.message)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Gagal", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.positionResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    
                }
                is UiState.Success -> {
                    jabatans.addAll(it.data.data)
                    val jabatanNames = mutableListOf<String>()
                    jabatans.forEach {
                        jabatanNames.add(it.position_name)
                    }
                    // spinner jabatan
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        jabatanNames
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerJabatan.adapter = adapter

                        spinnerJabatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                jabatanSelectedId = jabatans[position].id
                            }

                        }
                    }
                }
                is UiState.Error -> {
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)


            imagePath = image.path

            compress(File(imagePath))
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun compress(file: File) {
        disposables.add(
            Compressor(this)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                .compressToFileAsFlowable(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    compressedImage = it

                    Glide.with(this)
                        .load(compressedImage)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgProfile)

                }) { e { it.message.toString() } })
    }

    fun validation(): Boolean {
        val username = ValidationForm.validationInput(edtUsername, "Username tidak boleh kosong")
        val password = ValidationForm.validationInput(edtPassword, "Password tidak boleh kosong")
        val konfirmasiPassword = ValidationForm.validationInput(
            edtKonfirmasiPassword,
            "Konfirmasi password tidak boleh kosong"
        )
//        val nip = ValidationForm.validationInput(edtNip, "Username tidak boleh kosong")
        val namaLengkap =
            ValidationForm.validationInput(edtNamaLengkap, "Nama lengkap tidak boleh kosong")
        val tanggalLahir =
            ValidationForm.validationInput(edtTanggalLahir, "Tanggal lahir tidak boleh kosong")
        val tempatLahir =
            ValidationForm.validationInput(edtTempatLahir, "Tempat lahir tidak boleh kosong")
        val noHp = ValidationForm.validationInput(edtNoHp, "No hp tidak boleh kosong")
        val validEmail = ValidationForm.validationEmail(edtEmail, "Email tidak valid")
        val email = ValidationForm.validationInput(edtEmail, "Email tidak boleh kosong")
        val noPartner =
            ValidationForm.validationInput(edtNoPartner, "No partner tidak boleh kosong")
        val alamat = ValidationForm.validationInput(edtAddress, "alamat tidak boleh kosong")

        val matchPass = ValidationForm.validationSingkronPassword(
            edtPassword,
            edtKonfirmasiPassword,
            "Password tidak sama"
        )

        return username && password && konfirmasiPassword && namaLengkap && tanggalLahir &&
                tempatLahir && noHp && email && noPartner && alamat && validEmail && matchPass
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

}
