package id.android.kmabsensi.presentation.point.penarikandetail

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityWithdrawalDetailBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import okhttp3.internal.notify
import org.jetbrains.anko.toast

class WithdrawalDetailActivity : BaseActivity() {

    private val binding by lazy { ActivityWithdrawalDetailBinding.inflate(layoutInflater) }
    private var selectedPhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 0
    private val photoAdapter = GroupAdapter<GroupieViewHolder>()
    var idx: Int = 1
    val dataArray : MutableList<BuktiTransferModel> = arrayListOf()
    private var isReverse = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupListener()
        setupRv()
        dataArray.add(BuktiTransferModel(0, null))
        setupDataDummy(dataArray)
    }

    private fun setupView() {
        if (intent.getIntExtra("typePenarikan", 0) == 0){
            binding.llKasihTalent.visible()
            binding.llTarikSaldo.gone()
        }else{
            binding.llKasihTalent.gone()
            binding.llTarikSaldo.visible()
        }
        binding.toolbar.txtTitle.text = resources.getString(R.string.text_penarikan_poin)
    }

    private fun setupDataDummy(array: MutableList<BuktiTransferModel>) {
        photoAdapter.clear()
        array.forEach {
            Log.d("TAGonRemove", "onRemove: $it")
            photoAdapter.add(WithDrawalItem(this, it, object : WithDrawalItem.onCLick {
                override fun onRemove(datanya: BuktiTransferModel, position: Int) {
                    toast("$position")
                    photoAdapter.removeGroupAtAdapterPosition(position)
                    photoAdapter.notifyDataSetChanged()
                    dataArray.removeAt(position)
                    setupDataDummy(dataArray)
                }
            }) {
                if (it.id == 0) {
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
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

    }

    private fun setupListener() {
        binding.btnSelesai.setOnClickListener {
            showDialogConfirm()
        }
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = data.extras!!.get("data") as Bitmap
            idx++

//            membalikan posisi data array ke normal
            if (isReverse) {
                dataArray.reverse()
                isReverse = false
            }
            dataArray.add(BuktiTransferModel(idx, bitmap))

//            membalikan posisi data array
            if (!isReverse){
                dataArray.reverse()
                isReverse = true
            }

            setupDataDummy(dataArray)
        }
    }

    private fun showDialogConfirm(){
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
        btnOke.setOnClickListener {
            dialog.dismiss()
        }
        close.setOnClickListener {
            dialog.dismiss()
        }
    }
}