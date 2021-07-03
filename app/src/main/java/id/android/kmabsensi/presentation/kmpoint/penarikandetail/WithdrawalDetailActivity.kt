package id.android.kmabsensi.presentation.kmpoint.penarikandetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import com.stfalcon.imageviewer.StfalconImageViewer
import com.wildma.idcardcamera.camera.IDCardCamera
import com.wildma.idcardcamera.utils.ImageUtils
import com.wildma.idcardcamera.utils.PermissionUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.RequestWithdrawParams
import id.android.kmabsensi.data.remote.response.kmpoint.DetailWithdrawResponse.DataDetailWithDraw
import id.android.kmabsensi.data.remote.response.kmpoint.DetailWithdrawResponse
import id.android.kmabsensi.databinding.ActivityWithdrawalDetailBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kmpoint.penarikan.WithdrawListActivity
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.items.TalentItems
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.items.TransactionImgItem
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.items.TransactionItem
import id.android.kmabsensi.utils.*
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tambah_sdm.*
import kotlinx.android.synthetic.main.activity_tambah_sdm.imgProfile
import kotlinx.android.synthetic.main.fragment_home_management.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import java.io.File

class WithdrawalDetailActivity : BaseActivity() {
    private val vm : DetailWithDrawViewModel by inject()
    private val binding by lazy {
        ActivityWithdrawalDetailBinding.inflate(layoutInflater)
    }
    private val idWithdraw by lazy {
        intent.getIntExtra("_idWithdraw", 0)
    }

    /**
     * transactionType = 1 -> withdraw_to_me
     * transactionType = 0 -> share_to_talent
     */

    private val transactionType by lazy {
        intent.getIntExtra("_typePenarikan", 0)
    }
    private val TAG = "_withdrawResponse"
    private var selectedPhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 0
    private val photoAdapter = GroupAdapter<GroupieViewHolder>()
    private val photoImgAdapter = GroupAdapter<GroupieViewHolder>()
    private val talentAdapter = GroupAdapter<GroupieViewHolder>()
    private val dataArray: MutableList<BuktiTransferModel> = arrayListOf()
    private var isReverse = false
    private var capturedFilePath: String? = null
    private lateinit var photoFile : File
    private var detailWithDraw : DataDetailWithDraw? = null

    private val disposables = CompositeDisposable()
    private var compressedImage: File? = null
    private var skeletonPage: SkeletonScreen? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupObserver()
        setupListener()
        setupRv()
    }

    private fun setupObserver() {
        vm.getDetalWithdraw(idWithdraw).observe(this, {
            when (it) {
                is UiState.Loading -> {
                    skeletonPage = Skeleton.bind(binding.layout)
                            .load(R.layout.skeleton_detail_km_poin)
                            .show()
                    Log.d("_detailWithdraw", "Loading...")
                }
                is UiState.Success -> {
                    skeletonPage?.hide()
                    setupView(it.data.data)
                    detailWithDraw = it.data.data
                }
                is UiState.Error -> {
                    skeletonPage?.hide()
                    Log.d("_detailWithdraw", "Error... ${it.throwable}")
                }
            }
        })
    }

    @SuppressLint("ResourceAsColor")
    private fun setupView(data: DataDetailWithDraw) {
        if (data.status.equals("completed")) {
            binding.rvTransferPict.visible()
            binding.rvTransfer.gone()
            binding.btnSelesai.apply {
                this.isClickable = false
                this.alpha = 0.5f
                this.setBackgroundResource(R.drawable.bg_white_10dp)
                this.setTextColor(R.color.cl_grey_dark)
            }
            data.attachments.forEach {
                photoImgAdapter.add(TransactionImgItem(this, it){
                    StfalconImageViewer.Builder<String>(
                            this,
                            listOf(it.attachmentUrl)
                    ) { view, image ->
                        Glide.with(this)
                                .load(image).into(view)
                    }.show()
                })
            }
        }else{
            binding.rvTransfer.visible()
            binding.rvTransferPict.gone()
            dataArray.add(BuktiTransferModel(0, null))
            setupDataDummy(dataArray)
        }

        val totalPerTalent = (data.nominal / if(data.talentRecipients.size == 0) 1 else data.talentRecipients.size )
        data.talentRecipients.forEach {
            talentAdapter.add(TalentItems(this, it, totalPerTalent))
        }

        var type = ""
        if (transactionType == 0) {
            binding.llKasihTalent.visible()
            binding.llTarikSaldo.gone()
            type = "Kasih ke Talent"
        } else {
            binding.llKasihTalent.gone()
            binding.llTarikSaldo.visible()
            type = "Tarik Saldo"
            binding.etNameBank.text = data.bankName.toEditable()
            binding.etNoRek.text = data.bankNo.toEditable()
            binding.etRekOwner.text = data.bankOwnerName.toEditable()
        }
        binding.etNameBank.isEnabled = false
        binding.etNoRek.isEnabled = false
        binding.etRekOwner.isEnabled = false

        binding.toolbar.txtTitle.text = resources.getString(R.string.text_penarikan_poin)
        binding.txType.text = type
        binding.txTotalPoin.text = data.nominal.toString()
        try {
            data.user.let {
                Glide.with(this)
                        .load("https://image.freepik.com/free-vector/profile-icon-male-avatar-hipster-man-wear-headphones_48369-8728.jpg")
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading_image)
                        .into(binding.imgProfile)
                binding.txUsername.text = it.fullName
                binding.txNoPartner.text = "-"
            }
        }catch (e : Exception){}
    }

    private fun setupDataDummy(array: MutableList<BuktiTransferModel>) {
        photoAdapter.clear()
        array.forEach {
            photoAdapter.add(TransactionItem(this, it, object : TransactionItem.onCLick {
                override fun onRemove(datanya: BuktiTransferModel, position: Int) {
                    toast("$position")
                    photoAdapter.removeGroupAtAdapterPosition(position)
                    dataArray.removeAt(position)
                    setupDataDummy(dataArray)
                }
            }) {
                if (it.id == 0) {
                    if (dataArray.size <= 1){
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

    private fun setupRv() {
        val linearLayoutManager = GridLayoutManager(this, 4)
        binding.rvTransfer.apply {
            layoutManager = linearLayoutManager
            adapter = photoAdapter
        }

        val linearLayoutManagerPict = GridLayoutManager(this, 4)
        binding.rvTransferPict.apply {
            layoutManager = linearLayoutManagerPict
            adapter = photoImgAdapter
        }

        val linearLayoutManagerTalent = LinearLayoutManager(this)
        binding.rvTalent.apply {
            layoutManager = linearLayoutManagerTalent
            adapter = talentAdapter
        }
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

                        }) { Timber.e { it.message.toString() } })
    }

    private fun setupListener() {
        binding.btnSelesai.setOnClickListener {
            if (transactionType == 0){
                vm.updateStatusWithdraw(detailWithDraw!!.id).observe(this, {
                    when (it) {
                        is UiState.Loading -> setupLoadAnimation(true)
                        is UiState.Success -> {
                            setupLoadAnimation(false)
                            if (it.data.success) showDialogConfirm()
                            else createAlertError(this, "Permintaan gagal diproses!",it.data.message)
                        }
                        is UiState.Error -> setupLoadAnimation(false)
                    }
                })
            }else{
                if (validateForm()){
                    vm.updateStatusWithdraw(detailWithDraw!!.id).observe(this, {
                        when (it) {
                            is UiState.Loading -> setupLoadAnimation(true)
                            is UiState.Success -> {
                                if (it.data.success){
                                    vm.uploadAttachment(
                                            detailWithDraw!!.id,
                                            attachmentFile = compressedImage
                                    ).observe( this, {
                                        when (it) {
                                            is UiState.Loading -> {
                                                Log.d(TAG, "Loading upload...")
                                            }
                                            is UiState.Success -> {
                                                setupLoadAnimation(false)
                                                if (it.data.status){
                                                    showDialogConfirm()
                                                }else{
                                                    createAlertError(this, "Upload berkas gagal!","Berkas gagal diupload, silahkan coba lagi.", 3000)
                                                }
                                            }
                                            is UiState.Error -> setupLoadAnimation(false)
                                        }
                                    })
                                }else{
                                    setupLoadAnimation(false)
                                    createAlertError(this, "Permintaan gagal diproses!",it.data.message)
                                }
                            }
                            is UiState.Error -> setupLoadAnimation(false)
                        }
                    })
                }
            }
        }
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validateForm() : Boolean{
        var isChecked = true
        binding.let {
            if (dataArray.size == 1){
                it.rvTransfer.requestFocus()
                toast("Anda belum meng-upload bukti transaksi.")
                isChecked = false
            }
        }
        return isChecked
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
        }
        return photoFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = data.extras!!.get("data") as Bitmap

            compressedImage = convertBitmap(bitmap) as File
            compress(convertBitmap(bitmap))

//            membalikan posisi data array ke normal
            if (isReverse) {
                dataArray.reverse()
                isReverse = false
            }
            dataArray.add(BuktiTransferModel(dataArray.size, bitmap))

//            membalikan posisi data array
            if (!isReverse) {
                dataArray.reverse()
                isReverse = true
            }
            setupDataDummy(dataArray)
        }
    }

    private fun showDialogConfirm() {
        val dialog = MaterialDialog(this).show {
            cornerRadius(16f)
            customView(
                    R.layout.dialog_withdrawal_confirm,
                    scrollable = false,
                    horizontalPadding = true,
                    noVerticalPadding = true
            )
        }.cancelOnTouchOutside(false)
        val customView = dialog.getCustomView()
        val close = customView.findViewById<ImageView>(R.id.close)
        val btnOke = customView.findViewById<Button>(R.id.btn_ok)
        val txStatus = customView.findViewById<TextView>(R.id.textView14)
        if (transactionType == 0) txStatus.text = "Terimakasih, Poin berhasil di bagikan."

        btnOke.setOnClickListener {
            dialog.dismiss()
            startActivity<WithdrawListActivity>()
            finishAffinity()
        }
        close.setOnClickListener {
            dialog.dismiss()
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