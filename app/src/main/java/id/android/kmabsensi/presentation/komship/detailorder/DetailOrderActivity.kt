package id.android.kmabsensi.presentation.komship.detailorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import id.android.kmabsensi.databinding.ActivityDetailOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailOrderActivity : BaseActivityRf<ActivityDetailOrderBinding>(
    ActivityDetailOrderBinding::inflate
) {
    lateinit var datePick: Date
    private lateinit var productAdapter : ProductDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Pengiriman", isBackable = true)
        setupList()
        setupListener()
    }

    private fun setupListener() {
        binding.apply {
            tieDate.setOnClickListener {
                MaterialDialog(this@DetailOrderActivity).show {
                    datePicker { dialog, date ->
                        // Use date (Calendar)
                        dialog.dismiss()

                        datePick = date.time
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateSelected: String = dateFormat.format(date.time)
                        Log.d("_datePick", "datePick : $datePick dam dateSelected $dateSelected")
                        setDateFrom(dateSelected)
                    }
                }
            }
        }
    }

    private fun setDateFrom(dateSelected: String) {
        binding.tieDate.setText(dateSelected)
    }

    private fun setupList() {
        productAdapter = ProductDetailAdapter(this, object : ProductDetailAdapter.onadapterListener{
            override fun onClick(data: DetailProductSementara) {
                TODO("Not yet implemented")
            }

        })
        binding.rvOrder.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@DetailOrderActivity)
            setHasFixedSize(true)
        }
        productAdapter.setData(dummyList())
    }

    private fun dummyList(): List<DetailProductSementara> {
        val list: MutableList<DetailProductSementara> = ArrayList()
        for (idx in 0..2){
            list.add(DetailProductSementara("https://png.pngtree.com/png-vector/20191031/ourlarge/pngtree-t-shirt-and-hanger-vector-illustration-isolated-on-white-background-clothes-png-image_1928833.jpg",
            "Baju Polos $idx",
            "variant ke $idx",
            "${idx}000.000",
            "$idx Barang (${idx}00gr)"))
        }
        return list
    }
}