package id.android.kmabsensi.presentation.komship.ordercart

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
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

    private val listChecked = ArrayList<Int>()
    private val listId : ArrayList<Int> = ArrayList()

    private var dataPartner: MutableList<KomPartnerItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Keranjang", isBackable = true, isDelete = true)
        setupView()
        setupObserve()
        setupList()
        setupListener()
    }

    private fun setupObserve(){
        vm.cartState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag(TAG).d("on Loading ")
                is UiState.Success -> {
                    myCart.clear()
                    myCart.addAll(it.data.data!!)
                    binding.apply {
                        srListCart.isRefreshing = false
                        if (myCart.size<1){
                            tvEmptyCart.visible()
                       } else{
                           tvEmptyCart.gone()
//                           orderAdapter.setData(myCart)
                           orderChecked()
                       }
                    }
                }
                is UiState.Error -> {
                    binding.srListCart.isRefreshing = false
                    Timber.d(it.throwable)
                }
            }
        })

        vm.deleteState.observe(this, {
            when(it){
                is UiState.Loading -> Timber.tag(TAG).d("on Loading ")
                is UiState.Success -> {
                    removeChecked()
                    vm.GetDataCart()
                }
                is UiState.Error -> Timber.d(it.throwable)

            }
        })

        vm.updateQtyState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag(TAG).d("onLoding ")
                is UiState.Success -> {
                    vm.GetDataCart()
                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })

        vm.partnerState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag(TAG).d("onLoding ")
                is UiState.Success -> {
                    dataPartner.addAll(it.data.data!!)
                    setupSpinnerPartner(it.data.data)
                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })
    }

    private fun setupView(){
        vm.getPartner()
        vm.GetDataCart()
    }

    private fun setupList() {
        orderAdapter = OrderCartAdapter(this, isDirectOrder, object : OrderCartAdapter.onAdapterListener{
            override fun onChecked(position: Int, isChecked : Boolean, product: CartItem) {

                if (isChecked){
                    /** set position checked*/
                    listChecked.add(position)
                    listId.add(product.cartId!!)
                }else{
                    listId.remove(product.cartId!!)
                    listChecked.remove(position)
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

    private fun setupSpinnerPartner(data: List<KomPartnerItem>?) {
        val partner = ArrayList<String>()
        data?.forEach {
            partner.add(it.partnerName!!)
        }

        ArrayAdapter(this, R.layout.spinner_item, partner)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                binding.spPartner.adapter = adapter

                binding.spPartner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            orderAdapter.setData(vm.filterCart(myCart, dataPartner[position].partnerId!!))
                        }
                    }
            }
    }

    private fun setupListener(){
        binding.apply {

            srListCart.setOnRefreshListener {
                srListCart.isRefreshing = true
                setupView()
            }

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

            btnOrder.setOnClickListener {
                startActivity<DeliveryActivity>(
                    "_dataOrder" to checked
                )
            }
        }
    }

    /** set checked list*/
    fun orderChecked(){
        binding.btnOrder.text = "Order (${listChecked.size})"
        if (vm.validateOrderChecked(checked).size > 0) {
            binding.btnOrder.apply {
                setBackgroundResource(R.drawable.bg_orange_10dp)
                isEnabled = true
                isClickable = true
            }
        }
        else {
            binding.btnOrder.apply {
                setBackgroundResource(R.drawable.bg_grey_8dp)
                isEnabled = false
                isClickable = false
            }
        }
        var total = 0
        listChecked.forEach {
            total += myCart[it].subtotal!!
        }
        binding.tvTotalPayment.text = convertRupiah(total.toDouble())
    }

    private fun removeChecked(){
        checked.clear()
        listChecked.clear()
        listId.clear()
        orderChecked()
    }
}