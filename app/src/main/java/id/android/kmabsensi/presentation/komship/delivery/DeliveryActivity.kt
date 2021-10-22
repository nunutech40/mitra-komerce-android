package id.android.kmabsensi.presentation.komship.delivery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.komship.AddOrderParams
import id.android.kmabsensi.data.remote.response.komship.*
import id.android.kmabsensi.databinding.ActivityDeliveryBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.ordercart.ValidateChecked
import id.android.kmabsensi.presentation.komship.selectdestination.SelectDestinationActivity
import id.android.kmabsensi.presentation.komship.successorder.SuccessOrderActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ValidationForm.validationTextInputEditText
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DeliveryActivity : BaseActivityRf<ActivityDeliveryBinding>(
    ActivityDeliveryBinding::inflate
) {
    private val vm: DeliveryViewModel by inject()
    private val dataOrder by lazy {
        intent.getParcelableArrayListExtra<ValidateChecked>("_dataOrder")
    }
    private val idPartner by lazy {
        intent.getIntExtra("_idPartner", 0)
    }
    private var listIdOrder = ArrayList<Int>()
    private var listExpandable = false
    private var listOrder: MutableList<CartItem> = ArrayList()
    private lateinit var datePick: Date
    private var productAdapter: ProductDetailAdapter? = null
    private var paymentMethode = ""
    private var nameBank = ""
    private var typeEkspedisi = "REG"
    private val listCalculate: MutableList<CalculateItem> = ArrayList()
    private var dataCalculate = CalculateItem()
    private var destination: DestinationItem? = null
    private var listCustomer: MutableList<CustomerItem> = ArrayList()
    private var listCustomerName = ArrayList<String>()
    private var cust = CustomerItem()
    private var listBank: MutableList<BankItem> = ArrayList()
    private var listBankName = ArrayList<String>()
    private var bankItem = BankItem()
    private var costOrder = 0
    private var costShippingCost = 0
    private var discount = 0

    /** disable payment methode */
    private var isPayment = false

    private var sklBank : SkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Pengiriman", isBackable = true)
        setupView()
        setupObserver()
        setupList()
        setupListener()
    }

    private fun setupObserver() {

        vm.destinationState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag("_destinationState").d("on Loading ")
                is UiState.Success -> {
                    if (it.data.data?.data?.size != 0) {
                        setEnableShipping(true)
                        destination = it.data.data?.data!![0]
                        binding.tieDestination.text = destination!!.label?.toEditable()
                    }
                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })

        vm.customerState.observe(this, { uiState ->
            when (uiState) {
                is UiState.Loading -> Timber.tag("customerState").d("on loading ")
                is UiState.Success -> {
                    listCustomer.addAll(uiState.data.data!!)
                    listCustomer.forEach {
                        listCustomerName.add(it.name!!)
                    }
                    if (listCustomerName.size != 0) {
                        val acAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            listCustomerName
                        )
                        binding.acCustomer.setAdapter(acAdapter)
                    }
                }
                is UiState.Error -> Timber.d(uiState.throwable)
            }
        })

        vm.calculateState.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    showSkeleton()
                    Timber.tag("_calculateState").d("on Loading ")
                }
                is UiState.Success -> {
                    sklBank?.hide()
                    listCalculate.clear()
                    listCalculate.addAll(it.data.data!!)
                    if (typeEkspedisi != "") setupEkspedisi(
                        vm.shippingCost(typeEkspedisi, listCalculate)
                    )
                    if (listCalculate.size > 0) binding.rdGroup.visible() else binding.rdGroup.gone()
                }
                is UiState.Error -> {
                    sklBank?.hide()
                    Timber.tag("_calculateState").d(it.throwable)
                    toast("Server sedang bermasalah, Coba lagi nanti ya")
                }
            }
        })

        vm.addOrderState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag("_addOrderState").d("on Loading ")
                is UiState.Success -> {
                    startActivity<SuccessOrderActivity>(
                        "_successOrder" to it.data.data
                    )
                    finishAffinity()
                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })

        vm.bankState.observe(this, { uiState ->
            when (uiState) {
                is UiState.Loading -> Timber.tag("bankState").d("on Loading")
                is UiState.Success -> {
                    Timber.tag("bankState").d("on Success ${uiState.data.data}")
                    listBank.addAll(uiState.data.data!!)
                    listBank.forEach {
                        val nameBank =
                            if (it.bankName?.lowercase()?.contains("bca")!!) "BCA" else it.bankName
                        listBankName.add("$nameBank (${it.accountName}-${it.accountNo})")
                    }

                    if (listBankName.size != 0) {
                        val spAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            listBankName
                        )
                        binding.spBank.adapter = spAdapter
                    }
                }
                is UiState.Error -> {
                    Timber.d(uiState.throwable)
                }
            }
        })
    }

    private fun setupEkspedisi(calculate: ResultCalculate) {
        if (calculate.isCalculate) {
            dataCalculate = calculate.item
            binding.apply {
                btnOrder.setBackgroundResource(R.drawable.bg_orange_10dp)
                btnOrder.isClickable = true
                btnOrder.isEnabled = true

                tvSendingCost.text = convertRupiah(calculate.item.shippingCost!!)
                costShippingCost = calculate.item.shippingCost.toInt()
                tvTotalCost.text = convertRupiah(calculate.item.grandtotal!!.toDouble())
            }
        } else {
            dataCalculate = CalculateItem()
            binding.apply {
                btnOrder.setBackgroundResource(R.drawable.bg_grey_8dp)
                btnOrder.isClickable = false
                btnOrder.isEnabled = false

                tvSendingCost.text = getString(R.string.paket_tidak_tersedia)
                costShippingCost = 0
                tvTotalCost.text = convertRupiah(totalCost().toDouble())
            }
        }
    }

    private fun setupView() {
        vm.getCustomer()
        vm.getBank()
        listIdOrder = vm.getIdOrder(dataOrder)
        costOrder = vm.getCostOrder(dataOrder!!)
        binding.apply {

            /** destination is req in get calculate shipping cost */
            isPayment = destination != null
            setEnableShipping(isPayment)

            if (dataOrder?.size!! > 2) {
                listExpandable = true
                rlButtonExpand.visible()
            } else {
                listExpandable = false
                rlButtonExpand.gone()
            }
            listOrder = vm.setupListCart(listExpandable, dataOrder!!)
            tieDate.text = getTodayDate().toEditable()
            tvTotalCost.text = convertRupiah(totalCost().toDouble())
        }
    }

    private fun setupListener() {
        binding.apply {
            tieDate.setOnClickListener {
                MaterialDialog(this@DeliveryActivity).show {
                    datePicker { dialog, date ->
                        dialog.dismiss()
                        datePick = date.time
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateSelected: String = dateFormat.format(date.time)
                        setDateFrom(dateSelected)
                    }
                }
            }
            acCustomer.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, _, _ ->
                    cust = vm.getCustomerDetail(listCustomer, acCustomer.text.toString())
                    if (cust.phone != null) {
                        binding.apply {
                            tieTelp.text = cust.phone!!.toEditable()
                            tieAddress.text = cust.address!!.toEditable()
                            vm.getDestination(search = cust.zipCode)
                        }
                    }
                }
            spPaymentMethode.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        p2: Int,
                        p3: Long
                    ) {
                        paymentMethode = vm.paymentMethode(p2)

                        if (p2 != 2) {
                            /** reset bank item */
                            bankItem = BankItem()
                        }
                        vm.setupBank(llBank, p2)
                        if (paymentMethode != "") {
                            vm.getCalculate(
                                discount,
                                "JNE",
                                destination?.value!!,
                                paymentMethode,
                                idPartner,
                                listIdOrder
                            )
                        } else rdGroup.gone()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }

            spBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    /** get initial name bank */
                    nameBank = spBank.selectedItem.toString().split(" ")[0]
                    bankItem = vm.getBankDetail(listBank, nameBank.lowercase())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

            rdGroup.setOnCheckedChangeListener { p0, _ ->
                val rd = findViewById<RadioButton>(p0.checkedRadioButtonId)
                typeEkspedisi = rd.text.toString().take(3)
                setupEkspedisi(vm.shippingCost(typeEkspedisi, listCalculate))
            }

            tieDiscount.doAfterTextChanged {
                discount = if (it.toString() == "") 0
                else it.toString().toInt()

                if (paymentMethode != "") {
                    vm.getCalculate(
                        discount,
                        "JNE",
                        destination?.value!!,
                        paymentMethode,
                        idPartner,
                        listIdOrder
                    )
                }
            }

            btnOrder.setOnClickListener {
                val noTelp = tieTelp.text.toString()
                val address = tieAddress.text.toString()
                val name = acCustomer.text.toString()
                cust = vm.getCustomerDetail(listCustomer, name)
                if (validateCreateOrder()) {
                    val params = AddOrderParams(
                        tieDate.text.toString(),
                        destination?.value!!,
                        destination?.subdistrictName!!,
                        destination?.districtName!!,
                        destination?.cityName!!,
                        1,
                        cust.customerId,
                        name,
                        noTelp,
                        address,
                        "JNE",
                        typeEkspedisi,
                        paymentMethode,
                        bankItem.bankName,
                        bankItem.accountName,
                        bankItem.accountNo,
                        dataCalculate.subtotal!!,
                        dataCalculate.grandtotal!!,
                        dataCalculate.shippingCost?.toInt()!!,
                        dataCalculate.serviceFee!!,
                        dataCalculate.discount?.toInt()!!,
                        dataCalculate.cashback!!,
                        dataCalculate.netProfit!!,
                        listIdOrder
                    )
                    vm.addOrder(idPartner, params)
                }
            }

            btnExpandable.setOnClickListener {
                if (listExpandable) {
                    listExpandable = false
                    btnExpandable.text = getString(R.string.tampilkan_lebih_sedikit)
                    ivExpand.rotation = 180f
                } else {
                    listExpandable = true
                    btnExpandable.text = getString(R.string.selengkapnya)
                    ivExpand.rotation = 0f
                }
                listOrder.clear()
                listOrder.addAll(vm.setupListCart(listExpandable, dataOrder!!))
                productAdapter?.setData(listOrder)
            }

            cbDiskon.setOnCheckedChangeListener { _, b ->
                tieDiscount.isEnabled = b
                tieDiscount.alpha = if (!b) 0.5f else 1f
            }

            tieDestination.setOnClickListener {
                val intent = Intent(this@DeliveryActivity, SelectDestinationActivity::class.java)
                startResult.launch(intent)
            }
        }
    }

    private fun setDateFrom(dateSelected: String) {
        binding.tieDate.setText(dateSelected)
    }

    private fun setupList() {
        productAdapter =
            ProductDetailAdapter(this, object : ProductDetailAdapter.onadapterListener {
                override fun onClick(data: CartItem) {
                }
            })

        binding.rvOrder.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@DeliveryActivity)
            setHasFixedSize(true)
        }

        productAdapter?.setData(listOrder)
    }

    private fun validateCreateOrder(): Boolean {
        binding.apply {
//            cust = vm.getCustomerDetail(listCustomer, acCustomer.text.toString())
            return validationTextInputEditText(tieDate, tilDate, "Form tidak boleh kosong") &&
                    acCustomer.validateAutoComplete(tilCustomer, "Form tidak boleh kosong") &&
                    validationTextInputEditText(tieTelp, tilTelp, "Form tidak boleh kosong") &&
                    validationTextInputEditText(
                        tieDestination,
                        tilDestination,
                        "Form tidak boleh kosong"
                    ) &&
                    validationTextInputEditText(
                        tieAddress,
                        tilAddress,
                        "Form tidak boleh kosong"
                    ) &&
                    paymentMethode != ""
        }
    }

    private val startResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                destination = it.data?.getParcelableExtra("_destination")
                binding.apply {
                    tieDestination.text = destination?.label?.toEditable()
                    isPayment = destination != null
                    setEnableShipping(isPayment)
                }

            }
        }

    private fun setEnableShipping(enable: Boolean) {
        binding.apply {
            spPaymentMethode.isEnabled = enable
            spPaymentMethode.isClickable = enable
            cbDiskon.isClickable = enable
        }
    }

    private fun totalCost(): Int {
        return costOrder + costShippingCost
    }

    private fun showSkeleton(){
        if (sklBank == null){
            sklBank = Skeleton.bind(binding.rdGroup)
                .load(R.layout.skeleton_item_big)
                .show()
        } else sklBank?.show()
    }
}