package id.android.kmabsensi.presentation.komship.ordercart

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.data.remote.response.komship.DataProductItem
import id.android.kmabsensi.databinding.ActivityOrderCartBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderCartActivity : BaseActivityRf<ActivityOrderCartBinding>(
    ActivityOrderCartBinding::inflate
) {
    private val TAG = "_cartState"
    private val vm : OrderCartViewModel by inject()
    private var checked : MutableList<ValidateChecked> = ArrayList()
    private lateinit var orderAdapter : OrderCartAdapter
    private var myCart = ArrayList<CartItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Pengiriman", isBackable = true, isDelete = true)
        setupView()
        setupObserve()
        setupList()
        setupListener()
    }

    private fun setupObserve(){
        vm.cartState.observe(this, {
            when (it) {
                is UiState.Loading -> Log.d(TAG, "on Loading ")
                is UiState.Success -> {
                    myCart.clear()
                    myCart.addAll(it.data.data!!)
                    binding.apply {
                       if (myCart.size<1){
                            tvEmptyCart.visible()
                       } else{
                           tvEmptyCart.gone()
                           orderAdapter.setData(myCart)
                       }
                    }
                }
                is UiState.Error -> Log.d(TAG, "on Error ${it.throwable}")
            }
        })
        vm.deleteState.observe(this, {
            when(it){
                is UiState.Loading -> Log.d(TAG, "on Loading ")
                is UiState.Success -> {
                    Log.d(TAG, "on Success ${it.data}")
                    vm.GetDataCart()
                }
                is UiState.Error -> Log.d(TAG, "on Error ${it.throwable}")
            }
        })
        vm.updateQtyState.observe(this, {
            when (it) {
                is UiState.Loading -> Log.d(TAG, "onLoding ")
                is UiState.Success -> {
                    Log.d(TAG, "on Success ${it.data}")
                    vm.GetDataCart()
                }
                is UiState.Error -> Log.d(TAG, "on Error ")
            }
        })
    }

    private fun setupView(){
        vm.GetDataCart()
    }

    private fun setupList() {
        orderAdapter = OrderCartAdapter(this, object : OrderCartAdapter.onAdapterListener{
            override fun onChecked(position: Int, isChecked : Boolean, product: CartItem) {
                if(isChecked){
                    checked.add(ValidateChecked(product, position, isChecked))
                }else{
                    checked.remove(ValidateChecked(product, position, true))
                }
                orderChecked()
            }

            override fun onUpdateQty(product: CartItem, qty: Int) {
                vm.UpdateQtyCart(product.cartId!!, qty)
            }
        })
        binding.rvOrderCart.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(this@OrderCartActivity)
            setHasFixedSize(true)
        }
    }

    private fun setupListener(){
        binding.apply {
            toolbar.tvDelete.setOnClickListener {
                val list: ArrayList<Int> = ArrayList()
                if (checked.size > 0){
                    checked.forEach {
                        list.add(it.item.cartId!!)
                    }
                    vm.DeleteCart(list)
                }else{
                    toast("Anda belum memilih data yang akan dihapus.")
                }
            }
        }
    }


    fun orderChecked(){
        binding.btnOrder.text = "Order (${vm.validateOrderChecked(checked).size})"

        if (vm.validateOrderChecked(checked).size > 0) binding.btnOrder.setBackgroundResource(R.drawable.bg_orange_10dp)
        else binding.btnOrder.setBackgroundResource(R.drawable.bg_grey_8dp)
        var total = 0
        checked.forEach {
            total += it.item.subtotal!!
        }
        binding.tvTotalPayment.text = rupiah(total.toDouble())
    }

    fun rupiah(number: Double): String{
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).toString()
    }
}