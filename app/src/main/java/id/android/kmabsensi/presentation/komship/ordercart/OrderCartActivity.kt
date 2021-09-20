package id.android.kmabsensi.presentation.komship.ordercart

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.komship.DataProductItem
import id.android.kmabsensi.databinding.ActivityOrderCartBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class OrderCartActivity : BaseActivityRf<ActivityOrderCartBinding>(
    ActivityOrderCartBinding::inflate
) {
    private val listProduct : MutableList<DataProductItem> = ArrayList()
    private val vm : OrderCartViewModel by inject()
    private var checked : MutableList<ValidateChecked> = ArrayList()
    private lateinit var orderAdapter : OrderCartAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Pengiriman", isDelete = true)
        dummyData()
        setupList()
        setupListener()
    }

    private fun setupList() {
        orderAdapter = OrderCartAdapter(this, object : OrderCartAdapter.onAdapterListener{
            override fun onChecked(position: Int, isChecked : Boolean, product: DataProductItem) {
                checked.add(ValidateChecked(product.productId!!, position, isChecked))
                orderChecked()
            }
        })
        binding.rvOrderCart.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(this@OrderCartActivity)
            setHasFixedSize(true)
        }
        orderAdapter.setData(listProduct)
    }

    private fun setupListener(){
        binding.apply {
            toolbar.tvDelete.setOnClickListener {
                vm.validateOrderChecked(checked).forEach {
                    if (it.isChecked) {
                    }
                }
            }
        }
    }

    private fun dummyData(){
        for (idx in 0..5){
            listProduct.add(DataProductItem(productImages = "https://img.beritasatu.com/cache/beritasatu/620x350-2/1597505997.jpg",
             price = idx*1000000,
            productId = idx,
            stock = 10,
            productName = "Nike Air Jordan"))
        }
    }



    fun orderChecked(){
        binding.btnOrder.text = "Order (${vm.validateOrderChecked(checked).size})"

        if (vm.validateOrderChecked(checked).size > 0) binding.btnOrder.setBackgroundResource(R.drawable.bg_orange_10dp)
        else binding.btnOrder.setBackgroundResource(R.drawable.bg_grey_8dp)

    }
}