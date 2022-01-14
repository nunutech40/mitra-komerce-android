package id.android.kmabsensi.presentation.ubahprofile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.databinding.ActivityUbahProfileBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.sdm.tambahsdm.PartnerSelectedItem
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_delivery.*
import kotlinx.android.synthetic.main.activity_tambah_sdm.*
import kotlinx.android.synthetic.main.activity_ubah_profile.*
import kotlinx.android.synthetic.main.activity_ubah_profile.edtNoPartner
import kotlinx.android.synthetic.main.activity_ubah_profile.imgProfile
import kotlinx.android.synthetic.main.activity_ubah_profile.rvPartner
import kotlinx.android.synthetic.main.dialog_select_bank.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class UbahProfileActivity : BaseActivityRf<ActivityUbahProfileBinding>(
    ActivityUbahProfileBinding::inflate
) {

    private val vm: UbahProfileViewModel by inject()

    private var user: User? = null
    private var dataBank: AllBankResponse? = null

    var imagePath: String? = null

    private var genderSelectedId = 0
    private var martialStatus = -1
    private var compressedImage: File? = null

    private val disposables = CompositeDisposable()

    private lateinit var myDialog: MyDialog

    private val PICK_PARTNER_RC = 112

    private val groupAdpter = GroupAdapter<GroupieViewHolder>()
    private val partnerSelected = mutableListOf<Partner>()

    lateinit var dataArrayBank: List<DataBank>
    private var codeBank = ""
    private var nameBank = ""
    private var districtId = 0
    private var idTalent = 0
    private var idStaff = 0
    private lateinit var bankAdapter: SelectBankAdapter

    private val preferencesHelper: PreferencesHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar("Ubah Profile", isBackable = true)
//        setupObserver()
        setupListener()
        myDialog = MyDialog(this)

        user = intent.getParcelableExtra(USER_KEY)
        dataBank = intent.getParcelableExtra(BANK_KEY)
        dataArrayBank = dataBank!!.data

        initRv()

        user?.let { user -> setDataToView(user) }

        if (user?.role_id == 2) { //update manajemen jika digunakan
            districtId = user!!.staff?.districtId ?: 0
            idStaff = user!!.staff?.id ?: 0
            disableViews(false)
            setupObserver()
        } else if (user?.role_id == 3) { //talent
            districtId = user!!.talent?.districtId ?: 0
            idTalent = user!!.talent?.id ?: 0
            setupObserverTalent()
        }
    }

    private fun setupObserver() {
        vm.crudResponseStaff.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.success) {
                        compressedImage?.delete()
                        val dialogSuccess = MaterialDialog(this).show {
                            cornerRadius(16f)
                            customView(
                                R.layout.dialog_already_checkin,
                                scrollable = false,
                                horizontalPadding = true,
                                noVerticalPadding = true
                            )
                        }
                        val customView = dialogSuccess.getCustomView()
                        val btn_oke = customView.findViewById<Button>(R.id.btn_oke)
                        val tv_message = customView.findViewById<TextView>(R.id.tv_notif)
                        tv_message.text = resources.getString(R.string.success_edit_profile)
                        btn_oke.setOnClickListener { dialogSuccess.dismiss() }

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

        binding.apply {
            btnSimpan.setOnClickListener {
                user?.let { user ->

                    if (genderSelectedId == 0) {
                        createAlertError(
                            this@UbahProfileActivity,
                            "Warning",
                            "Pilih jenis kelamin dahulu",
                            3000
                        )
                        return@setOnClickListener
                    }

                    if (martialStatus == -1) {
                        createAlertError(
                            this@UbahProfileActivity,
                            "Warning",
                            "Pilih status pernikahan dahulu",
                            3000
                        )
                        return@setOnClickListener
                    }

//                    vm.updateKaryawan(
//                        user.id.toString(),
//                        tieUsername.text.toString(),
//                        tieEmail.text.toString(),
//                        user.role_id.toString(),
//                        tieFullName.text.toString(),
//                        user.division_id.toString(),
//                        user.office_id.toString(),
//                        user.position_id.toString(),
//                        edtNoPartner.text.toString(),
//                        tieVilage.text.toString(),
//                        tiePhone.text.toString(),
//                        tieFullAddres.text.toString(),
//                        tieBirthDay.text.toString(),
//                        genderSelectedId.toString(),
//                        user.user_management_id.toString(),
//                        user.status,
//                        compressedImage,
//                        tieJoinDate.text.toString(),
//                        martialStatus.toString(),
//                        bankAccountId.toString(),
//                        tieBank.toString(),
//                        tieNoRek.text.toString(),
//                        tieNoRekOwner.text.toString()
//                    )

                    vm.updateKaryawanStaff(
                        idStaff,
                        "PUT",
                        tieUsername.text.toString(),
                        tieEmail.text.toString(),
                        tieFullName.text.toString(),
                        user.division_id.toString(),
                        user.office_id.toString(),
                        user.position_id.toString(),
                        tiePhone.text.toString(),
                        tieFullAddres.text.toString(),
                        tieBirthDay.text.toString(),
                        genderSelectedId.toString(),
                        user.status,
                        districtId,
                        compressedImage,
                        tieJoinDate.text.toString(),
                        martialStatus.toString(),
                        codeBank,
                        nameBank,
                        tieNoRek.text.toString(),
                        tieNoRekOwner.text.toString()
                    )
                }
            }
        }

    }

    private fun setupObserverTalent() {
        vm.crudResponseTalent.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.success) {
                        compressedImage?.delete()
                        val dialogSuccess = MaterialDialog(this).show {
                            cornerRadius(16f)
                            customView(
                                R.layout.dialog_already_checkin,
                                scrollable = false,
                                horizontalPadding = true,
                                noVerticalPadding = true
                            )
                        }
                        val customView = dialogSuccess.getCustomView()
                        val btn_oke = customView.findViewById<Button>(R.id.btn_oke)
                        val tv_message = customView.findViewById<TextView>(R.id.tv_notif)
                        tv_message.text = resources.getString(R.string.success_edit_profile)
                        btn_oke.setOnClickListener {
                            dialogSuccess.dismiss()
                            finish()
                        }

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

        binding.apply {
            btnSimpan.setOnClickListener {
                user?.let { user ->

//                    if (genderSelectedId == 0) {
//                        createAlertError(
//                            this@UbahProfileActivity,
//                            "Warning",
//                            "Pilih jenis kelamin dahulu",
//                            3000
//                        )
//                        return@setOnClickListener
//                    }

//                    if (martialStatus == -1) {
//                        createAlertError(
//                            this@UbahProfileActivity,
//                            "Warning",
//                            "Pilih status pernikahan dahulu",
//                            3000
//                        )
//                        return@setOnClickListener
//                    }

//                    vm.updateKaryawan(
//                        user.id.toString(),
//                        tieUsername.text.toString(),
//                        tieEmail.text.toString(),
//                        user.role_id.toString(),
//                        tieFullName.text.toString(),
//                        user.division_id.toString(),
//                        user.office_id.toString(),
//                        user.position_id.toString(),
//                        edtNoPartner.text.toString(),
//                        tieVilage.text.toString(),
//                        tiePhone.text.toString(),
//                        tieFullAddres.text.toString(),
//                        tieBirthDay.text.toString(),
//                        genderSelectedId.toString(),
//                        user.user_management_id.toString(),
//                        user.status,
//                        compressedImage,
//                        tieJoinDate.text.toString(),
//                        martialStatus.toString(),
//                        bankAccountId.toString(),
//                        nameBank,
//                        tieNoRek.text.toString(),
//                        tieNoRekOwner.text.toString()
//                    )

                    if (!formValidation()) {
                        return@setOnClickListener
                    } else if (!validationEmail()) {
                        return@setOnClickListener
                    }
                    vm.updateKaryawanTalent(
                        idTalent,
                        "PUT",
                        tieUsername.text.toString(),
                        tieEmail.text.toString(),
                        tieFullName.text.toString(),
                        user.division_id.toString(),
                        user.office_id.toString(),
                        user.position_id.toString(),
                        tiePhone.text.toString(),
                        tieFullAddres.text.toString(),
                        tieBirthDay.text.toString(),
                        genderSelectedId.toString(),
                        user.talent?.status.toString(),
                        districtId,
                        compressedImage,
                        tieJoinDate.text.toString(),
                        martialStatus.toString(),
                        codeBank,
                        nameBank,
                        tieNoRek.text.toString(),
                        tieNoRekOwner.text.toString()
                    )
                }
            }

        }
    }

    private fun setupListener() {
        binding.apply {
            tvChangePhoto.setOnClickListener {
                ImagePicker.create(this@UbahProfileActivity)
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

            imgProfile.setOnClickListener {
                ImagePicker.create(this@UbahProfileActivity)
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

            tieBirthDay.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    this@UbahProfileActivity,
                    { view, years, monthOfYear, dayOfMonth ->
                        c[Calendar.YEAR] = years
                        c[Calendar.MONTH] = monthOfYear
                        c[Calendar.DAY_OF_MONTH] = dayOfMonth
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateSelected: String = dateFormat.format(c.time)
                        setDateToView(dateSelected)
                    },
                    year,
                    month,
                    day
                )
                dpd.datePicker.maxDate = c.timeInMillis
                dpd.show()
//                MaterialDialog(this@UbahProfileActivity).show {
//                    datePicker { dialog, date ->
//
//                        // Use date (Calendar)
//
//                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                        val dateSelected: String = dateFormat.format(date.time)
//                        setDateToView(dateSelected)
//                    }
//                }
            }

            tieBank.setOnClickListener {
                val dialogBanks = MaterialDialog(this@UbahProfileActivity).show {
                    cornerRadius(16f)
                    customView(
                        R.layout.dialog_select_bank,
                        scrollable = false,
                        horizontalPadding = true,
                        noVerticalPadding = true
                    )
                }
                val customView = dialogBanks.getCustomView()
                dialogBanks.setCanceledOnTouchOutside(false)
                val edtSelectBank = customView.findViewById<AppCompatEditText>(R.id.et_search_bank)
                val rvBank = customView.findViewById<RecyclerView>(R.id.rv_banks)
                val ivClose = customView.findViewById<AppCompatImageView>(R.id.close)
                bankAdapter = SelectBankAdapter(
                    this@UbahProfileActivity,
                    object : SelectBankAdapter.onAdapterListener {
                        override fun onCLick(data: DataBank) {
                            nameBank = data.name.toString()
                            codeBank = data.code.toString()
                            dialogBanks.dismiss()
                            tieBank.text = data.name?.toEditable()
                            Log.d("TAG", "onCLick: $codeBank")
                        }

                    }
                )
                rvBank.apply {
                    adapter = bankAdapter
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@UbahProfileActivity)
                }
                bankAdapter.setData(dataArrayBank)

                edtSelectBank.doAfterTextChanged {
                    bankAdapter.filter.filter(it.toString())
                }
                ivClose.setOnClickListener { dialogBanks.dismiss() }
            }

//            tieJoinDate.setOnClickListener {
//                MaterialDialog(this@UbahProfileActivity).show {
//                    datePicker { dialog, date ->
//
//                        // Use date (Calendar)
//
//                        dialog.dismiss()
//
//                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                        val dateSelected: String = dateFormat.format(date.time)
//                        setJoinDate(dateSelected)
//                    }
//                }
//            }

            edtNoPartner.setOnClickListener {
//            startActivityForResult<PartnerPickerActivity>(
//                PICK_PARTNER_RC
//            )
            }

            //spinner pernikahan
            ArrayAdapter.createFromResource(
                this@UbahProfileActivity,
                R.array.martial_statuses,
                R.layout.spinner_item
            )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spStatusPernikahan.adapter = adapter

                    binding.spStatusPernikahan.onItemSelectedListener =
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
                                Log.d("TAG", "onItemMArtial =$martialStatus")
                            }

                        }
                }

            //spinner jenis kelamin
            ArrayAdapter.createFromResource(
                this@UbahProfileActivity,
                R.array.genders,
                R.layout.spinner_item
            )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spJenisKelamin.adapter = adapter

                    binding.spJenisKelamin.onItemSelectedListener =
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
                                Log.d("TAG", "onItemSelected: $genderSelectedId")
                            }

                        }
                }

//            //spinner jenis kelamin
//            ArrayAdapter.createFromResource(
//                this@UbahProfileActivity,
//                R.array.gender,
//                android.R.layout.simple_spinner_item
//            )
//                .also { adapter ->
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    spinnerJenisKelamin.adapter = adapter
//
//                    spinnerJenisKelamin.onItemSelectedListener =
//                        object : AdapterView.OnItemSelectedListener {
//                            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                            }
//
//                            override fun onItemSelected(
//                                parent: AdapterView<*>?,
//                                view: View?,
//                                position: Int,
//                                id: Long
//                            ) {
//                                genderSelectedId = position
//                            }
//
//                        }
//                }
//
//            /* spinner martial status */
//            ArrayAdapter.createFromResource(
//                this@UbahProfileActivity,
//                R.array.martial_status,
//                android.R.layout.simple_spinner_item
//            )
//                .also { adapter ->
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    spinnerStatusPernikahan.adapter = adapter
//
//                    spinnerStatusPernikahan.onItemSelectedListener =
//                        object : AdapterView.OnItemSelectedListener {
//                            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                            }
//
//                            override fun onItemSelected(
//                                parent: AdapterView<*>?,
//                                view: View?,
//                                position: Int,
//                                id: Long
//                            ) {
//                                martialStatus = position - 1
//                            }
//
//                        }
        }

    }

    private fun initRv() {
        rvPartner.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = groupAdpter
        }
    }

    private fun setDataToView(data: User) {

//        if (data.bank_accounts.isNullOrEmpty()){
//            Log.d("TAG", "checks: data null")
//            setBankUser(dataNew, dataArrayBank)
////            searchAllBank(dataArrayBank)
//        } else{
//            Log.d("TAG", "checks: ${data.bank_accounts[0].bankName}")
//            nameBank = data.bank_accounts.get(0).bankName.toString()
//            codeBank = data.bank_accounts.get(0).bankCode.toString()
//            Log.d("TAG", "setDataToView: ${nameBank+codeBank}")
//        }
        nameBank = data.bank_accounts?.get(0)?.bankName.toString()
        codeBank = data.bank_accounts?.get(0)?.bankCode.toString()
        martialStatus = data.martial_status
        Log.d("TAG", "setDataToView: ${nameBank + codeBank}")
        binding.apply {

            tieUsername.text = data.username.toEditable()
            tieFullName.text = data.full_name.toEditable()
            tieJoinDate.text = data.join_date.toEditable()
            tieBirthDay.text = data.birth_date.toEditable()
            tieVilage.text = data.origin_village?.toEditable()
            tiePhone.text = data.no_hp.toEditable()
            tieEmail.text = data.email.toEditable()
            tieFullAddres.text = data.address.toEditable()

            if (data.no_partners.isNullOrEmpty()) {
                layoutPartner.gone()
            }

            data.partner_assignments?.forEach {
                partnerSelected.add(
                    Partner(
                        id = it.id,
                        fullName = it.fullName,
                        partnerDetail = PartnerDetail(noPartner = it.noPartner!!)
                    )
                )
            }
            populatePartnerSelected()

            data.bank_accounts?.let { bank_account ->
                if (bank_account.isNotEmpty()) {
//                    bankAccountId = bank_account[0].id
                    tieBank.text = bank_account[0].bankName?.toEditable()
                    tieNoRek.text = bank_account[0].bankNo?.toEditable()
                    tieNoRekOwner.text = bank_account[0].bankOwnerName?.toEditable()
                }
            }

            if (data.role_id ==2){
                imgProfile.setImageResource(R.drawable.logo_mitra_km)
            } else {
                imgProfile.loadCircleImage(
                    data.photo_profile_url
                        ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
                )
            }

//            spinnerStatusPernikahan.setSelection(data.martial_status + 1)
//            spinnerJenisKelamin.setSelection(data.gender)
            if (data.gender == 2) {
                genderSelectedId = 1
                tieGender.text = "Perempuan".toEditable()
            } else if (data.gender == 1) {
                genderSelectedId = 0
                tieGender.text = "Laki-laki".toEditable()

            }

            if (data.martial_status == 0){
                tieStatusPernikahan.text = "Belum Menikah".toEditable()
            } else if (data.martial_status ==1){
                tieStatusPernikahan.text = "Sudah Menikah".toEditable()
            }
            spJenisKelamin.setSelection(genderSelectedId)
            spStatusPernikahan.setSelection(martialStatus)
        }

    }

    private fun populatePartnerSelected(enableClose: Boolean = false) {
        if (partnerSelected.isNotEmpty()) edtNoPartner.setHint("") else edtNoPartner.setHint("Pilih Partner")
        groupAdpter.clear()
        partnerSelected.forEach {
            groupAdpter.add(PartnerSelectedItem(it, enableClose) {
                partnerSelected.removeAt(partnerSelected.indexOf(it))
                populatePartnerSelected(enableClose)
            })
        }
    }

    fun setDateToView(date: String) {
        binding.tieBirthDay.text = date.toEditable()
    }

    fun setJoinDate(date: String) {
        binding.tieJoinDate.text = date.toEditable()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)

            imagePath = image.path

            compress(File(imagePath))

        }

        if (requestCode == PICK_PARTNER_RC && resultCode == Activity.RESULT_OK) {
            val partners = data?.getParcelableExtra<Partner>(PARTNER_DATA_KEY)
            edtNoPartner.setText(partners?.partnerDetail?.noPartner)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun compress(file: File) {
        disposables.add(
            Compressor(this)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
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

    private fun formValidation(): Boolean {
        binding.apply {
            val username =
                ValidationForm.validationTextInputEditText(tieUsername, tilUsername, "Wajib diisi.")
            val fullName =
                ValidationForm.validationTextInputEditText(tieFullName, tilFullName, "Wajib diisi.")
            val noHp =
                ValidationForm.validationTextInputEditText(tiePhone, tilPhone, "Wajib diisi.")
            val address = ValidationForm.validationTextInputEditText(
                tieFullAddres,
                tilFullAddres,
                "Wajib diisi."
            )
            val noRek =
                ValidationForm.validationTextInputEditText(tieNoRek, tilNoRek, "Wajib diisi.")
            val noRekOwner = ValidationForm.validationTextInputEditText(
                tieNoRekOwner,
                tilNoRekOwner,
                "Wajib diisi."
            )
            val email =
                ValidationForm.validationTextInputEditText(tieEmail, tilEmail, "Wajib diisi.")
            return username && fullName && noHp && address && noRek && noRekOwner && email
        }
    }

    private fun validationEmail(): Boolean {
        binding.apply {
            val validEmail = ValidationForm.validationEmail(
                tieEmail,
                tilEmail,
                "Format Email Kamu tidak sesuai."
            )
            return validEmail
        }
    }

    private fun disableViews(enabled: Boolean) {
        setupToolbar("Data Profile", isBackable = true)
        binding.apply {
            tieUsername.isEnabled = enabled
            tieFullName.isEnabled = enabled
            tieBirthDay.isEnabled = enabled
            tieBirthDay.isEnabled = enabled
            tiePhone.isEnabled = enabled
            tieEmail.isEnabled = enabled
            tieFullAddres.isEnabled = enabled
            tieJoinDate.isEnabled = enabled
            tieBank.isEnabled = enabled
            tieNoRek.isEnabled = enabled
            tieNoRekOwner.isEnabled = enabled
            tilGender.visible()
            tilStatusPernikahan.visible()
            imgProfile.isEnabled = enabled
            tvChangePhoto.gone()
            btnSimpan.gone()
            llSpGender.gone()
            llSpPernikahan.gone()
        }
    }
}
