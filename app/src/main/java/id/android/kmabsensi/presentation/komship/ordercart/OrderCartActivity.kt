package id.android.kmabsensi.presentation.komship.ordercart

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
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
    private var idPartner   = 0
    private val listChecked = ArrayList<Int>()
    private val listId : ArrayList<Int> = ArrayList()
    private var dataPartner: MutableList<KomPartnerItem> = ArrayList()
    private var sklList: SkeletonScreen? = null
    private var sklPartner: SkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Keranjang", isBackable = true, isDelete = true)
        setupView()
        setupObserve()
        if (isDirectOrder && myCart.size != 0){
            setupDirectOrder()
        }
        setupList()
        setupListener()
    }

    private fun setupObserve(){
        vm.cartState.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    Timber.tag(TAG).d("on Loading ")
                }
                is UiState.Success -> {
                    sklList?.hide()
                    binding.apply {
                        srListCart.isRefreshing = false
                        if (myCart.size<1){
                            tvEmptyCart.visible()
                       } else{
                           tvEmptyCart.gone()
                           orderChecked()
                       }
                    }
                    myCart.clear()
                    myCart.addAll(it.data.data!!)
                    orderAdapter.setData(myCart)
                }
                is UiState.Error -> {
                    sklList?.hide()
                    binding.srListCart.isRefreshing = false
                    Timber.d(it.throwable)
                }
            }
        })

        vm.deleteState.observe(this, {
            when(it){
                is UiState.Loading -> {
                    sklList?.show()
                    Timber.tag(TAG).d("on Loading ")
                }
                is UiState.Success -> {
                    sklList?.hide()
                    removeChecked()
                    vm.GetDataCart()
                }
                is UiState.Error -> {
                    sklList?.hide()
                    Timber.d(it.throwable)
                }
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
                is UiState.Loading ->{
                    showSkeleton()
                    Timber.tag(TAG).d("onLoding ")
                }
                is UiState.Success -> {
                    sklPartner?.hide()
                    dataPartner.addAll(it.data.data!!)
                    setupSpinnerPartner(it.data.data)
                    // todo : testing
                    binding.spPartner.setSelection(vm.getPartnerPosition(dataPartner, idPartner))
                }
                is UiState.Error -> {
                    sklPartner?.hide()
                    Timber.d(it.throwable)
                }
            }
        })
    }

    private fun setupView(){
        idPartner = intent.getIntExtra("_idPartner", 0)
        vm.getPartner()
        vm.GetDataCart()
    }

    private fun setupDirectOrder(){
        listChecked.add(0)
        listId.add(myCart[0].cartId!!)
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
                            idPartner = dataPartner[position].partnerId!!

                            if (vm.filterCart(myCart, idPartner).size<=0){
                                binding.tvEmptyCart.visible()
                            }else binding.tvEmptyCart.gone()

                            orderAdapter.setData(vm.filterCart(myCart, idPartner))
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
                if (checked.size > 0 && idPartner > 0){
                    val list: ArrayList<Int> = ArrayList()
                    checked.forEach {
                        list.add(it.item.cartId!!)
                    }
                    startActivity<DeliveryActivity>(
                        "_dataOrder" to checked,
                        "_idPartner" to idPartner
                    )
                }
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

    private fun showSkeleton(){
        if (sklList == null){
            sklList = Skeleton.bind(binding.rvOrderCart)
                .adapter(orderAdapter)
                .load(R.layout.skeleton_list_cart)
                .show()
            sklPartner = Skeleton.bind(binding.spPartner)
                .load(R.layout.skeleton_item_small)
                .show()
        }else{
            sklList?.show()
            sklPartner?.show()
        }
    }
}