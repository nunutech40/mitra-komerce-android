package id.android.kmabsensi.presentation.partner.tambahpartner

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import id.android.kmabsensi.R
import id.android.kmabsensi.data.db.entity.City
import id.android.kmabsensi.data.db.entity.Province
import id.android.kmabsensi.data.remote.response.PartnerCategory
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.kategori.PartnerCategoryViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
import id.android.kmabsensi.utils.compressCustomerCaptureImage
import id.android.kmabsensi.utils.createAlertError
import kotlinx.android.synthetic.main.activity_form_partner.*
import kotlinx.android.synthetic.main.activity_form_partner.btnSimpan
import kotlinx.android.synthetic.main.activity_form_partner.edtAddress
import kotlinx.android.synthetic.main.activity_form_partner.edtEmail
import kotlinx.android.synthetic.main.activity_form_partner.edtKonfirmasiPassword
import kotlinx.android.synthetic.main.activity_form_partner.edtNamaLengkap
import kotlinx.android.synthetic.main.activity_form_partner.edtNoHp
import kotlinx.android.synthetic.main.activity_form_partner.edtNoPartner
import kotlinx.android.synthetic.main.activity_form_partner.edtPassword
import kotlinx.android.synthetic.main.activity_form_partner.edtTanggalLahir
import kotlinx.android.synthetic.main.activity_form_partner.edtUsername
import kotlinx.android.synthetic.main.activity_form_partner.imgProfile
import kotlinx.android.synthetic.main.activity_form_partner.spinnerJenisKelamin
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class FormPartnerActivity : BaseActivity() {

    private val partnerViewModel: PartnerViewModel by viewModel()
    private val partnerCategoryViewModel: PartnerCategoryViewModel by viewModel()

    private var imageProfileSelectedPath: String? = ""
    private var compressedImagePhotoFile: File? = null

    private var genderSelectedId = 0
    private var partnerStatus = 1 /* default aktif */
    private var martialStatus = 0
    private var provinceSelected = Province()
    private var citySelected = City()
    private var partnerCategorySelected = PartnerCategory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_partner)
        disableAutofill()
        setupToolbar("Tambah Partner")

        initViews()
        viewListener()

        partnerViewModel.getProvinces()
        partnerCategoryViewModel.getPartnerCategories()
        observeAreaData()
        observeCrudResponse()
        observePartnerCategory()

        btnSimpan.setOnClickListener {
            if (!formValidation()){
                return@setOnClickListener
            }
            partnerViewModel.addPartner(
                noPartner = edtNoPartner.text.toString(),
                username = edtUsername.text.toString(),
                status = partnerStatus.toString(),
                email = edtEmail.text.toString(),
                password = edtPassword.text.toString(),
                passwordConfirmation = edtKonfirmasiPassword.text.toString(),
                fullname = edtNamaLengkap.text.toString(),
                noHp = edtNoHp.text.toString(),
                address = edtAddress.text.toString(),
                photoProfileUrl = compressedImagePhotoFile,
                birthdate = edtTanggalLahir.text.toString(),
                gender = genderSelectedId.toString(),
                joinDate = edtTanggalBergabung.text.toString(),
                martialStatus = this.martialStatus.toString(),
                bankName = edtNamaBank.text.toString(),
                bankNo = edtNoRekening.text.toString(),
                bankOwnerName = edtPemilikRekening.text.toString(),
                partnerCategoryId = partnerCategorySelected.id.toString(),
                partnerCategoryName = partnerCategorySelected.partnerCategoryName,
                provinceCode = provinceSelected.kodeWilayah,
                provinceName = provinceSelected.nama,
                cityCode = citySelected.kodeWilayah,
                cityName = citySelected.nama
            )
        }
    }

    private fun initViews(){
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

        /* spinner partner status */
//        ArrayAdapter.createFromResource(this, R.array.partner_status, android.R.layout.simple_spinner_item)
//            .also { adapter ->
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                spinnerStatusPartner.adapter = adapter
//
//                spinnerStatusPartner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                        }
//
//                        override fun onItemSelected(
//                            parent: AdapterView<*>?,
//                            view: View?,
//                            position: Int,
//                            id: Long
//                        ) {
//                            partnerStatus = position
//                        }
//
//                    }
//            }

        /* spinner martial status */
        ArrayAdapter.createFromResource(this, R.array.martial_status, android.R.layout.simple_spinner_item)
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


    }

    private fun viewListener(){
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
            showDatePicker(true)
        }

        edtTanggalBergabung.setOnClickListener {
            showDatePicker(false)
        }

    }

    private fun showDatePicker(isBirthDate: Boolean){
        MaterialDialog(this).show {
            datePicker { dialog, date ->

                // Use date (Calendar)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateSelected: String = dateFormat.format(date.time)
                if (isBirthDate) setBirthDate(dateSelected) else setJoinDate(dateSelected)
                dialog.dismiss()
            }
        }
    }

    private fun setBirthDate(date: String) {
        edtTanggalLahir.setText(date)
    }

    private fun setJoinDate(date: String){
        edtTanggalBergabung.setText(date)
    }

    private fun observeCrudResponse(){
        partnerViewModel.crudResponse.observe(this, Observer { state ->
        when(state) {
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
        } })
    }

    private fun observeAreaData(){
        partnerViewModel.provinces.observe(this, Observer {

            val provinces = mutableListOf<String>()
            it.forEach {
                provinces.add(it.nama)
            }

            /* spinner provinsi */
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinces).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerProvince.adapter = adapter

                spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        provinceSelected = it[position]
                        partnerViewModel.getCities(it[position].kodeWilayah)
                    }

                }
            }

        })

        partnerViewModel.cities.observe(this, Observer {
            val cities = mutableListOf<String>()
            it.forEach {
                cities.add(it.nama)
            }
            /* spinner city */
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities).also { adapter ->
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
                        citySelected = it[position]
                    }

                }
            }
        })
    }

    private fun observePartnerCategory(){
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
                    ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCategoryPartner.adapter = adapter

                        spinnerCategoryPartner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        val password = ValidationForm.validationInput(edtPassword, "Password tidak boleh kosong")
        val konfirmasiPassword = ValidationForm.validationInput(edtKonfirmasiPassword, "Konfirmasi password tidak boleh kosong")
        val namaLengkap = ValidationForm.validationInput(edtNamaLengkap, "Nama lengkap tidak boleh kosong")
        val tanggalLahir = ValidationForm.validationInput(edtTanggalLahir, "Tanggal lahir tidak boleh kosong")
        val noHp = ValidationForm.validationInput(edtNoHp, "No hp tidak boleh kosong")
        val validEmail = ValidationForm.validationEmail(edtEmail, "Email tidak valid")
        val email = ValidationForm.validationInput(edtEmail, "Email tidak boleh kosong")
        val noPartner = ValidationForm.validationInput(edtNoPartner, "No partner tidak boleh kosong")
        val alamat = ValidationForm.validationInput(edtAddress, "alamat tidak boleh kosong")
        val tanggalBergabung = ValidationForm.validationInput(edtTanggalBergabung, "Tanggal bergabung tidak boleh kosong")
        val namaBank = ValidationForm.validationInput(edtNamaBank, "Nama bank tidak boleh kosong")
        val noRek = ValidationForm.validationInput(edtNoRekening, "Nomor rekening tidak boleh kosong")
        val pemilikRek = ValidationForm.validationInput(edtPemilikRekening, "Pemilik rekening tidak boleh kosong")

        val matchPass = ValidationForm.validationSingkronPassword(
            edtPassword,
            edtKonfirmasiPassword,
            "Password tidak sama"
        )

        return username && password && konfirmasiPassword && namaLengkap && tanggalLahir
                && noHp && email && noPartner && alamat && validEmail && matchPass
                && tanggalBergabung && namaBank && noRek && pemilikRek
    }

}
