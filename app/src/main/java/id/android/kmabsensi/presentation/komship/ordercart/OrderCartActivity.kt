package id.android.kmabsensi.presentation.komship.ordercart

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.komship.DeleteParams
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.databinding.ActivityOrderCartBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.delivery.DeliveryActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.convertRupiah
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
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
    private val isDirectOrder by lazy {
        intent.getBooleanExtra("_isDirectOrder", false)
    }

    private val listChecked: MutableList<Int> = ArrayList()
    private val listId : ArrayList<Int> = ArrayList()

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
                           orderChecked()
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
                    vm.GetDataCart()
                }
                is UiState.Error -> Log.d(TAG, "on Error ${it.throwable}")
            }
        })

        vm.updateQtyState.observe(this, {
            when (it) {
                is UiState.Loading -> Log.d(TAG, "onLoding ")
                is UiState.Success -> {
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
        orderAdapter = OrderCartAdapter(this, isDirectOrder, object : OrderCartAdapter.onAdapterListener{
            override fun onChecked(position: Int, isChecked : Boolean, product: CartItem) {
                if (isChecked){
                    listChecked.add(position)
                    listId.add(product.cartId!!)
                    Log.d("ListId", listId.toString())
                }else{
                    listChecked.removeAt(listChecked.indexOf(position))
                }

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
                        Log.d("listidchecked", list.toString())
                    }
                    vm.DeleteCart(list)
                }else{
                    toast("Anda belum memilih data yang akan dihapus.")
                }
            }

            btnOrder.setOnClickListener {
                startActivity<DeliveryActivity>(
                    "_dataOrder" to checked
                )
            }
        }
    }


    fun orderChecked(){
        if (listChecked.size != 0){
            Log.d("listChecked", listChecked.toString())
            binding.btnOrder.text = "Order (${listChecked.size})"
            if (vm.validateOrderChecked(checked).size > 0) binding.btnOrder.setBackgroundResource(R.drawable.bg_orange_10dp)
            else binding.btnOrder.setBackgroundResource(R.drawable.bg_grey_8dp)
            var total = 0

            listChecked.forEach {
                total = total+myCart[it].subtotal!!
            }
            binding.tvTotalPayment.text = convertRupiah(total.toDouble())
        }
    }

}