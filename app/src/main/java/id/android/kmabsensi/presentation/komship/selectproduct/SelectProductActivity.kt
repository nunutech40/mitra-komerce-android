package id.android.kmabsensi.presentation.komship.selectproduct

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.data.remote.response.komship.ProductKomItem
import id.android.kmabsensi.databinding.ActivitySelectProductBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf

class SelectProductActivity : BaseActivityRf<ActivitySelectProductBinding>(
    ActivitySelectProductBinding::inflate
){

    private val listProduct by lazy {
        intent.getParcelableArrayListExtra<ProductKomItem>("_productByPartner")
    }
    private lateinit var productAdapter: SelectProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar("Pilih Produk", isBackable = true)
        setupList()
        setupListener()
        Log.d("_listProduct", "listProduct: $listProduct")
    }

    private fun setupListener(){
        binding.apply {
            etSearchProduct.doAfterTextChanged {
                productAdapter.filter.filter(it.toString())
            }
        }
    }

    private fun setupList() {
        productAdapter = SelectProductAdapter(
            this,
            object : SelectProductAdapter.onAdapterListener {
                override fun onCLick(data: ProductKomItem) {
                    val intent = Intent()
                    intent.putExtra("_productByPartner", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            })
        binding.rvProduct.apply {
            adapter = productAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SelectProductActivity)
        }
        productAdapter.setData(listProduct!!)
    }
}