package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.stfalcon.imageviewer.StfalconImageViewer
import com.wildma.idcardcamera.camera.IDCardCamera
import com.wildma.idcardcamera.utils.ImageUtils
import com.wildma.idcardcamera.utils.PermissionUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.Item
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams.UpdateItem
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse.Data
import id.android.kmabsensi.data.remote.response.kmpoint.model.ShoppingRequestItem
import id.android.kmabsensi.data.remote.response.kmpoint.model.ShoppingRequestParticipant
import id.android.kmabsensi.databinding.ActivityShoppingDetailsBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingCartActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter.TalentItem
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter.WithDrawalShoppingItem
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.BuktiTransferModel
import id.android.kmabsensi.presentation.kmpoint.tambahdaftarbelanja.ToolsModel
import id.android.kmabsensi.utils.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import java.io.File

class ShoppingDetailsFinanceActivity : BaseActivity() {

    private val TAG = "_detailResponse"
    private val TAGUpdate = "_updateResponse"
    private val TAGAttachment = "_AttachemntResponse"
    private val vm : ShoppingDetailFinanceViewModel by inject()
    private var selectedPhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 0
    private val photoAdapter = GroupAdapter<GroupieViewHolder>()
    private val talentAdapter = GroupAdapter<GroupieViewHolder>()
    var idx: Int = 1
    private val dataPhoto: MutableList<BuktiTransferModel> = arrayListOf()
    private val datatalent: MutableList<ShoppingRequestParticipant> = arrayListOf()
    private var detailShopping : Data? = null
    private var isReverse = false
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

    private lateinit var photoFile : File
    private var compressedImage: File? = null
    private var capturedFilePath: String? = null
    private var skeletonPage: SkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListener()
        setupRv()
        setupObserver()
        /**
        first data(dataPhoto) used for button take pict
         */
        dataPhoto.add(BuktiTransferModel(0, null))
        setupDataDummy(dataPhoto)
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
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSelesai.setOnClickListener {
            if (validateForm()){
                updateSataShopping()
            }
        }
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
            if (dataPhoto.size == 1){
                it.rvTransfer.requestFocus()
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

    private fun setupDataDummy(array: MutableList<BuktiTransferModel>) {
        photoAdapter.clear()
        array.forEach {
            photoAdapter.add(WithDrawalShoppingItem(this, it, object : WithDrawalShoppingItem.onCLick {
                override fun onRemove(position: Int) {
                    dataPhoto.removeAt(position)
                    photoAdapter.removeGroupAtAdapterPosition(position)
                    photoAdapter.notifyDataSetChanged()
                    setupDataDummy(dataPhoto)
                }
            }) {
                if (it.id == 0) {
                    if (dataPhoto.size <= 1){
                        if (checkPermissionCamera()){
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                                takePictureIntent.resolveActivity(packageManager)?.also {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                                }
                            }
                        }
                    }else toast("Berkas sudah ditambahkan.")
                }else{
                    StfalconImageViewer.Builder<String>(
                            this,
                            listOf(compressedImage?.path)
                    ) { view, image ->
                        Glide.with(this)
                                .load(image).into(view)
                    }.show()
                }

            })
        }
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
                if (!s.toString().equals("")){
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

            if (toolsName.text.toString().equals("")) {
                toolsName.error = "form nama barang tidak boleh kosong!"
                checked = false
                binding.btnSelesai.isEnabled = false
            } else binding.btnSelesai.isEnabled = true

            if (toolsPrice.text.toString().equals("")) {
                toolsPrice.error = "form tidak boleh kosong!"
                checked = false
                binding.btnSelesai.isEnabled = false
            } else binding.btnSelesai.isEnabled = true

            if (checked) {
                val total = if (toolsPrice.isEnabled) toolsPrice.text.toString() else "0"
                val datatools = ToolsModel(i, toolsName.text.toString(), total)
                updateListItems.add(UpdateShoppingRequestParams.UpdateItem(
                        id = listItemsUpdate[i].id,
                        name = datatools.name!!,
                        total = datatools.price!!.toInt(),
                        will_delete = !toolsName.isEnabled))
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
            capturedFilePath =
                    compressCustomerCaptureImage(this, imagePath)
            deleteCaptureFileFromPath(imagePath)
            photoFile = File(capturedFilePath)
            Log.d("_photoFile", "confirmImage: $photoFile, $bitmap")
        }
        return photoFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            /**
            get data photo after take pict
             */
            selectedPhotoUri = data.data
            val bitmap = data.extras!!.get("data") as Bitmap
            compressedImage = convertBitmap(bitmap)

            idx++
            /**
             * membalikan posisi data array ke normal
             */
            if (isReverse) {
                dataPhoto.reverse()
                isReverse = false
            }
            dataPhoto.add(BuktiTransferModel(idx, bitmap))

            /**
             * membalikan posisi data array
             */
            if (!isReverse) {
                dataPhoto.reverse()
                isReverse = true
            }
            setupDataDummy(dataPhoto)
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