package id.android.kmabsensi.presentation.komship.myorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.chip.Chip
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.komship.AddCartParams
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.data.remote.response.komship.ProductKomItem
import id.android.kmabsensi.data.remote.response.komship.ProductVariantKomItem
import id.android.kmabsensi.data.remote.response.komship.VariantKomItem
import id.android.kmabsensi.databinding.FragmentMyOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.presentation.komship.selectproduct.SelectProductActivity
import id.android.kmabsensi.utils.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject


class MyOrderFragment : BaseFragmentRf<FragmentMyOrderBinding>(
    FragmentMyOrderBinding::inflate
) {
    private val vm : MyOrderViewModel by inject()
    private var totalProduct = 1
    private var maxProduct = 10
    val TAGp = "_partnerState"
    private val productKey = "_productByPartner"
    private var dataPartner : MutableList<KomPartnerItem> = ArrayList()
    private var dataProduct : ArrayList<ProductKomItem> = ArrayList()
    private lateinit var dataProductItem : ProductKomItem
    private var variantSize = 0
    private lateinit var dataOrder : AddCartParams

    private lateinit var productVariantSelect : ProductVariantKomItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupView()
        setupObserver()
    }

    private fun setupObserver() {
        vm.partnerState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> Log.d(TAGp, "on loading")
                is UiState.Success -> {
                    dataPartner.addAll(it.data.data!!)
                    setupSpinnerPartner(it.data.data)
                }
                is UiState.Error -> Log.d(TAGp, "on error ${it.throwable}")
            }
        })

        vm.productState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> Log.d("_productState", "on loading")
                is UiState.Success -> {
                    dataProduct.clear()
                    dataProduct.addAll(it.data.data!!)
                }
                is UiState.Error -> Log.d("_productState", "on error")
            }
        })
    }

    private fun setupContentProduct(data: ProductKomItem) {
        binding?.apply {
            llDetailProduct.visible()
            variantSize = data.variant!!.size
            imgProduct.loadImageFromUrl(data.productImage?:"https://www.kindpng.com/picc/m/600-6008515_shopping-transparent-design-png-shopping-bag-icon-png.png")
            tvNameProduct.text = data.productName?:"-"
            tvPrice.text = "Rp${data.price}"
            tvAvailableProduct.text = "Tersedia: ${data.stock} Pcs"
            setupChip(data.variant[0], 0)
        }
    }

    private fun setupChip(dataVariant : VariantKomItem, position : Int, optionId: Int? = null){
        when(position){
            0 -> {
                binding?.apply {
                    llVariant1.visible()
                    chipVarian1.removeAllViews()
                    dataVariant.apply {
                        tvNameVariant1.text = "Pilih ${this.variantName}"
                        this.variantOption?.forEach {
                            val chip = this@MyOrderFragment.layoutInflater.inflate(R.layout.custom_chip_order,
                                null,
                                false) as Chip
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
            1 -> {
                binding?.apply {
                    llVariant2.visible()
                    dataVariant.apply {
                        tvNameVariant2.text = "Pilih ${this.variantName}"
                        chipVarian2.removeAllViews()
                        this.variantOption?.forEach {
                            if (it.optionParent == optionId){
                                val chip = this@MyOrderFragment.layoutInflater.inflate(R.layout.custom_chip_order,
                                    null,
                                    false) as Chip
                                chip.apply {
                                    text = "${it.optionName}"
                                    id = it.optionId!!
                                    isChecked = false
                                }
                                chipVarian2.addView(chip)
                            }
                        }
                    }
                }
            }
            2 -> {
                binding?.apply {
                    llVariant3.visible()
                    dataVariant.apply {
                        tvNameVariant3.text = "Pilih ${this.variantName}"
                        chipVarian3.removeAllViews()
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
                                chipVarian3.addView(chip)
                            }
                        }
                    }
                }
            }
            3 -> {
                binding?.apply {
                    llVariant4.visible()
                    dataVariant.apply {
                        tvNameVariant4.text = "Pilih ${this.variantName}"
                        chipVarian4.removeAllViews()
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
                                chipVarian4.addView(chip)
                            }
                        }
                    }
                }
            }
            4 -> {
                binding?.apply {
                    llVariant5.visible()
                    dataVariant.apply {
                        tvNameVariant5.text = "Pilih ${this.variantName}"
                        chipVarian5.removeAllViews()
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
                                chipVarian5.addView(chip)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupSpinnerPartner(data: List<KomPartnerItem>?) {
        var partner = ArrayList<String>()
        data?.forEach {
            partner.add(it.partnerName!!)
        }

        ArrayAdapter<String>(requireContext(), R.layout.spinner_item, partner)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                            vm.getProduct(201)
//                            vm.getProduct(dataPartner[position].partnerId!!)
                        }
                    }
            }
    }

    private fun setupView() {
        vm.getPartner()
    }

    private fun setupListener() {
        binding?.apply {
            btnPlus.setOnClickListener {
                if (vm.validateMaxProduct(totalProduct, maxProduct)){
                    totalProduct+=1
                    tvTotal.text = totalProduct.toString()
                }else{
                    requireActivity().toast("sudah mencapai batas max")
                }
            }

            btnCart.setOnClickListener {
                dataOrder = AddCartParams(
                    dataProductItem.productId!!,
                    dataProductItem.productName!!,
                    productVariantSelect.optionId!!,
                    productVariantSelect.name!!,
                    productVariantSelect.price!!,
                    totalProduct,
                    (totalProduct*productVariantSelect.price!!)
                )
                vm.addCart(dataOrder)
            }

            btnMinus.setOnClickListener {
                if (vm.validateMinProduct(totalProduct)){
                    totalProduct-=1
                    tvTotal.text = totalProduct.toString()
                }else{
                    requireActivity().toast("sudah mencapai batas min")
                }
            }

            btnOrder.setOnClickListener {
                Log.d(TAGp, "btn Order setupListener: $dataOrder")
            }

            tieProduk.setOnClickListener {
                if (!dataPartner.isNullOrEmpty()) {
                    val intent = Intent(requireContext(), SelectProductActivity::class.java)
                    intent.putParcelableArrayListExtra(productKey, dataProduct)
                    startResult.launch(intent)
                } else requireActivity().toast("Anda belum memilih Partner")
            }

            chipVarian1.setOnCheckedChangeListener { group, checkedId ->
                if (variantSize>1){
                    dataProductItem.variant?.get(1)?.let { setupChip(it, 1, checkedId) }
                    lastOption(false, checkedId)
                    hidenVariant(1)
                }else lastOption(true, checkedId)
            }
            chipVarian2.setOnCheckedChangeListener { group, checkedId ->
                if (variantSize>2){
                    dataProductItem.variant?.get(2)?.let { setupChip(it, 2, checkedId) }
                    lastOption(false, checkedId)
                    hidenVariant(2)
                }else lastOption(true, checkedId)
            }
            chipVarian3.setOnCheckedChangeListener { group, checkedId ->
                if (variantSize>3){
                    dataProductItem.variant?.get(3)?.let { setupChip(it, 3, checkedId) }
                    lastOption(false, checkedId)
                    hidenVariant(3)
                }else lastOption(true, checkedId)
            }
            chipVarian4.setOnCheckedChangeListener { group, checkedId ->
                if (variantSize>4){
                    dataProductItem.variant?.get(4)?.let { setupChip(it, 4, checkedId) }
                    lastOption(false, checkedId)
                }else lastOption(true, checkedId)
            }
            chipVarian5.setOnCheckedChangeListener { group, checkedId ->
                if (variantSize>5){
                    dataProductItem.variant?.get(5)?.let { setupChip(it, 5, checkedId) }
                    lastOption(false, checkedId)
                }else lastOption(true, checkedId)
            }
        }
    }

    private fun hidenVariant(position : Int){
        when(position){
            0 ->{
                binding?.apply {
                    llVariant2.gone()
                    llVariant3.gone()
                    llVariant4.gone()
                    llVariant5.gone()
                }
            }
            1 ->{
                binding?.apply {
                    llVariant3.gone()
                    llVariant4.gone()
                    llVariant5.gone()
                }
            }
            2 ->{
                binding?.apply {
                    llVariant4.gone()
                    llVariant5.gone()
                }
            }
            3 ->{
                binding?.apply {
                    llVariant5.gone()
                }
            }
        }
    }

    private fun lastOption(last : Boolean, optionsId: Int){
        if (last){
            binding?.llQty?.visible()
            dataProductItem.productVariant?.forEach {
                if (it.optionId == optionsId){
                    binding?.apply {
                        tvNameProduct.text = "${dataProductItem.productName} - ${it.name}"
                        tvPrice.text = "Rp${it.price}"
                        tvAvailableProduct.text = "Tersedia: ${it.stock} Pcs"

                        btnOrder.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_orange_10dp))
                        imgCart.setImageDrawable(resources.getDrawable(R.drawable.ic_orderku_rf))
                        productVariantSelect = it
                    }
                }
            }
        }else {
            binding?.apply {
                imgCart.setImageDrawable(resources.getDrawable(R.drawable.ic_orderku_off_rf))
                btnOrder.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_grey_8dp))
                llQty?.gone()
            }
        }
    }

    val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            dataProductItem = it.data?.getParcelableExtra<ProductKomItem>(productKey)!!
            setupContentProduct(dataProductItem)
            hidenVariant(0)
            binding?.tieProduk?.text = dataProductItem.productName?.toEditable()
        }
    }

}