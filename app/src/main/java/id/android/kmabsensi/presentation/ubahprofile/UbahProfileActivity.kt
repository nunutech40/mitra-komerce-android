package id.android.kmabsensi.presentation.ubahprofile

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
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ubah_profile.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UbahProfileActivity : BaseActivity() {

    private val vm: UbahProfileViewModel by inject()

    private lateinit var user: User

    var imagePath : String? = null

    private var genderSelectedId = 0
    private var martialStatus = 0
    private var bankAccountId = 0
    private var compressedImage : File? = null

    private val disposables = CompositeDisposable()

    private lateinit var myDialog: MyDialog

    private val PICK_PARTNER_RC = 112

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_profile)

        disableAutofill()

        setupToolbar("Ubah Profile")

        myDialog = MyDialog(this)

        user = intent.getParcelableExtra(USER_KEY)

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

        btnSimpan.setOnClickListener {
            vm.updateKaryawan(
                user.id.toString(),
                edtUsername.text.toString(),
                edtEmail.text.toString(),
                user.role_id.toString(),
                edtNamaLengkap.text.toString(),
                user.division_id.toString(),
                user.office_id.toString(),
                user.position_id.toString(),
                edtNoPartner.text.toString(),
                edtAsalDesa.text.toString(),
                edtNoHp.text.toString(),
                edtAddress.text.toString(),
                edtTanggalLahir.text.toString(),
                genderSelectedId.toString(),
                user.user_management_id.toString(),
                user.status,
                compressedImage,
                edtTanggalBergabung.text.toString(),
                martialStatus.toString(),
                bankAccountId.toString(),
                edtNamaBank.text.toString(),
                edtNoRekening.text.toString(),
                edtPemilikRekening.text.toString()
            )
        }

        vm.crudResponse.observe(this, Observer {
            when(it){
                is UiState.Loading -> { myDialog.show() }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if(it.data.status){
                        compressedImage?.delete()
                        createAlertSuccess(this, it.data.message)

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

        setDataToView(user)
    }

    private fun setDataToView(data: User){
        edtUsername.setText(data.username)
        edtTanggalLahir.setText(data.birth_date)
        edtAddress.setText(data.address)
        edtEmail.setText(data.email)
        edtNamaLengkap.setText(data.full_name)
        edtNoHp.setText(data.no_hp)
        edtNoPartner.setText(data.no_partner)
        edtAsalDesa.setText(data.origin_village)
        edtTanggalBergabung.setText(data.join_date)

        data.bank_accounts?.let {bank_account ->
            if (bank_account.isNotEmpty()){
                bankAccountId = bank_account[0].id
                edtNamaBank.setText(bank_account[0].bankName)
                edtNoRekening.setText(bank_account[0].bankNo)
                edtPemilikRekening.setText(bank_account[0].bankOwnerName)
            }
        }

        data.photo_profile_url?.let {
            d { it }
            imgProfile.loadCircleImage(it)
        }
        spinnerStatusPernikahan.setSelection(data.martial_status)
        spinnerJenisKelamin.setSelection(data.gender-1)


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
            startActivityForResult<PartnerPickerActivity>(
                PICK_PARTNER_RC
            )
        }


    }

    fun setDateToView(date: String) {
        edtTanggalLahir.setText(date)
    }

    fun setJoinDate(date: String) {
        edtTanggalBergabung.setText(date)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)

            imagePath = image.path

            compress(File(imagePath))

        }

        if (requestCode == PICK_PARTNER_RC && resultCode == Activity.RESULT_OK){
            val partners = data?.getParcelableExtra<SimplePartner>(SIMPLE_PARTNER_DATA_KEY)
            edtNoPartner.setText(partners?.noPartner)
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
