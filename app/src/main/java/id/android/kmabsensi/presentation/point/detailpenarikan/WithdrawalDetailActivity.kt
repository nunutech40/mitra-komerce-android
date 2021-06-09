package id.android.kmabsensi.presentation.point.detailpenarikan

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityWithdrawalDetailBinding
import org.jetbrains.anko.toast

class WithdrawalDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityWithdrawalDetailBinding.inflate(layoutInflater) }
    private var selectedPhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 0
    private val photoAdapter = GroupAdapter<GroupieViewHolder>()
    var idx: Int = 1
    val dataArray : ArrayList<BuktiTransferModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListener()
        setupRv()
        setupDataDummy()
    }

    private fun setupDataDummy() {
        dataArray.add(BuktiTransferModel(0, null))
        photoAdapter.add(WithDrawalItem(this, dataArray[0], object : WithDrawalItem.onCLick{
            override fun onRemove(position: Int) {
                photoAdapter.removeGroupAtAdapterPosition(position)
            }
        }){
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        })
    }

    private fun setupRv() {
        val linearLayoutManager = GridLayoutManager(this, 3)
        binding.rvTransfer.apply {
            layoutManager = linearLayoutManager
            adapter = photoAdapter
        }

    }

    private fun setupListener() {
        binding.btnSelesai.setOnClickListener {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = data.extras!!.get("data") as Bitmap
            dataArray.add(BuktiTransferModel(idx, bitmap))
            Log.d("_registration", "Photo was selected, with location :  ${dataArray.size}")
            idx++
            photoAdapter.clear()
//            dataArray.res
            for (index in dataArray.size ..0){
                Log.d("_registration", "Photo was selected, with location :  ${dataArray.size}")
                photoAdapter.add(WithDrawalItem(this, dataArray[index-1], object : WithDrawalItem.onCLick{
                    override fun onRemove(position: Int) {
                        photoAdapter.removeGroupAtAdapterPosition(position)
                    }
                }){ })
            }
        }
    }
}