package id.android.kmabsensi.presentation.komship.delivery

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import id.android.kmabsensi.data.remote.body.komship.AddOrderParams
import id.android.kmabsensi.data.remote.response.komship.CalculateItem
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.data.remote.response.komship.DestinationItem
import id.android.kmabsensi.databinding.ActivityDeliveryBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.ordercart.ValidateChecked
import id.android.kmabsensi.presentation.komship.successorder.SuccessOrderActivity
import id.android.kmabsensi.utils.*
import org.jetbrains.anko.startActivity
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
    private var listExpandable = false

    private var listOrder: MutableList<CartItem> = ArrayList()
    lateinit var datePick: Date
    private var productAdapter: ProductDetailAdapter? = null
    private var paymentMethode = ""
    private var nameBank = ""
    private var typeEkspedisi = "REG"
    private val listCalculate: MutableList<CalculateItem> = ArrayList()

    private val listAutoComplete: MutableList<String> = ArrayList()
    private val destinationItem : MutableList<DestinationItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Pengiriman", isBackable = true)
        setupView()
        setupObserver()
        setupList()
        setupListener()
    }

    private fun setupObserver() {
        vm.destinationState.observe(this, { uiState ->
            when (uiState) {
                is UiState.Loading -> Log.d("_destinationState", "on Loading ")
                is UiState.Success -> {
                    listAutoComplete.clear()
                    destinationItem.clear()
                    destinationItem.addAll(uiState.data.data?.data!!)
                    destinationItem.forEach {
                        listAutoComplete.add(it?.label!!)
                    }
                }
                is UiState.Error -> Log.d("_destinationState", "on Error ${uiState.throwable}")
            }
        })

        vm.calculateState.observe(this, {
            when (it) {
                is UiState.Loading -> Log.d("calculateState", "on Loading ")
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
                is UiState.Error -> Log.d("calculateState", "on Error ${it.throwable}")
            }
        })

        vm.addOrderState.observe(this, {
            when (it) {
                is UiState.Loading -> Log.d("addOrderState", "on Loading ")
                is UiState.Success -> {
                    startActivity<SuccessOrderActivity>()
                }
                is UiState.Error -> Log.d("addOrderState", "on Error ${it.throwable}")
            }
        })
    }

    private fun setupAutoComplete(list: MutableList<String>) {
        val scAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        binding.autoComplete.setAdapter(scAdapter)
    }

        private fun setupEkspedisi(calculate: ResultCalculate) {
            if (calculate.isCalculate) {
                binding.apply {
                    tvSendingCost.text = convertRupiah(calculate.item.shippingCost!!)
                    tvBonus.text = convertRupiah(calculate.item.discount!!.toDouble())
                }

            } else binding.tvSendingCost.text = "Kelas tidak tersedia."

        }

        private fun setupView() {
            vm.getDestination()
            binding.apply {
                if (dataOrder?.size!! > 2){
                    listExpandable = true
                    rlButtonExpand.visible()
                } else{
                    listExpandable = false
                    rlButtonExpand.gone()
                }
                listOrder = vm.setupListCart(listExpandable, dataOrder!!)
                tieDate.text = getTodayDate().toEditable()
            }
        }

        private fun setupListener() {
            binding.apply {
                tieDate.setOnClickListener {
                    MaterialDialog(this@DeliveryActivity).show {
                        datePicker { dialog, date ->
                            // Use date (Calendar)
                            dialog.dismiss()

                            datePick = date.time
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateSelected: String = dateFormat.format(date.time)
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
                            vm.setupBank(llBank, p2)
                            if (paymentMethode != "") {
                                vm.getCalculate(0, "JNE", "AMI10000", paymentMethode, 201)
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }

                spBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        nameBank = p1.toString()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
                rdGroup.setOnCheckedChangeListener { p0, p1 ->
                    val rd = findViewById<RadioButton>(p0.checkedRadioButtonId)
                    typeEkspedisi = rd.text.toString().take(3)
                    setupEkspedisi(vm.shippingCost(typeEkspedisi, listCalculate))
                }

                btnOrder.setOnClickListener {
                    val id = listOf(15, 14)
                    val params = AddOrderParams(
                        "2021-09-30",
                        "BDO10000",
                        "TUNJUNGMULI",
                        "KARANGMONCOL",
                        "PURBALINGGA",
                        1,
                        0,
                        "Pak Muh",
                        "09182938398",
                        "rt.10. Pulogadung, jakarta",
                        "JNE",
                        "YES",
                        "COD",
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
                        id
                    )
                    vm.addOrder(201, params)
                }

                autoComplete.doAfterTextChanged {
                    vm.getDestination(search = it.toString())
                    setupAutoComplete(listAutoComplete)
                }

                btnExpandable.setOnClickListener {
                    if (listExpandable){
                        listExpandable = false
                        btnExpandable.text = "Lebih sedikit"
                        ivExpand.rotation = 180f
                    }else{
                        listExpandable = true
                        btnExpandable.text = "Selengkapnya"
                        ivExpand.rotation = 0f
                    }
                    listOrder.clear()
                    listOrder.addAll(vm.setupListCart(listExpandable, dataOrder!!))
                    productAdapter?.setData(listOrder)
                }

                cbDiskon.setOnCheckedChangeListener { compoundButton, b ->

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
}