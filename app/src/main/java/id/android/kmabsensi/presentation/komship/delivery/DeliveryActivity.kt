package id.android.kmabsensi.presentation.komship.delivery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.github.ajalt.timberkt.Timber
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
    private var listIdOrder = ArrayList<Int>()

    private var listExpandable = false

    private var listOrder: MutableList<CartItem> = ArrayList()
    lateinit var datePick: Date
    private var productAdapter: ProductDetailAdapter? = null
    private var paymentMethode = ""
    private var nameBank = ""
    private var typeEkspedisi = "REG"
    private val listCalculate: MutableList<CalculateItem> = ArrayList()
    private val calculateItem = CalculateItem()

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
    var isPayment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Pengiriman", isBackable = true)
        setupView()
        setupObserver()
        setupList()
        setupListener()
    }
/**
 * calculating / ongkir perlu get destination, diskon dan payment methode
 *
 * */
    private fun setupObserver() {

        vm.customerState.observe(this, { uiState ->
            when (uiState) {
                is UiState.Loading -> Timber.tag("customerState").d("on loading ")
                is UiState.Success -> {
                    Log.d("customerState", "on success ${uiState.data}")
                    listCustomer.addAll(uiState.data.data!!)
                    listCustomer.forEach {
                        listCustomerName.add(it.name!!)
                    }
                    if (listCustomerName.size != 0){
                        val acAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listCustomerName)
                        binding.acCustomer.setAdapter(acAdapter)
                    }
                }
                is UiState.Error -> Timber.d(uiState.throwable)
            }
        })

        vm.calculateState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag("calculateState").d("on Loading ")
                is UiState.Success -> {
                    listCalculate.clear()
                    listCalculate.addAll(it.data.data!!)
                    if (typeEkspedisi != "") setupEkspedisi(
                        vm.shippingCost(
                            typeEkspedisi,
                            listCalculate
                        )
                    )

                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })

        vm.addOrderState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag("addOrderState").d("on Loading ")
                is UiState.Success -> {
                    startActivity<SuccessOrderActivity>()
                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })

        vm.bankState.observe(this,{
            when(it){
                is UiState.Loading -> Timber.tag("bankState").d("on Loading")
                is UiState.Success -> {
                    Timber.tag("bankState").d("on Success ${it.data.data}")
                    listBank.addAll(it.data.data!!)
                    listBank.forEach {
                        var nameBank = if (it.bankName?.lowercase()?.contains("bca")!!)"BCA" else it.bankName
                        listBankName.add("$nameBank (${it.accountName}-${it.accountNo})")
                    }

                    if (listBankName.size != 0){
                        val spAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listBankName)
                        binding.spBank.adapter = spAdapter
                    }
                }
                is UiState.Error ->{
                    Timber.d(it.throwable)
                }
            }
        })
    }

    private fun setupEkspedisi(calculate: ResultCalculate) {
        if (calculate.isCalculate) {
            binding.apply {
                tvSendingCost.text  = convertRupiah(calculate.item.shippingCost!!)
                costShippingCost    = calculate.item.shippingCost.toInt()
            }

        } else {
            binding.tvSendingCost.text  = "Kelas tidak tersedia."
            costShippingCost            = 0
        }
        binding.tvTotalCost.text = convertRupiah(totalCost().toDouble())
    }

    private fun setupView() {
        vm.getCustomer()
        vm.getBank()
        listIdOrder     = vm.getIdOrder(dataOrder)
        costOrder       = vm.getCostOrder(dataOrder!!)
        binding.apply {

            /** destination is req in get calculate shipping cost */
            isPayment = destination != null
            setEnableShipping(isPayment)
//            spPaymentMethode.setupSpinner(isPayment)

            if (dataOrder?.size!! > 2){
                listExpandable  = true
                rlButtonExpand.visible()
            } else{
                listExpandable  = false
                rlButtonExpand.gone()
            }
            listOrder           = vm.setupListCart(listExpandable, dataOrder!!)
            tieDate.text        = getTodayDate().toEditable()
            tvTotalCost.text    = convertRupiah(totalCost().toDouble())
        }
    }

    private fun setupListener() {
        binding.apply {
            tieDate.setOnClickListener {
                MaterialDialog(this@DeliveryActivity).show {
                    datePicker { dialog, date ->
                        dialog.dismiss()
                        datePick                    = date.time
                        val dateFormat              = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateSelected: String    = dateFormat.format(date.time)
                        setDateFrom(dateSelected)
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

                        if (p2 != 2){
                        /** reset bank item */
                            bankItem = BankItem()
                        }
                        vm.setupBank(llBank, p2)
                        if (paymentMethode != "") {
                            vm.getCalculate(discount, "JNE", "AMI10000", paymentMethode, 201)
                        }
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }

            spBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        /** get initial name bank */
                        nameBank = p1.toString().split(" ")[0]
                        bankItem = vm.getBankDetail(listBank, nameBank)
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
            }

            rdGroup.setOnCheckedChangeListener { p0, p1 ->
                val rd          = findViewById<RadioButton>(p0.checkedRadioButtonId)
                typeEkspedisi   = rd.text.toString().take(3)
                setupEkspedisi(vm.shippingCost(typeEkspedisi, listCalculate))
            }

            tieDiscount.doAfterTextChanged {
                discount = it.toString().toInt()
            }

            btnOrder.setOnClickListener {
                cust = vm.getCustomerDetail(listCustomer, acCustomer.text.toString())
                if (validateCreateOrder()){
                    val params = AddOrderParams(
                        tieDate.text.toString(),
                        destination?.value!!,
                        destination?.subdistrictName!!,
                        destination?.districtName!!,
                        destination?.cityName!!,
                        1,
                        cust.customerId,
                        cust.name!!,
                        cust.phone,
                        cust.address,
                        "JNE",
                        typeEkspedisi,
                        paymentMethode,
                        null,
                        null,
                        null,
                        40000,
                        82000,
                        42000,
                        3500,
                        0,
                        3750,
                        113750,
                        listIdOrder
                    )
                    vm.addOrder(201, params)
                }
            }

            btnExpandable.setOnClickListener {
                if (listExpandable){
                    listExpandable      = false
                    btnExpandable.text  = "Lebih sedikit"
                    ivExpand.rotation   = 180f
                }else{
                    listExpandable      = true
                    btnExpandable.text  = "Selengkapnya"
                    ivExpand.rotation   = 0f
                }
                listOrder.clear()
                listOrder.addAll(vm.setupListCart(listExpandable, dataOrder!!))
                productAdapter?.setData(listOrder)
            }

            cbDiskon.setOnCheckedChangeListener { compoundButton, b ->
                tieDiscount.isEnabled   = b
                tieDiscount.alpha       = if (!b) 0.5f else 1f
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
        productAdapter = ProductDetailAdapter(this, object : ProductDetailAdapter.onadapterListener {
            override fun onClick(data: CartItem) {
            }
        })

        binding.rvOrder.apply {
            adapter         = productAdapter
            layoutManager   = LinearLayoutManager(this@DeliveryActivity)
            setHasFixedSize(true)
        }

        productAdapter?.setData(listOrder)
    }

    private fun validateCreateOrder(): Boolean{
        binding.apply {
            cust = vm.getCustomerDetail(listCustomer, acCustomer.text.toString())
            return validationTextInputEditText(tieDate, tilDate, "Form tidak boleh kosong")&&
                    acCustomer.validateAutoComplete(tilCustomer,"Form tidak boleh kosong") &&
                    validationTextInputEditText(tieTelp, tilTelp, "Form tidak boleh kosong") &&
                    validationTextInputEditText(tieDestination, tilDestination, "Form tidak boleh kosong") &&
                    validationTextInputEditText(tieAddress, tilAddress, "Form tidak boleh kosong") &&
                    paymentMethode != ""
        }
    }

    private val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            destination = it.data?.getParcelableExtra("_destination")
            binding.apply {
                tieDestination.text = destination?.label?.toEditable()
                isPayment           = destination != null
                setEnableShipping(isPayment)
            }

        }
    }

    private fun setEnableShipping(enable: Boolean){
        binding.apply {
            spPaymentMethode.isEnabled = enable
            spPaymentMethode.isClickable = enable

            if (enable) rdGroup.visible() else rdGroup.gone()

            cbDiskon.isClickable = enable
        }
    }

    private fun AppCompatSpinner.setupSpinner(mode: Boolean){
        this.isEnabled = mode
        this.isClickable = mode
    }

    private fun totalCost():Int{
        return costOrder+costShippingCost
    }
}