package id.android.kmabsensi.presentation.komship.delivery

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import id.android.kmabsensi.data.remote.body.komship.AddOrderParams
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.databinding.ActivityDeliveryBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.ordercart.ValidateChecked
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.getTodayDate
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
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
    private var listOrder : MutableList<CartItem> = ArrayList()
    lateinit var datePick: Date
    private lateinit var productAdapter : ProductDetailAdapter
    private var paymentMethode = ""
    private var nameBank = ""
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
            when(it){
                is UiState.Loading -> Log.d("_destinationState", "on Loading ")
                is UiState.Success -> Log.d("_destinationState", "on SUccess ${it.data.data}")
                is UiState.Error -> Log.d("_destinationState", "on Error ${it.throwable}")
            }
        })

        vm.calculateState.observe(this, {
            when(it){
                is UiState.Loading -> Log.d("calculateState", "on Loading ")
                is UiState.Success -> Log.d("calculateState", "on SUccess ${it.data.data}")
                is UiState.Error -> Log.d("calculateState", "on Error ${it.throwable}")
            }
        })

        vm.addOrderState.observe(this, {
            when(it){
                is UiState.Loading -> Log.d("addOrderState", "on Loading ")
                is UiState.Success -> Log.d("addOrderState", "on SUccess ${it.data}")
                is UiState.Error -> Log.d("addOrderState", "on Error ${it.throwable}")
            }
        })
    }

    private fun setupView() {
        vm.getDestination()
        binding.apply {
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
            spPaymentMethode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    paymentMethode = vm.paymentMethode(p2)
                    vm.setupBank(llBank, p2)
                    if (paymentMethode != ""){
                        vm.getCalculate(0, "JNE", "AMI10000", paymentMethode)
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

            spBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    nameBank = p1.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
            rdGroup.setOnCheckedChangeListener { p0, p1 ->
                Log.d("_TAGTAGTAG", "rd: ${p0?.id}, index : $p1 ")
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

        }
    }

    private fun setDateFrom(dateSelected: String) {
        binding.tieDate.setText(dateSelected)
    }

    private fun setupList() {
        productAdapter = ProductDetailAdapter(this, object : ProductDetailAdapter.onadapterListener{
            override fun onClick(data: CartItem) {
            }
        })
        binding.rvOrder.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@DeliveryActivity)
            setHasFixedSize(true)
        }
        dataOrder?.forEach {
            listOrder.add(it.item)
        }.also {
            productAdapter.setData(listOrder)
        }
    }
}