package id.android.kmabsensi.presentation.sdm.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.github.ajalt.timberkt.Timber
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.loadCircleImage
import id.android.kmabsensi.utils.ui.MyDialog
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_detail_karyawan.*
import kotlinx.android.synthetic.main.activity_detail_karyawan.btnSimpan
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtAddress
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtEmail
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNamaLengkap
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNip
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNoHp
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtNoPartner
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtTanggalLahir
import kotlinx.android.synthetic.main.activity_detail_karyawan.edtTempatLahir
import kotlinx.android.synthetic.main.activity_detail_karyawan.imgProfile
import kotlinx.android.synthetic.main.activity_detail_karyawan.layout_spinner_management
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerDivisi
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerJabatan
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerJenisKelamin
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerKantorCabang
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerManagement
import kotlinx.android.synthetic.main.activity_detail_karyawan.spinnerRole
import kotlinx.android.synthetic.main.activity_detail_karyawan.toolbar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
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

    private lateinit var myDialog: MyDialog
    var deleteMode = false

    var roleSelectedId = 0
    var divisionSelectedId = 0
    var officeSelectedId = 0
    var positionSelectedId = 0
    var genderSelectedId = 0
    var userManagementSelectedId = 0

    var imagePath : String? = null

    var isManagement = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_karyawan)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Detail Karyawan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        myDialog = MyDialog(this)
        karyawan = intent.getParcelableExtra("karyawan")
        isManagement = intent.getBooleanExtra("isManagement", false)
        userManagementSelectedId = karyawan.id

        initSpinners()
        setDataToView(karyawan)
        observeData()
        disableViews(false)

        vm.getDataOffice()
        vm.getUserManagement(2)

        btnSimpan.setOnClickListener {

            var compressedImageFile : File? = null

            imagePath?.let {
                //TODO compress this image
//                val file = File(it)
                compressedImageFile = File(it)

//                compressedImageFile = Compressor(this).compressToFile(file)
            }

            vm.updateKaryawan(
                karyawan.id.toString(),
                edtUsername.text.toString(),
                edtEmail.text.toString(),
                roleSelectedId.toString(),
                edtNamaLengkap.text.toString(),
                divisionSelectedId.toString(),
                officeSelectedId.toString(),
                positionSelectedId.toString(),
                edtNoPartner.text.toString(),
                edtTempatLahir.text.toString(),
                edtNoHp.text.toString(),
                edtAddress.text.toString(),
                edtTanggalLahir.text.toString(),
                genderSelectedId.toString(),
                userManagementSelectedId.toString(),
                compressedImageFile
                )
        }
    }

    fun setDataToView(data: User){
        edtUsername.setText(data.username)
        edtTanggalLahir.setText(data.birth_date)
        edtAddress.setText(data.address)
        edtEmail.setText(data.email)
        edtNamaLengkap.setText(data.full_name)
        edtNip.setText(data.npk)
        edtNoHp.setText(data.no_hp)
        edtNoPartner.setText(data.no_partner)
        edtTempatLahir.setText(data.origin_village)

        data.photo_profile_url?.let {
            imgProfile.loadCircleImage(it)
        }

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
                .toolbarFolderTitle("Images")
                .toolbarImageTitle("ketuk untuk memilih")
                .toolbarArrowColor(Color.WHITE)
                .single()
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
                    toast(dateSelected)
                    setDateToView(dateSelected)
                }
            }
        }

    }

    fun setDateToView(date: String) {
        edtTanggalLahir.setText(date)
    }

    fun disableViews(enabled: Boolean){

        imgProfile.isEnabled = enabled

        edtUsername.isEnabled = enabled
        edtTanggalLahir.isEnabled = enabled
        edtAddress.isEnabled = enabled
        edtEmail.isEnabled = enabled
        edtNamaLengkap.isEnabled = enabled
        edtNip.isEnabled = enabled
        edtNoHp.isEnabled = enabled
        edtNoPartner.isEnabled = enabled
        edtTempatLahir.isEnabled = enabled

        spinnerJenisKelamin.isEnabled = enabled
        spinnerKantorCabang.isEnabled = enabled
        spinnerDivisi.isEnabled = enabled
        spinnerJabatan.isEnabled = enabled
        spinnerRole.isEnabled = enabled
        spinnerManagement.isEnabled = enabled
    }

    fun initSpinners(){

        // spinner jabatan / position
        ArrayAdapter.createFromResource(
            this,
            R.array.position,
            android.R.layout.simple_spinner_item
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
                    positionSelectedId = position + 1
                }

            }
        }

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
                    userManagements.addAll(it.data.data)

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
                    toast(it.data.message)
//                    if (deleteMode) finish()
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    Timber.e { it.throwable.message.toString() }
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
            R.id.action_edit -> {
                disableViews(true)
                btnSimpan.visible()
            }
            R.id.action_edit_password -> {
                startActivity<EditPasswordActivity>("karyawan" to karyawan)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)


            imagePath = image.path

            imgProfile.loadCircleImage(image.path)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
