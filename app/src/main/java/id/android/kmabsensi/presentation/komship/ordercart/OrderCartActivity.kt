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
    private val vm: OrderCartViewModel by inject()
    private lateinit var orderAdapter: OrderCartAdapter
    private var dataPartner : MutableList<KomPartnerItem> = ArrayList()

    private var listCartItem : MutableList<CartListItem> = ArrayList()
    private var myCart      = ArrayList<CartItem>()
    private var myCartEmpty = ArrayList<CartItem>()

    private var idPartner = 0
    private var sklList: SkeletonScreen? = null
    private var sklPartner: SkeletonScreen? = null
    private var position = 0
    private var isOnBackPress = false
    private var isUpdateQty = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Keranjang", isBackable = true, isDelete = true)
        setupView()
        setupObserve()
        setupList()
        setupListener()
    }

    private fun setupObserve() {

        vm.partnerState.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    showSkeleton()
                    Timber.tag("_cartState").d("onLoding")
                }
                is UiState.Success -> {
                    sklPartner?.hide()
                    if (it.data.code == 200){
                        dataPartner.clear()
                        dataPartner.addAll(it.data.data!!)
                        setupSpinnerPartner(dataPartner)
                        vm.cartState.observe(this, { state ->
                            when (state) {
                                is UiState.Loading -> {
                                    Timber.tag("_cartState").d("on Loading ")
                                }
                                is UiState.Success -> {
                                    sklList?.hide()
                                    if (state.data.code == 200){
                                        binding.apply {
                                            srListCart.isRefreshing = false
                                        }
                                        myCart.clear()
                                        myCart.addAll(state.data.data!!)
                                        setupListCartItem()
                                        setDataListCart(position)
                                    } else{
                                        toast("Data tidak berhasil di unduh, Coba lagi.")
                                    }
                                }
                                is UiState.Error -> {
                                    sklList?.hide()
                                    binding.srListCart.isRefreshing = false
                                    Timber.d(state.throwable)
                                }
                            }
                        })
                    } else {
                        toast("Data tidak berhasil di unduh, Coba lagi.")
                    }
                }
                is UiState.Error -> {
                    sklPartner?.hide()
                    Timber.d(it.throwable)
                }
            }
        })

        vm.deleteState.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    sklList?.show()
                    Timber.tag("_cartState").d("on Loading ")
                }
                is UiState.Success -> {
                    sklList?.hide()
                    if (it.data.code == 200){
                        removeChecked()
                        vm.GetDataCart()
                    } else{
                        toast("Data tidak berhasil di unduh, Coba lagi.")
                    }
                }
                is UiState.Error -> {
                    sklList?.hide()
                    Timber.d(it.throwable)
                }
            }
        })

        vm.updateQtyState.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    if (isOnBackPress){
                        showProgress()
                    }
                    Timber.tag("_cartState").d("onLoding ")
                }
                is UiState.Success -> {
                    if (it.data.code == 200){
                        if (isOnBackPress){
                            hideProgress()
                            super.onBackPressed()
                        } else {
                            orderPayment()
                        }
                    } else{
                        toast("Data tidak berhasil di unduh, Coba lagi.")
                    }
                }
                is UiState.Error -> {
                    hideProgress()
                    Timber.d(it.throwable)
                }
            }
        })
    }

    private fun orderPayment(){
        startActivity<DeliveryActivity>(
            "_dataOrder" to vm.getCheckedCartItem(listCartItem),
            "_idPartner" to idPartner
        )
    }
    private fun setupView() {
        idPartner = intent.getIntExtra("_idPartner", 0)
        vm.getPartner()
        vm.GetDataCart()
    }

    private fun setupListCartItem(){
        listCartItem.clear()
        myCart.forEach {
            listCartItem.add(CartListItem(it, false, false))
        }
    }

    private fun setupList() {
        orderAdapter =
            OrderCartAdapter(this, object : OrderCartAdapter.onAdapterListener {
                override fun onChecked(position: Int, isChecked: Boolean, product: CartItem) {
                    /**
                     * setup checked item
                     */
                    val index = vm.getCartPosition(myCart, product.cartId?:0)
                    val item = listCartItem[index]
                    listCartItem[index] = CartListItem(item.item, isChecked, item.isUpdate)
                    orderChecked()
                }

                override fun onUpdateQty(product: CartItem, qty: Int) {
                    product.apply {
                        /**
                         * update qty
                         */
                        isUpdateQty = true

                        val index = vm.getCartPosition(myCart, product.cartId?:0)
                        val item = listCartItem[index]
                        listCartItem[index] = CartListItem(item.item.copy(qty = qty), item.isChecked, true)

                        binding.tvTotalPayment.text = convertRupiah(vm.totalCost(listCartItem))
                    }
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
        if (data?.count()!! <= 1){
            data?.forEach {
                partner.add(it.partnerName!!)
            }
        }else{
            partner.add("Pilih Partner")
            data?.forEach {
                partner.add(it.partnerName!!)
            }
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
                            p: Int,
                            id: Long
                        ) {
                            position = p
                            if (p == 0){
                                binding.toolbar.tvDelete.gone()
                            }else{
                                binding.toolbar.tvDelete.visible()
                                setupListCartItem()
                            }
                            setDataListCart(position)
                        }
                    }
            }
    }

    private fun setDataListCart(posit: Int){
        if (posit != 0){
            idPartner = dataPartner[(posit - 1)].partnerId ?: 0
            if (vm.filterCart(myCart, idPartner).size < 1) {
                binding.tvEmptyCart.visible()
                binding.tvEmptyCart.text = "Kamu belum memiliki\\nproduk di keranjang"
            } else {
                binding.tvEmptyCart.gone()
                orderChecked()
            }
            orderAdapter.setData(vm.filterCart(myCart, idPartner))
        }else{
            if (dataPartner.count() <= 1){
                idPartner = dataPartner[posit].partnerId ?: 0
                orderAdapter.setData(vm.filterCart(myCart, idPartner))
            }else{
            orderAdapter.setData(myCartEmpty)
            binding.tvEmptyCart.apply {
                visible()
                text = "Anda belum memilih partner"}
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            srListCart.setOnRefreshListener {
                srListCart.isRefreshing = true
                setupView()
            }

            toolbar.tvDelete.setOnClickListener {
                if (vm.countCheckedItem(listCartItem) > 0) {
                    vm.DeleteCart(vm.getCartId(listCartItem))
                } else {
                    toast(getString(R.string.anda_belum_memilih_item))
                }
            }

            btnOrder.setOnClickListener {
                if (vm.countCheckedItem(listCartItem) > 0) {
                    isOnBackPress = false
                    if (!isUpdateQty){
                        orderPayment()
                    } else{
                        vm.updateListQtyCart(vm.getUpdateQtyParams(listCartItem))
                    }
                }
            }
        }
    }

    /** set checked list*/
    fun orderChecked() {
        val textBtnOrder = "Order (${vm.countCheckedItem(listCartItem)})"
        binding.btnOrder.text = textBtnOrder

        if (vm.countCheckedItem(listCartItem) > 0) {
            binding.btnOrder.apply {
                setBackgroundResource(R.drawable.bg_orange_10dp)
                isEnabled = true
                isClickable = true
            }
        } else {
            binding.btnOrder.apply {
                setBackgroundResource(R.drawable.bg_grey_8dp)
                isEnabled = false
                isClickable = false
            }
        }
        binding.tvTotalPayment.text = convertRupiah(vm.totalCost(listCartItem))

    }

    private fun removeChecked() {
        setupListCartItem()
        orderChecked()
    }

    private fun showSkeleton() {
        if (sklList == null) {
            sklList = Skeleton.bind(binding.rvOrderCart)
                .adapter(orderAdapter)
                .load(R.layout.skeleton_list_cart)
                .show()
            sklPartner = Skeleton.bind(binding.spPartner)
                .load(R.layout.skeleton_item_small)
                .show()
        } else {
            sklList?.show()
            sklPartner?.show()
        }
    }

    override fun onBackPressed() {
        isOnBackPress = true
        vm.updateListQtyCart(vm.getUpdateQtyParams(listCartItem))
    }
}