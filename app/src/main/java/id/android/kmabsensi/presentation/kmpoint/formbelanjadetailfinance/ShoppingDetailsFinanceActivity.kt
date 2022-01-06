package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wildma.idcardcamera.utils.ImageUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.FilePickerConst.KEY_SELECTED_MEDIA
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.Item
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams.UpdateItem
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse.Data
import id.android.kmabsensi.data.remote.response.kmpoint.model.ShoppingRequestItem
import id.android.kmabsensi.data.remote.response.kmpoint.model.ShoppingRequestParticipant
import id.android.kmabsensi.databinding.ActivityShoppingDetailsBinding
import id.android.kmabsensi.databinding.DialogChooseImageBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingCartActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter.TalentItem
import id.android.kmabsensi.presentation.kmpoint.tambahdaftarbelanja.ToolsModel
import id.android.kmabsensi.utils.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import org.koin.android.ext.android.inject
import java.io.File
import org.joda.time.DateTime

class ShoppingDetailsFinanceActivity : BaseActivity() {

    private val TAG = "_detailResponse"
    private val TAGUpdate = "_updateResponse"
    private val vm : ShoppingDetailFinanceViewModel by inject()
    private val REQUEST_IMAGE_CAPTURE = 0
    private val photoAdapter = GroupAdapter<GroupieViewHolder>()
    private val talentAdapter = GroupAdapter<GroupieViewHolder>()
    private val datatalent: MutableList<ShoppingRequestParticipant> = arrayListOf()
    private var detailShopping : Data? = null
    private val binding by lazy {
        ActivityShoppingDetailsBinding.inflate(layoutInflater)
    }
    private val idDetailSHopping by lazy {
        intent.getIntExtra("idDetailSHopping", 0)
    }
    private var listItemsUpdate: ArrayList<UpdateItem> = arrayListOf()
    private var updateListItems: ArrayList<UpdateItem> = arrayListOf() // new data for update items
    private var shoopingRequestItems: ShoppingRequestItem? = null
    private var listItems: ArrayList<Item> = arrayListOf()
    private var compressedImage: File? = null
    private var skeletonPage: SkeletonScreen? = null
    private lateinit var photoFile : File
    private var capturedFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListener()
        setupRv()
        setupObserver()
    }

    private fun setupObserver() {
        vm.getShoppingDetail(idDetailSHopping)
        vm.shoppingDetail.observe(this, {
            when (it) {
                is UiState.Loading ->{
                    skeletonPage = Skeleton.bind(binding.layout)
                            .load(R.layout.skeleton_detail_km_poin)
                            .show()
                    Log.d(TAG, "Loading...")
                }
                is UiState.Success -> {
                    skeletonPage?.hide()
                    Log.d(TAG, "Success ${it.data}")
                    setupView(it.data.data)
                }
                is UiState.Error -> {
                    skeletonPage?.hide()
                    Log.d(TAG, "Error... ${it.throwable}")
                }
            }
        })
    }

    private fun setupRv() {
//        list photo
        val linearLayoutManager = GridLayoutManager(this, 4)
        binding.rvTransfer.apply {
            layoutManager = linearLayoutManager
            adapter = photoAdapter
        }

//        list talent
        val talentLayoutManager = LinearLayoutManager(this)
        binding.rvTalent.apply {
            layoutManager = talentLayoutManager
            adapter = talentAdapter
        }
    }

    private fun setupListener() {
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                onBackPressed()
            }
            btnSelesai.setOnClickListener {
                if (validateForm()) {
                    updateSataShopping()
                }
            }

            addImage.setOnClickListener {
                showBottomDialog()
            }
        }

    }

    private fun showBottomDialog(){
        val bottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val bottomBinding = DialogChooseImageBinding.inflate(layoutInflater, null, false)
        bottomSheet.setContentView(bottomBinding.root)
        bottomBinding.apply {
            ambilGallery.setOnClickListener {
                openGallery()
                bottomSheet.dismiss()
            }
            ambilFoto.setOnClickListener {
                openCamera()
                bottomSheet.dismiss()
            }
        }
        bottomSheet.show()
    }

    private fun openCamera(){
        if (checkPermissionCamera()){
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun openGallery() {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // check if all permissions are granted
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            FilePickerBuilder.instance
                                .enableCameraSupport(true)
                                .setMaxCount(1) //optional
                                .setActivityTheme(R.style.LibAppTheme) //optional
                                .pickPhoto(this@ShoppingDetailsFinanceActivity)
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // permission is denied permenantly, navigate user to app settings
                            alert(
                                "This app needs permission to use this feature. You can grant them in app settings.",
                                "Need Permission"
                            ) {
                                yesButton { openSettings() }
                            }.show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun updateSataShopping() {
        var userId = ArrayList<Int>()
        detailShopping?.shoopingRequestParticipants!!.forEach {
            userId.add(it.userId)
        }
        saveDataFormTools()
        vm.updateShoppingRequest(idDetailSHopping,
        UpdateShoppingRequestParams(
                notes = binding.etNotes.text.toString(),
                items = updateListItems,
                participant_user_ids = userId,
                status = "completed",
        )).observe(this, {
            when (it) {
                is UiState.Loading -> {
                    setupLoadAnimation(true)
                }
                is UiState.Success -> {
                    Log.d(TAGUpdate, "Success... ${it.data}")
                    if (it.data.success){
                        vm.uploadAttachment(idDetailSHopping,
                                attachmentFile = compressedImage).observe(this, {
                            when (it) {
                                is UiState.Loading -> Log.d(TAG, "Loading...")
                                is UiState.Success -> {
                                    setupLoadAnimation(false)
                                    Log.d(TAG, "setupListener: ${it.data.status}")
                                    if (it.data.status) {
                                        startActivity<ShoppingCartActivity>("_isFinance" to true)
                                        finishAffinity()
                                    } else createAlertError(this, "Prosess Gagal!", it.data.message)
                                }
                                is UiState.Error -> setupLoadAnimation(false)
                            }
                        })
                    }else {
                        createAlertError(this, "Prosess Gagal!", it.data.message!!)
                        setupLoadAnimation(false)
                    }
                }
                is UiState.Error -> {
                    setupLoadAnimation(false)
                    Log.d(TAGUpdate, "Error...")
                }
            }
        })
    }

    private fun validateForm() : Boolean{
        var isChecked = true
        binding.let {
            if (compressedImage == null){
                createAlertError(this, "Prosess gagal!", "Anda belum meng-upload bukti transaksi.")
                isChecked = false
            }
        }
        return isChecked
    }

    private fun setupView(data: Data?) {
        detailShopping = data
        binding.toolbar.txtTitle.text = getString(R.string.text_detail_belanja)
        data?.partner?.let {
            Glide.with(this)
                    .load(it.user?.photoProfileUrl)
            .centerCrop()
                .placeholder(R.drawable.ic_my_profile)
                .into(binding.imgProfile)
            binding.txNoPartner.text = "No. Partner : "+it.noPartner.toString()
            binding.txUsername.text = it.user?.fullName?: "-"
        }

        data?.shoopingRequestItems!!.forEach {
            listItemsUpdate.add(UpdateItem(id = it.id!!, name = it.name!!, total = it.total!!, will_delete = false))
            shoopingRequestItems = it
            editFormTools()
        }

        data.shoopingRequestParticipants!!.forEach {
            datatalent.add(it)
            talentAdapter.add(TalentItem(this, it))
        }

        binding.etNotes.text = data.notes?.toEditable()
        binding.txTotalPrice.text = data.total.toString()

    }

    private fun editFormTools() {
        val inflater = LayoutInflater.from(this).inflate(R.layout.item_row_form_belanja, null, false)
        binding.llFormTools.addView(inflater, binding.llFormTools.childCount)
        val btnRemove: ImageView = inflater.findViewById(R.id.btn_remove)
        val toolname: EditText = inflater.findViewById(R.id.et_name_tools)
        val toolPrice: EditText = inflater.findViewById(R.id.tx_price_forecasts)
        btnRemove.gone()
        toolPrice.setText(shoopingRequestItems?.total.toString(), TextView.BufferType.EDITABLE)
        toolname.setText(shoopingRequestItems?.name.toString(), TextView.BufferType.EDITABLE)
        var total = 0
        toolPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != ""){
                    if (s.toString().toInt() > 100000000) {
                        toolPrice.text = "100000000".toEditable()
                        toolPrice.error = "Batas maksimal adalah Rp.100-juta."
                    }
                }
                total = 0
                saveDataFormTools()
                updateListItems.forEach {
                    total += it.total
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.txTotalPrice.text = total.toString()
            }
        })
    }

    private fun saveDataFormTools() {
        listItems.clear()
        updateListItems.clear()
        val count = binding.llFormTools.childCount
        var v: View? = null
        var checked = true

        for (i in 0 until count) {
            v = binding.llFormTools.getChildAt(i)
            val toolsName: EditText = v.findViewById(R.id.et_name_tools)
            val toolsPrice: EditText = v.findViewById(R.id.tx_price_forecasts)
            val btnRemove: ImageView = v.findViewById(R.id.btn_remove)
            btnRemove.gone()

            if (toolsName.text.toString() == "") {
                toolsName.error = "form nama barang tidak boleh kosong!"
                checked = false
                binding.btnSelesai.isEnabled = false
            } else binding.btnSelesai.isEnabled = true

            if (toolsPrice.text.toString() == "") {
                toolsPrice.error = "form tidak boleh kosong!"
                checked = false
                binding.btnSelesai.isEnabled = false
            } else binding.btnSelesai.isEnabled = true

            if (checked) {
                val total = if (toolsPrice.isEnabled) toolsPrice.text.toString() else "0"
                val datatools = ToolsModel(i, toolsName.text.toString(), total)
                updateListItems.add(
                    UpdateItem(
                        id = listItemsUpdate[i].id,
                        name = datatools.name!!,
                        total = datatools.price!!.toInt(),
                        will_delete = !toolsName.isEnabled)
                )
            }
        }
    }

    private fun convertBitmap(bitmap: Bitmap) : File{
        var imagePath = ""
        val cw = ContextWrapper(applicationContext)
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, DateTime.now().millis.toString() + ".jpg")

        imagePath = file.path

        if (ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
            capturedFilePath = compressCustomerCaptureImage(this, imagePath)
            deleteCaptureFileFromPath(imagePath)
            photoFile = File(capturedFilePath)
            Log.d("_photoFile", "confirmImage: $photoFile, $bitmap")
        }
        return photoFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FilePickerConst.REQUEST_CODE_PHOTO -> {
                if (resultCode == RESULT_OK && data != null){
                    val photoPaths = ArrayList<Uri>()
                    photoPaths.addAll(data.getParcelableArrayListExtra(KEY_SELECTED_MEDIA)!!)
                    compressedImage = File(compressCustomerCaptureImage(this,
                        ContentUriUtils.getFilePath(this, photoPaths[0])!!
                    )!!)

                    binding.addImage.setImageURI(photoPaths[0])
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK && data != null){
                    val datas = data.extras!!.get("data") as Bitmap
                    compressedImage = convertBitmap(datas)
                    binding.addImage.setImageBitmap(datas)
                }
            }
        }
    }

    private fun setupLoadAnimation(isPlay: Boolean){
        if (isPlay){
            binding.animationView.visible()
            binding.layout.isEnabled = false
        }else{
            binding.animationView.gone()
            binding.layout.isEnabled = true
        }
    }
}