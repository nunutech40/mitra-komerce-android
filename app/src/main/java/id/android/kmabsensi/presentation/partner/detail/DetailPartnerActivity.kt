package id.android.kmabsensi.presentation.partner.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.db.entity.City
import id.android.kmabsensi.data.db.entity.Province
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.PartnerCategory
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.kategori.PartnerCategoryViewModel
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_detail_partner.*
import kotlinx.android.synthetic.main.activity_detail_partner.btnBack
import kotlinx.android.synthetic.main.activity_detail_partner.edtAddress
import kotlinx.android.synthetic.main.activity_detail_partner.edtEmail
import kotlinx.android.synthetic.main.activity_detail_partner.edtNamaLengkap
import kotlinx.android.synthetic.main.activity_detail_partner.edtNoHp
import kotlinx.android.synthetic.main.activity_detail_partner.edtNoPartner
import kotlinx.android.synthetic.main.activity_detail_partner.edtTanggalLahir
import kotlinx.android.synthetic.main.activity_detail_partner.edtUsername
import kotlinx.android.synthetic.main.activity_detail_partner.imgProfile
import kotlinx.android.synthetic.main.activity_detail_partner.spinnerJenisKelamin
import kotlinx.android.synthetic.main.activity_detail_partner.toolbar
import kotlinx.android.synthetic.main.activity_detail_partner.txtTitle
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DetailPartnerActivity : BaseActivity() {


    private val partnerViewModel: PartnerViewModel by viewModel()
    private val partnerCategoryViewModel: PartnerCategoryViewModel by viewModel()

    private lateinit var partner: Partner

    private var imageProfileSelectedPath: String? = ""
    private var compressedImagePhotoFile: File? = null

    private var genderSelectedId = 0
    private var partnerStatus = 0
    private var martialStatus = 0
    private var provinceSelected = Province()
    private var citySelected = City()
    private var partnerCategorySelected = PartnerCategory()
    var userManagements = mutableListOf<User>()
    var userManagementId = 0
    var deleteMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_partner)

        setToolbar()

        partner = intent.getParcelableExtra<Partner>(PARTNER_DATA_KEY)

        initViews()
        setDataToView(partner)
        disableViews(false)

        partnerViewModel.getProvinces()
        partnerCategoryViewModel.getPartnerCategories()
        observeCrudResponse()
        observeAreaData()
        observePartnerCategory()
        observeUserManagement()

        btnSimpan.setOnClickListener {
            if (!formValidation()){
                return@setOnClickListener
            }
            partnerViewModel.editPartner(
                id = partner.id.toString(),
                noPartner = edtNoPartner.text.toString(),
                username = edtUsername.text.toString(),
                status = partnerStatus.toString(),
                email = edtEmail.text.toString(),
                fullname = edtNamaLengkap.text.toString(),
                noHp = edtNoHp.text.toString(),
                address = edtAddress.text.toString(),
                photoProfileUrl = compressedImagePhotoFile,
                birthdate = edtTanggalLahir.text.toString(),
                gender = genderSelectedId.toString(),
                joinDate = edtTanggalBergabung.text.toString(),
                martialStatus = this.martialStatus.toString(),
                partnerCategoryId = partnerCategorySelected.id.toString(),
                partnerCategoryName = partnerCategorySelected.partnerCategoryName,
                provinceCode = provinceSelected.kodeWilayah,
                provinceName = provinceSelected.nama,
                cityCode = citySelected.kodeWilayah,
                cityName = citySelected.nama,
                userManagementId = userManagementId.toString()
            )
        }

        switchStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                partnerStatus = 1
                switchStatus.text = "Aktif"
            } else {
                partnerStatus = 0
                switchStatus.text = "Pause"
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        txtTitle.text = "Detail Partner"
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeUserManagement() {
        partnerViewModel.getUserManagement()
        partnerViewModel.userManagements.observe(this, Observer {
                state ->
            when(state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    userManagements.addAll(state.data.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })
                    val userManagementNames = mutableListOf<String>()
                    userManagements.forEach { userManagementNames.add(it.full_name) }
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        userManagementNames
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                        spinnerManagement.setSelection(userManagements.indexOfFirst { it.id == partner.userManagementId })
                    }
                }
                is UiState.Error -> {
                    e(state.throwable)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_partner, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_chat -> {

                val firstChar = partner.noHp.first().toString()

                val nomorHp = partner.noHp

                var validNomorHp = ""

                if (firstChar == "0"){
                    validNomorHp = "+62${nomorHp.substring(1)}"
                } else if(firstChar == "6"){
                    validNomorHp = "+$nomorHp"
                } else if (firstChar == "+"){
                    validNomorHp = nomorHp
                } else {
                    createAlertError(this, "Gagal", "Nomor hp tidak valid.")
                    return false
                }

                openChatWhatsapp(validNomorHp)
            }
            R.id.action_edit -> {
                disableViews(true)
                layoutStatus.visible()
                btnSimpan.visible()
            }
            R.id.action_edit_password -> {
                startActivity<EditPasswordActivity>(USER_ID_KEY to partner.id)
            }
            R.id.action_delete -> {

                MaterialDialog(this).show {
                    title(text = "Hapus Partner")
                    message(text = "Apakah anda yakin ingin menghapus data partner ini?")
                    positiveButton(text = "Ya"){
                        deleteMode = true
                        it.dismiss()
                        partnerViewModel.deletePartner(partner.id)
                    }
                    negativeButton(text = "Batal"){
                        it.dismiss()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
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

        /* spinner martial status */
        ArrayAdapter.createFromResource(
            this,
            R.array.martial_status,
            android.R.layout.simple_spinner_item
        )
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerStatusPernikahan.adapter = adapter

                spinnerStatusPernikahan.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            martialStatus = position
                        }

                    }
            }

        imgProfile.setOnClickListener {
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

        edtTanggalLahir.setOnClickListener {
            MaterialDialog(this).show {
                datePicker { dialog, date ->

                    // Use date (Calendar)

                    dialog.dismiss()

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateSelected: String = dateFormat.format(date.time)
                    setBirthdayDate(dateSelected)
                }
            }
        }

        edtTanggalBergabung.setOnClickListener {
            MaterialDialog(this).show {
                datePicker { dialog, date ->

                    // Use date (Calendar)

                    dialog.dismiss()

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateSelected: String = dateFormat.format(date.time)
                    setJoinDate(dateSelected)
                }
            }
        }
    }

    private fun setBirthdayDate(date: String) {
        edtTanggalLahir.setText(date)
    }

    private fun setJoinDate(date: String) {
        edtTanggalBergabung.setText(date)
    }

    private fun setDataToView(data: Partner) {
        d { data.toString() }

        edtUsername.setText(data.username)
        edtTanggalLahir.setText(data.birthDate)
        edtAddress.setText(data.address)
        edtEmail.setText(data.email)
        edtNamaLengkap.setText(data.fullName)
        edtNoHp.setText(data.noHp)
        edtNoPartner.setText(data.noPartner)
        edtTanggalBergabung.setText(data.joinDate)

        data.photoProfileUrl?.let {
            d { it }
            imgProfile.loadCircleImage(it)
        }

        spinnerJenisKelamin.setSelection(data.gender - 1)
        spinnerStatusPernikahan.setSelection(data.martialStatus)

        switchStatus.isChecked = data.status == 1
        switchStatus.text = if (data.status == 1) "Aktif" else "Pause"
    }

    private fun disableViews(enabled: Boolean){
        imgProfile.isEnabled = enabled

        edtUsername.isEnabled = enabled
        edtTanggalLahir.isEnabled = enabled
        edtAddress.isEnabled = enabled
        edtEmail.isEnabled = enabled
        edtNamaLengkap.isEnabled = enabled
        edtNoHp.isEnabled = enabled
        edtNoPartner.isEnabled = enabled
        edtTanggalBergabung.isEnabled = enabled
        spinnerCategoryPartner.isEnabled = enabled
        spinnerJenisKelamin.isEnabled = enabled
        spinnerStatusPernikahan.isEnabled = enabled
        spinnerProvince.isEnabled = enabled
        spinnerCity.isEnabled = enabled
        spinnerManagement.isEnabled = enabled

        if (!enabled) btnSimpan.gone() else btnSimpan.visible()
    }

    private fun observeCrudResponse() {
        partnerViewModel.crudResponse.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status) {
                        compressedImagePhotoFile?.delete()
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
                }
            }
        })
    }

    private fun observeAreaData() {
        partnerViewModel.provinces.observe(this, Observer { provinces ->

            val provinceNames = mutableListOf<String>()
            provinces.forEach {
                provinceNames.add(it.nama)
            }

            /* spinner provinsi */
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                provinceNames
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerProvince.adapter = adapter

                spinnerProvince.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            provinceSelected = provinces[position]
                            partnerViewModel.getCities(provinces[position].kodeWilayah)
                        }

                    }
                spinnerProvince.setSelection(provinces.indexOfFirst { it.kodeWilayah == partner.partnerDetail.provinceCode })
            }

        })

        partnerViewModel.cities.observe(this, Observer { cities ->
            val cityNames = mutableListOf<String>()
            cities.forEach {
                cityNames.add(it.nama)
            }
            /* spinner city */
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                cityNames
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCity.adapter = adapter

                spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        citySelected = cities[position]
                    }

                }

                spinnerCity.setSelection(cities.indexOfFirst { it.kodeWilayah == partner.partnerDetail.cityCode })
            }
        })
    }

    private fun observePartnerCategory() {
        partnerCategoryViewModel.partnerCategories.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    val categories = mutableListOf<String>()
                    state.data.categories.forEach {
                        categories.add(it.partnerCategoryName)
                    }
                    /* spinner category partner */
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCategoryPartner.adapter = adapter

                        spinnerCategoryPartner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    partnerCategorySelected = state.data.categories[position]
                                }

                            }

                        spinnerCategoryPartner.setSelection(state.data.categories.indexOfFirst { it.id == partner.partnerDetail.partnerCategoryId })

                    }
                }
                is UiState.Error -> {

                }
            }
        })
    }

    private fun openChatWhatsapp(number: String) {
        val url = "https://wa.me/$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)
            imageProfileSelectedPath = compressCustomerCaptureImage(this, image.path)
            compressedImagePhotoFile = File(imageProfileSelectedPath)

            Glide.with(this)
                .load(compressedImagePhotoFile)
                .apply(RequestOptions.circleCropTransform())
                .into(imgProfile)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun formValidation(): Boolean {
        val username = ValidationForm.validationInput(edtUsername, "Username tidak boleh kosong")
        val namaLengkap = ValidationForm.validationInput(edtNamaLengkap, "Nama lengkap tidak boleh kosong")
        val tanggalLahir = ValidationForm.validationInput(edtTanggalLahir, "Tanggal lahir tidak boleh kosong")
        val noHp = ValidationForm.validationInput(edtNoHp, "No hp tidak boleh kosong")
        val validEmail = ValidationForm.validationEmail(edtEmail, "Email tidak valid")
        val email = ValidationForm.validationInput(edtEmail, "Email tidak boleh kosong")
        val noPartner = ValidationForm.validationInput(edtNoPartner, "No partner tidak boleh kosong")
        val alamat = ValidationForm.validationInput(edtAddress, "alamat tidak boleh kosong")
        val tanggalBergabung = ValidationForm.validationInput(edtTanggalBergabung, "Tanggal bergabung tidak boleh kosong")

        return username && namaLengkap && tanggalLahir
                && noHp && email && noPartner && alamat && validEmail
                && tanggalBergabung
    }

}
