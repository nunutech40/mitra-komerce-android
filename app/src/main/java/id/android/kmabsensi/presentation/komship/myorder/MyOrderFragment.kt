package id.android.kmabsensi.presentation.komship.myorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.data.remote.response.komship.ProductKomItem
import id.android.kmabsensi.data.remote.response.komship.ProductVariantKomItem
import id.android.kmabsensi.data.remote.response.komship.VariantKomItem
import id.android.kmabsensi.databinding.FragmentMyOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.presentation.komship.ordercart.OrderCartActivity
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
    private var maxProduct = 10
    private val TAGp = "_partnerState"
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
        setupListener()
        setupView()
        setupObserver()
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
                    if (isDirectOrder) {
                        requireActivity().startActivity<OrderCartActivity>("_isDirectOrder" to isDirectOrder)
                    } else {
                        requireActivity().toast("Data berhasil ditambahkan ke keranjang.")
                    }
                    resetForm()
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
                    Timber.tag(TAGp).d("on loading")
                }
                is UiState.Success -> {
                    binding?.srMyOrder?.isRefreshing = false
                    sklPartner?.hide()
                    dataPartner.addAll(it.data.data!!)
                    setupSpinnerPartner(it.data.data)
                }
                is UiState.Error -> {
                    binding?.srMyOrder?.isRefreshing = false
                    sklPartner?.hide()
                    Timber.d(it.throwable)
                }
            }
        })

        vm.productState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> Timber.tag("_productState").d("on loading")
                is UiState.Success -> {
                    sklProduct?.hide()
                    dataProduct.clear()
                    dataProduct.addAll(it.data.data!!)
                }
                is UiState.Error -> {
                    sklProduct?.hide()
                    Timber.d(it.throwable)
                }
            }
        })
    }

    private fun setupContentProduct(data: ProductKomItem) {
        binding?.apply {
            llDetailProduct.visible()
            variantSize = data.variant!!.size
            imgProduct.loadImageFromUrl(
                if (data.productImage?.size != 0) data.productImage?.get(0)!!
                else "https://www.kindpng.com/picc/m/600-6008515_shopping-transparent-design-png-shopping-bag-icon-png.png"
            )
            tvNameProduct.text = data.productName ?: "-"
            tvPrice.text = convertRupiah(data.price?.toDouble()!!)
            tvAvailableProduct.text = "Tersedia: ${data.stock} Pcs"
            setupChip(data.variant[0], 0)
        }
    }

    private fun setupSpinnerPartner(data: List<KomPartnerItem>?) {
        val partner = ArrayList<String>()
        data?.forEach {
            partner.add(it.partnerName!!)
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
                            idPartner = dataPartner[position].partnerId!!
                            vm.getProduct(idPartner)
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
                    totalProduct += 1
                    tvTotal.text = totalProduct.toString()
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
                    totalProduct -= 1
                    tvTotal.text = totalProduct.toString()
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
            chipVarian1.setOnCheckedChangeListener { group, checkedId ->
                setupChipChangeListener(checkedId, 1)
            }

            chipVarian2.setOnCheckedChangeListener { group, checkedId ->
                setupChipChangeListener(checkedId, 2)
            }

            chipVarian3.setOnCheckedChangeListener { group, checkedId ->
                setupChipChangeListener(checkedId, 3)
            }

            chipVarian4.setOnCheckedChangeListener { group, checkedId ->
                setupChipChangeListener(checkedId, 4)
            }

            chipVarian5.setOnCheckedChangeListener { group, checkedId ->
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
        if (last) {
            binding?.nsProduct?.smoothScrollTo(0, binding?.nsProduct?.getChildAt(0)!!.height)
            binding?.llQty?.visible()
            dataProductItem.productVariant?.forEach {
                if (it.optionId == optionsId) {
                    binding?.apply {
                        setupButton(true)
                        tvNameProduct.text = "${dataProductItem.productName} - ${it.name}"
                        tvPrice.text = convertRupiah(it.price?.toDouble()!!)
                        tvAvailableProduct.text = "Tersedia: ${it.stock} Pcs"

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
                dataProductItem = it.data?.getParcelableExtra<ProductKomItem>(productKey)!!
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
            tv.text = "Pilih ${this.variantName}"
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
                            tvNameVariant1.text = "Pilih ${this.variantName}"
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
                1 -> createChipItem(
                    llVariant2,
                    chipVarian2,
                    tvNameVariant2,
                    dataVariant,
                    optionId!!
                )
                2 -> createChipItem(
                    llVariant3,
                    chipVarian3,
                    tvNameVariant3,
                    dataVariant,
                    optionId!!
                )
                3 -> createChipItem(
                    llVariant4,
                    chipVarian4,
                    tvNameVariant4,
                    dataVariant,
                    optionId!!
                )
                4 -> createChipItem(
                    llVariant5,
                    chipVarian5,
                    tvNameVariant5,
                    dataVariant,
                    optionId!!
                )
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
            sklProduct = Skeleton.bind(binding?.tieProduk)
                .load(R.layout.skeleton_item_big)
                .show()
        }else {
            sklPartner?.show()
            sklProduct?.show()
        }
    }
}