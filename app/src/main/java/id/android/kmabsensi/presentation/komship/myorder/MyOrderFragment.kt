package id.android.kmabsensi.presentation.komship.myorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.komship.AddCartParams
import id.android.kmabsensi.data.remote.response.komship.*
import id.android.kmabsensi.databinding.FragmentMyOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderActivity
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.presentation.komship.delivery.DeliveryActivity
import id.android.kmabsensi.presentation.komship.ordercart.OrderCartActivity
import id.android.kmabsensi.presentation.komship.ordercart.ValidateChecked
import id.android.kmabsensi.presentation.komship.selectproduct.SelectProductActivity
import id.android.kmabsensi.utils.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class MyOrderFragment : BaseFragmentRf<FragmentMyOrderBinding>(
    FragmentMyOrderBinding::inflate
) {

    private val vm: MyOrderViewModel by inject()
    private var totalProduct = 1
    private var maxProduct = 0
    private val productKey = "_productByPartner"
    private var dataPartner: MutableList<KomPartnerItem> = ArrayList()
    private var dataProduct: ArrayList<ProductKomItem> = ArrayList()
    private lateinit var dataProductItem: ProductKomItem
    private var variantSize = 0
    private lateinit var dataOrder: AddCartParams
    private lateinit var productVariantSelect: ProductVariantKomItem
    private var isActive = false
    private var isDirectOrder = false
    private var idPartner = 0
    /** variantName used to collect variant name from option varian */
    private var variantName = ArrayList<String>()
    private var sklPartner: SkeletonScreen? = null
    private var sklProduct: SkeletonScreen? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        vm.addCartState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> {
                    binding?.apply {
                        progressBar.visible()
                        btnOrder.disableButton(false)
                    }
                    Timber.tag("_addCartState").d("on loading")
                }
                is UiState.Success -> {
                    binding?.apply {
                        btnOrder.disableButton(true)
                        progressBar.gone()
                    }
                    if (it.data.code == 200){
                        if (isDirectOrder) {
                            val cartItem = it.data.data
                            val order = ArrayList<CartItem>()
                            order.add(cartItem!!)
                            requireActivity().startActivity<DeliveryActivity>(
                                "_dataOrder" to order,
                                "_idPartner" to idPartner
                            )
                        } else {
                            requireActivity().toast(getString(R.string.data_cart_berhasil_ditambahkan))
                        }
                        updateQTY("reset")
                        resetForm()
                        (activity as MyOrderActivity).refreshCart()
                    } else{
                        requireActivity().toast("Order gagal dibuat, Coba lagi")
                    }
                }
                is UiState.Error -> {
                    binding?.apply {
                        btnOrder.disableButton(true)
                        progressBar.gone()
                    }
                    Timber.d(it.throwable)
                }
            }
        })

        vm.partnerState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> {
                    showSkeleton()
                    Timber.tag("_partnerState").d("on loading")
                }
                is UiState.Success -> {
                    binding?.srMyOrder?.isRefreshing = false
                    sklPartner?.hide()
                    if (it.data.code == 200){
                        dataPartner.addAll(it.data.data!!)
                        setupSpinnerPartner(it.data.data)
                    } else {
                        requireActivity().toast("Partner gagal dimuat, Coba lagi")
                    }
                }
                is UiState.Error -> {
                    binding?.srMyOrder?.isRefreshing = false
                    sklPartner?.hide()
                    Timber.tag("_partnerState").d("on error ${it.throwable}")
                }
            }
        })

        vm.productState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> {
                    showSkeletonProduct()
                    Timber.tag("_productState").d("on loading")
                }
                is UiState.Success -> {
                    sklProduct?.hide()
                    dataProduct.clear()
                    dataProduct.addAll(it.data.data!!)
                }
                is UiState.Error -> {
                    sklProduct?.hide()
                    Timber.tag("_productState").d(it.throwable)
                }
            }
        })
    }

    private fun setupContentProduct(data: ProductKomItem) {
        binding?.apply {
            llDetailProduct.visible()
            variantSize = data.variant?.size ?: 0
//            imgProduct.loadImageFromUrl(data.productImage ?: URL_SHOPPING_EMPTY )
            if (data.productImage.equals("")){
                imgProduct.loadImageFromUrl(URL_SHOPPING_EMPTY)
            }else{
                imgProduct.loadImageFromUrl(data.productImage.toString())
            }
            tvNameProduct.text = data.productName ?: "-"
            tvPrice.text = convertRupiah((data.price ?: 0).toDouble())
//            val productAvailable = "Tersedia: ${data.stock} Pcs"
            tvAvailableProduct.text = "Silahkan pilih varian yang ada"
            setupChip(data.variant!![0], 0)
        }
    }

    private fun setupSpinnerPartner(data: List<KomPartnerItem>?) {
        val partner = ArrayList<String>()
        if (data?.count()!! > 1){
            partner.add("Pilih Partner")
            data?.forEach {
                partner.add(it.partnerName ?: "-")
            }
        }else{
            data?.forEach {
                partner.add(it.partnerName ?: "-")
            }
        }
        ArrayAdapter(requireContext(), R.layout.spinner_item, partner)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                binding?.spPartner?.adapter = adapter

                binding?.spPartner?.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (data?.count()!! <= 1 && position == 0){
                                binding?.tilProduk?.disableForm(true)
                                idPartner = dataPartner[position].partnerId ?: 0
                                if (idPartner != 0){
                                    vm.getProduct(idPartner)
                                }
                            }else if(data?.count()!! > 1 && position != 0){
                                binding?.tilProduk?.disableForm(true)
                                idPartner = dataPartner[(position-1)].partnerId ?: 0
                                if (idPartner != 0){
                                    vm.getProduct(idPartner)
                                }
                            }else binding?.tilProduk?.disableForm(false)
                        }
                    }
            }
    }

    private fun setupView() {
        binding?.btnCart?.isClickable = false
        vm.getPartner()
    }

    private fun setupListener() {

        binding?.apply {

            srMyOrder.setOnRefreshListener {
                srMyOrder.isRefreshing = true
                setupView()
            }

            btnPlus.setOnClickListener {
                if (vm.validateMaxProduct(totalProduct, maxProduct)) {
                    updateQTY("plus")
                } else {
                    requireActivity().toast("sudah mencapai batas max")
                }
            }

            btnCart.setOnClickListener {
                dataOrder = vm.getDataOrderParam(
                    idPartner,
                    dataProductItem,
                    productVariantSelect,
                    vm.variantName(variantName),
                    totalProduct
                )
                vm.addCart(dataOrder)
                isDirectOrder = false
            }

            btnOrder.setOnClickListener {
                dataOrder = vm.getDataOrderParam(
                    idPartner,
                    dataProductItem,
                    productVariantSelect,
                    vm.variantName(variantName),
                    totalProduct
                )
                vm.addCart(dataOrder)
                isDirectOrder = true
            }

            btnMinus.setOnClickListener {
                if (vm.validateMinProduct(totalProduct)) {
                    updateQTY("minus")
                } else {
                    requireActivity().toast("sudah mencapai batas min")
                }
            }

            tieProduk.setOnClickListener {
                if (!dataPartner.isNullOrEmpty()) {
                    val intent = Intent(requireContext(), SelectProductActivity::class.java)
                    intent.putParcelableArrayListExtra(productKey, dataProduct)
                    startResult.launch(intent)
                } else requireActivity().toast("Anda belum memilih Partner")
            }

            /** variant Size = total variant is availeble in product */
            chipVarian1.setOnCheckedChangeListener { _, checkedId ->
                setupChipChangeListener(checkedId, 1)
            }

            chipVarian2.setOnCheckedChangeListener { _, checkedId ->
                setupChipChangeListener(checkedId, 2)
            }

            chipVarian3.setOnCheckedChangeListener { _, checkedId ->
                setupChipChangeListener(checkedId, 3)
            }

            chipVarian4.setOnCheckedChangeListener { _, checkedId ->
                setupChipChangeListener(checkedId, 4)
            }

            chipVarian5.setOnCheckedChangeListener { _, checkedId ->
                setupChipChangeListener(checkedId, 5)
            }

        }
    }

    private fun hiddenVariant(position: Int) {
        binding?.apply {
            vm.hiddenVariant(position, llVariant2, llVariant3, llVariant4, llVariant5)
        }
    }

    private fun lastOption(last: Boolean, optionsId: Int) {
        Log.d("onChipID", "lastOption: $optionsId")
        if (last) {
            if (optionsId == -1){
                binding?.apply {
                    tvAvailableProduct.text = "Silahkan pilih varian yang ada"
                    llQty.gone()
                    btnMinus.isEnabled = false
                    btnPlus.isEnabled = false
                    imgCart.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_orderku_off_rf
                        )
                    )
                    btnOrder.setBackgroundResource(R.drawable.bg_grey_8dp)
                }
            }else{
                binding?.llQty?.visible()
            }
            binding?.nsProduct?.smoothScrollTo(0, binding?.nsProduct?.getChildAt(0)!!.height)
            dataProductItem.productVariant?.forEach {
                if (it.optionId == optionsId) {
                    binding?.apply {
                        setupButton(true)
                        val productName = "${dataProductItem.productName} - ${it.name}"
                        var availableProduct = ""
                        if (it.stock!! <= 0 ) {
                            availableProduct = "Stock habis"
                            btnMinus.isEnabled = false
                            btnPlus.isEnabled = false
                            imgCart.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_orderku_off_rf
                                )
                            )
                            btnOrder.setBackgroundResource(R.drawable.bg_grey_8dp)
                            tvTotal.text = "habis"
                        }else availableProduct = "Tersedia: ${it.stock} Pcs"
                        maxProduct = it.stock ?: 0
                        tvNameProduct.text = productName
                        tvAvailableProduct.text = availableProduct
                        tvPrice.text = convertRupiah((it.price ?: 0).toDouble())

                        btnOrder.setBackgroundResource(R.drawable.bg_orange_10dp)
                        imgCart.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_orderku_rf
                            )
                        )
                        productVariantSelect = it
                    }
                }
            }
        } else {
            if (optionsId == -1){
                binding?.apply {
                    llVariant2.visibility = View.GONE
                    tvAvailableProduct.text = "Silahkan pilih varian yang ada"
                }
            }
            binding?.apply {
                setupButton(false)
                imgCart.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_orderku_off_rf
                    )
                )
                btnOrder.setBackgroundResource(R.drawable.bg_grey_8dp)
                llQty.gone()
            }
        }
    }

    private val startResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                dataProductItem = it.data?.getParcelableExtra(productKey)!!
                setupContentProduct(dataProductItem)
                hiddenVariant(0)
                binding?.tieProduk?.text = dataProductItem.productName?.toEditable()
                setupButton(false)
            }
        }

    private fun resetForm() {
        binding?.apply {
            llDetailProduct.gone()
            hiddenVariant(0)
            llVariant1.gone()
            tieProduk.text = "".toEditable()
            lastOption(false, 0)
        }
    }

    private fun setupButton(isClicable: Boolean) {
        binding?.apply {
            isActive = isClicable
            btnCart.isClickable = isClicable
            btnOrder.isEnabled = isClicable
        }
    }

    private fun createChipItem(
        ll: LinearLayoutCompat,
        cg: ChipGroup,
        tv: AppCompatTextView,
        dv: VariantKomItem,
        optionId: Int
    ) {
        ll.visible()
        dv.apply {
            val titleVariant = "Pilih ${this.variantName}"
            tv.text = titleVariant
            cg.removeAllViews()
            this.variantOption?.forEach {
                if (it.optionParent == optionId) {
                    val chip = this@MyOrderFragment.layoutInflater.inflate(
                        R.layout.custom_chip_order,
                        null,
                        false
                    ) as Chip
                    chip.apply {
                        text = "${it.optionName}"
                        id = it.optionId!!
                        isChecked = false
                    }
                    cg.addView(chip)
                }
            }
        }
    }

    private fun setupChip(dataVariant: VariantKomItem, position: Int, optionId: Int? = null) {
        binding?.apply {
            when (position) {
                0 -> {
                    binding?.apply {
                        llVariant1.visible()
                        chipVarian1.removeAllViews()
                        dataVariant.apply {
                            val titleVariant = "Pilih ${this.variantName}"
                            tvNameVariant1.text = titleVariant
                            this.variantOption?.forEach {
                                val chip = this@MyOrderFragment.layoutInflater.inflate(
                                    R.layout.custom_chip_order,
                                    null,
                                    false
                                ) as Chip
                                chip.apply {
                                    text = "${it.optionName}"
                                    id = it.optionId!!
                                    isChecked = false
                                }
                                chipVarian1.addView(chip)
                            }
                        }
                    }
                }
                1 -> createChipItem(llVariant2, chipVarian2, tvNameVariant2, dataVariant, optionId ?: 0)

                2 -> createChipItem(llVariant3, chipVarian3, tvNameVariant3, dataVariant, optionId ?: 0)

                3 -> createChipItem(llVariant4, chipVarian4, tvNameVariant4, dataVariant, optionId ?: 0)

                4 -> createChipItem(llVariant5, chipVarian5, tvNameVariant5, dataVariant, optionId ?: 0)
            }
        }
    }

    private fun setupChipChangeListener(checkedId: Int, position: Int) {
        if (variantName.size >= position){
            variantName.set((position - 1), vm.getVariantName(checkedId, dataProductItem.variant?.get(position - 1)!!))
        }else{
            variantName.add((position - 1), vm.getVariantName(checkedId, dataProductItem.variant?.get(position - 1)!!))
        }

        if (variantSize > position) {
            dataProductItem.variant?.get(position)?.let { setupChip(it, position, checkedId) }
            lastOption(false, checkedId)
            hiddenVariant(position)
        } else lastOption(true, checkedId)
    }

    private fun showSkeleton(){
        if (sklPartner == null){
            sklPartner = Skeleton.bind(binding?.spPartner)
                .load(R.layout.skeleton_item_big)
                .show()
        }else {
            sklPartner?.show()
        }
    }

    private fun showSkeletonProduct(){
        if (sklPartner == null){
            sklProduct = Skeleton.bind(binding?.tilProduk)
                .load(R.layout.skeleton_item_big)
                .show()
        }else sklProduct?.show()

    }

    private fun updateQTY(type: String){
        when(type){
            "plus" -> totalProduct += 1
            "minus" -> totalProduct -= 1
            "reset" -> totalProduct = 1
        }
        binding?.tvTotal?.text = totalProduct.toString()
    }
}