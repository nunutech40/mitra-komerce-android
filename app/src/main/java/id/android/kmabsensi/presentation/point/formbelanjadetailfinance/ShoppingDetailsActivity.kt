package id.android.kmabsensi.presentation.point.formbelanjadetailfinance

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityShoppingDetailsBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.point.formbelanjadetailfinance.adapter.ShoppingItem
import id.android.kmabsensi.presentation.point.formbelanjadetailfinance.adapter.TalentItem
import id.android.kmabsensi.presentation.point.formbelanjadetailfinance.adapter.WithDrawalShoppingItem
import id.android.kmabsensi.presentation.point.penarikandetail.BuktiTransferModel
import org.jetbrains.anko.toast

class ShoppingDetailsActivity : BaseActivity() {
    private var selectedPhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 0
    private val photoAdapter = GroupAdapter<GroupieViewHolder>()
    private val talentAdapter = GroupAdapter<GroupieViewHolder>()
    private val shoppingAdapter = GroupAdapter<GroupieViewHolder>()
    var idx: Int = 1
    val dataPhoto: MutableList<BuktiTransferModel> = arrayListOf()
    val datatalent: MutableList<TalentModel> = arrayListOf()
    val dataShopping: MutableList<ShoppingModel> = arrayListOf()
    private var isReverse = false
    private val binding by lazy { ActivityShoppingDetailsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setuView()
        setupListener()
        setupRv()
        dataDummy()
        /*
        first data(dataPhoto) used for button take pict
         */
        dataPhoto.add(BuktiTransferModel(0, null))
        setupDataDummy(dataPhoto)
    }

    private fun dataDummy() {
        for (index in 0..3) {
            datatalent.add(TalentModel(index, "name $index", "posisi $index"))
            talentAdapter.add(TalentItem(this, datatalent[index]) { })
        }
        for (index in 0..4) {
            dataShopping.add(ShoppingModel(index, "tool $index", "$index 000 000"))
            shoppingAdapter.add(ShoppingItem(this, dataShopping[index]) {
                toast("move to edit page")
            })
        }
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

//        list Shopping
        val shoppingLayoutManager = LinearLayoutManager(this)
        binding.rvTools.apply {
            layoutManager = shoppingLayoutManager
            adapter = shoppingAdapter
        }
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSelesai.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setuView() {
        binding.toolbar.txtTitle.text = getString(R.string.text_detail_belanja)
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
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
//            get data photo after take pict
            selectedPhotoUri = data.data
            val bitmap = data.extras!!.get("data") as Bitmap
            idx++
//            membalikan posisi data array ke normal
            if (isReverse) {
                dataPhoto.reverse()
                isReverse = false
            }
            dataPhoto.add(BuktiTransferModel(idx, bitmap))
//            membalikan posisi data array
            if (!isReverse) {
                dataPhoto.reverse()
                isReverse = true
            }
            setupDataDummy(dataPhoto)
        }
    }
}