package id.android.kmabsensi.presentation.sdm.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.google.android.material.button.MaterialButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordActivity
import id.android.kmabsensi.presentation.sdm.tambahsdm.PartnerSelectedItem
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_karyawan.*
import kotlinx.android.synthetic.main.activity_detail_karyawan.btnSimpan
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtAddress
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtAsalDesa
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtEmail
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNamaBank
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNamaLengkap
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNoHp
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNoPartner
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNoRekening
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtPemilikRekening
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtTanggalBergabung
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtTanggalLahir
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtUsername
import kotlinx.android.synthetic.main.activity_detail_karyawan.imgProfile
import kotlinx.android.synthetic.main.activity_detail_karyawan.layout_spinner_management
import kotlinx.android.synthetic.main.activity_detail_karyawan.rvPartner
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerDivisi
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerJabatan
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerJenisKelamin
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerKantorCabang
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerManagement
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerRole
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerStatusPernikahan
import kotlinx.android.synthetic.main.activity_detail_karyawan.toolbar
import kotlinx.android.synthetic.main.activity_tambah_sdm.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DetailKaryawanActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by inject()

    private lateinit var karyawan: User

    val roles = mutableListOf<String>("Management", "SDM")
    var offices = mutableListOf<Office>()
    var userManagements = mutableListOf<User>()
    var jabatans = mutableListOf<Position>()

    private lateinit var myDialog: MyDialog
    var deleteMode = false

    var roleSelectedId = 0
    var divisionSelectedId = 0
    var officeSelectedId = 0
    var positionSelectedId = 0
    var genderSelectedId = 0
    var userManagementSelectedId = 0
    var martialStatus = 0
    var statusKaryawan = 1
    var bankAccountId = 0

    var imagePath : String? = null

    var isManagement = false

    private val disposables = CompositeDisposable()

    private var compressedImage : File? = null

    private val PICK_PARTNER_RC = 112

    private val groupAdpter = GroupAdapter<GroupieViewHolder>()
    val partnerSelected = mutableListOf<Partner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_karyawan)
        disableAutofill()

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        setupToolbar("Detail SDM")
        initRv()

        myDialog = MyDialog(this)
        karyawan = intent.getParcelableExtra(USER_KEY)!!
        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)
        userManagementSelectedId = karyawan.user_management_id

        initSpinners()
        setDataToView(karyawan)
        observeData()
        disableViews(false)

        vm.getDataOffice()
        vm.getUserManagement(2)
        vm.getPositions()

        divisionSelectedId = karyawan.division_id

        btnSimpan.setOnClickListener {
            vm.updateKaryawan(
                karyawan.id.toString(),
                edtUsername.text.toString(),
                edtEmail.text.toString(),
                roleSelectedId.toString(),
                edtNamaLengkap.text.toString(),
                divisionSelectedId.toString(),
                officeSelectedId.toString(),
                positionSelectedId.toString(),
                if (partnerSelected.isEmpty()) "0" else partnerSelected.joinToString(separator = "|", transform = { it.partnerDetail.noPartner }),
                edtAsalDesa.text.toString(),
                edtNoHp.text.toString(),
                edtAddress.text.toString(),
                edtTanggalLahir.text.toString(),
                genderSelectedId.toString(),
                userManagementSelectedId.toString(),
                statusKaryawan,
                compressedImage,
                edtTanggalBergabung.text.toString(),
                martialStatus.toString(),
                bankAccountId.toString(),
                edtNamaBank.text.toString(),
                edtNoRekening.text.toString(),
                edtPemilikRekening.text.toString()
                )
        }

        switchStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                statusKaryawan = 1
                switchStatus.text = "SDM Aktif"
            } else {
                dialogStatusConfirmation()
            }
        }
    }

    private fun dialogStatusConfirmation(){
        val dialog = MaterialDialog(this).
                customView(R.layout.dialog_confirm_on_off_sdm, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val btnYa = customView.findViewById<MaterialButton>(R.id.btnYa)
        val btnBatal = customView.findViewById<MaterialButton>(R.id.btnBatal)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnYa.setOnClickListener {
            statusKaryawan = 0
            switchStatus.text = "Non Job"
            dialog.dismiss()
        }

        btnBatal.setOnClickListener {
            dialog.dismiss()
            switchStatus.isChecked = !switchStatus.isChecked
        }

        dialog.show()
    }

    private fun initRv(){
        rvPartner.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = groupAdpter
        }
    }

    fun setDataToView(data: User){
        edtUsername.setText(data.username)
        edtTanggalLahir.setText(data.birth_date)
        edtAddress.setText(data.address)
        edtEmail.setText(data.email)
        edtNamaLengkap.setText(data.full_name)
        edtNoHp.setText(data.no_hp)
        edtNoPartner.setText(if (data.no_partners.isNotEmpty()) data.no_partners[0] else "")
        edtAsalDesa.setText(data.origin_village)
        edtTanggalBergabung.setText(data.join_date)

        data.partner_assignments.forEach {
            partnerSelected.add(Partner(id = it.id, fullName = it.fullName, partnerDetail = PartnerDetail(noPartner = it.noPartner)))
        }
        populatePartnerSelected()

        data.bank_accounts?.let {bank_account ->
            if (bank_account.isNotEmpty()){
                bankAccountId = bank_account[0].id
                edtNamaBank.setText(bank_account[0].bankName)
                edtNoRekening.setText(bank_account[0].bankNo)
                edtPemilikRekening.setText(bank_account[0].bankOwnerName)
            }
        }

        switchStatus.isChecked = data.status == 1
        switchStatus.text = if (data.status == 1) "SDM Aktif" else "Non Job"

        data.photo_profile_url?.let {
            d { it }
            imgProfile.loadCircleImage(it)
        }

        spinnerStatusPernikahan.setSelection(data.martial_status)
        spinnerJenisKelamin.setSelection(data.gender-1)
        spinnerJabatan.setSelection(data.position_id-1)
        spinnerDivisi.setSelection(data.division_id-1)
        if (!isManagement) spinnerRole.setSelection(data.role_id-2)

        if (karyawan.role_id == 3){
            layout_spinner_management.visible()
        } else {
            layout_spinner_management.gone()
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
                    setDateToView(dateSelected)
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

        edtNoPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(PICK_PARTNER_RC)
        }

    }

    private fun populatePartnerSelected(enableClose : Boolean = true){
        if(partnerSelected.isNotEmpty()) edtNoPartner.setHint("") else edtNoPartner.setHint("Pilih Partner")
        groupAdpter.clear()
        partnerSelected.forEach {
            groupAdpter.add(PartnerSelectedItem(it, enableClose){
                partnerSelected.removeAt(partnerSelected.indexOf(it))
                populatePartnerSelected(enableClose)
            })
        }
    }

    fun setDateToView(date: String) {
        edtTanggalLahir.setText(date)
    }

    private fun setJoinDate(date: String) {
        edtTanggalBergabung.setText(date)
    }

    fun disableViews(enabled: Boolean){

        imgProfile.isEnabled = enabled

        edtUsername.isEnabled = enabled
        edtTanggalLahir.isEnabled = enabled
        edtAddress.isEnabled = enabled
        edtEmail.isEnabled = enabled
        edtNamaLengkap.isEnabled = enabled
        edtNoHp.isEnabled = enabled
        edtNoPartner.isEnabled = enabled
        edtAsalDesa.isEnabled = enabled
        edtTanggalBergabung.isEnabled = enabled
        edtNamaBank.isEnabled = enabled
        edtNoRekening.isEnabled = enabled
        edtPemilikRekening.isEnabled = enabled

        spinnerJenisKelamin.isEnabled = enabled
        spinnerKantorCabang.isEnabled = enabled
        spinnerDivisi.isEnabled = enabled
        spinnerJabatan.isEnabled = enabled
        spinnerRole.isEnabled = enabled
        spinnerManagement.isEnabled = enabled
        spinnerStatusPernikahan.isEnabled = enabled

        populatePartnerSelected(enabled)
    }

    fun initSpinners(){

//        // spinner jabatan / position
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.position,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinnerJabatan.adapter = adapter
//
//            spinnerJabatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                }
//
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    positionSelectedId = position + 1
//                }
//
//            }
//        }

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
                    divisionSelectedId = position + 1
                }

            }
        }

        //spinner jenis kelamin
        ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerJenisKelamin.adapter = adapter

                spinnerJenisKelamin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        //spinner role
        if (isManagement) roles.removeAt(0)
        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles).also { adapter ->
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
                    roleSelectedId = if (isManagement) position+3 else position+2
                    if (position == 0){
                        if (!isManagement) userManagementSelectedId = 0
                        layout_spinner_management.gone()
                    } else {
                        layout_spinner_management.visible()
                    }
                }

            }
        }
    }

    fun observeData(){
        vm.officeData.observe(this, Observer {
            when(it){
                is UiState.Loading -> {}
                is UiState.Success -> {
                    offices.addAll(it.data.data)

                    val officeNames = mutableListOf<String>()
                    offices.forEach { officeNames.add(it.office_name) }
                    ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, officeNames).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerKantorCabang.adapter = adapter

                        spinnerKantorCabang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                officeSelectedId = offices[position].id
                            }

                        }

                        spinnerKantorCabang.setSelection(offices.indexOfFirst { it.id == karyawan.office_id })
                    }

                }
                is UiState.Error -> {
                    Timber.e { it.throwable.message.toString() }
                }
            }
        })

        vm.userManagementData.observe(this, Observer {
            when(it){
                is UiState.Loading -> {}
                is UiState.Success -> {
                    userManagements.addAll(it.data.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })

                    val userManagementNames = mutableListOf<String>()
                    userManagements.forEach { userManagementNames.add(it.full_name) }
                    ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userManagementNames).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerManagement.adapter = adapter

                        spinnerManagement.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                userManagementSelectedId = userManagements[position].id
                            }

                        }
                        spinnerManagement.setSelection(userManagements.indexOfFirst { it.id == karyawan.user_management_id })
                    }


                }
                is UiState.Error -> {
                    Timber.e { it.throwable.message.toString() }
                }
            }
        })

        vm.crudResponse.observe(this, Observer {
            when(it){
                is UiState.Loading -> { myDialog.show() }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if(it.data.status){
                        compressedImage?.delete()
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
                    Timber.e { it.throwable.message.toString() }
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
                                positionSelectedId = jabatans[position].id
                            }

                        }
                        spinnerJabatan.setSelection(jabatans.indexOfFirst { it.id == karyawan.position_id })
                    }
                }
                is UiState.Error -> {
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_karyawan, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_chat -> {

                val firstChar = karyawan.no_hp.first().toString()

                val nomorHp = karyawan.no_hp

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
                startActivity<EditPasswordActivity>(USER_ID_KEY to karyawan.id)
            }
            R.id.action_delete -> {

                MaterialDialog(this).show {
                    title(text = "Hapus Karyawan")
                    message(text = "Apakah anda yakin ingin menghapus karyawan ini?")
                    positiveButton(text = "Ya"){
                        deleteMode = true
                        it.dismiss()
                        vm.deleteKaryawan(karyawan.id)
                    }
                    negativeButton(text = "Batal"){
                        it.dismiss()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
            imagePath = image.path
            compress(File(imagePath))
        }

        if (requestCode == PICK_PARTNER_RC && resultCode == Activity.RESULT_OK){
            val partner = data?.getParcelableExtra<Partner>(PARTNER_DATA_KEY)
            partnerSelected.add(partner!!)
            populatePartnerSelected()
//            edtNoPartner.setText(partner?.partnerDetail?.noPartner)
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
                        Environment.DIRECTORY_PICTURES).absolutePath
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

                }) { Timber.e { it.message.toString() } })
    }
}
