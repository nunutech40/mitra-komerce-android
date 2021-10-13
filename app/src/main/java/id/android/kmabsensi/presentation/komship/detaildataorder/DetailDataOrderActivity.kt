package id.android.kmabsensi.presentation.komship.detaildataorder

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.data.remote.response.komship.DataDetailOrder
import id.android.kmabsensi.data.remote.response.komship.OrderItem
import id.android.kmabsensi.databinding.ActivityDetailDataOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.MyOrderActivity
import id.android.kmabsensi.utils.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import timber.log.Timber

class DetailDataOrderActivity : BaseActivityRf<ActivityDetailDataOrderBinding>(
    ActivityDetailDataOrderBinding::inflate
) {

    private val vm: DetailOrderViewModel by inject()
    private val dataOrder by lazy {
        intent.getParcelableExtra<OrderItem>("_dataOrder")
    }

    private val idPartner by lazy {
        intent.getIntExtra("_idPartner", 0)
    }

    private var detailOrder: DataDetailOrder? = null
    private var orderAdapter: DetailOrderAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Data Order", isBackable = true)
        setupListener()
        setupObserver()
        setupList()
    }

    private fun setupObserver() {
        vm.getDetailOrder(idPartner, dataOrder?.orderId!!)
        vm.detailOrderState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag("detailOrderState").d("on Loading")
                is UiState.Success -> {
                    Timber.tag("detailOrderState").d("on Success ${it.data.data}")
                    detailOrder = it.data.data
                    detailOrder.let {
                        setupView()
                        orderAdapter?.setData(it?.product!!)
                    }
                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })

        vm.deleteState.observe(this, {
            when (it) {
                is UiState.Loading -> Timber.tag("deleteState").d("on Loading")
                is UiState.Success -> {
                    Timber.tag("deleteState").d("on Success ${it.data}")
                    startActivity<MyOrderActivity>(
                        "_currentPage" to 1
                    )
                }
                is UiState.Error -> Timber.d(it.throwable)
            }
        })
    }

    private fun setupList() {
        orderAdapter = DetailOrderAdapter(this)
        binding.rvProduct.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(this@DetailDataOrderActivity)
            setHasFixedSize(true)
        }
    }

    private fun setupView() {
        binding.apply {
            if (detailOrder != null) {
                tvStatusOrder.setStatusOrderView(detailOrder?.orderStatus!!)
                if (detailOrder?.orderStatus!!.lowercase() == "diajukan"){
                    btnCancle.visible()
                }
                tvNoOrder.text = detailOrder?.orderNo.toString()
                tvOrderTime.text = vm.getTime(detailOrder?.orderDate!!)
                tvOrderDate.text = convertDate(detailOrder?.orderDate!!)
                tvCustomerName.text = detailOrder?.customerName
                tvAddress.text = detailOrder?.customerAddress
                tvPayment.text = convertRupiah(detailOrder?.grandtotal?.toDouble()!!)
                tvSendingType.text = detailOrder?.shippingType
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            btnToTheTop.setOnClickListener {
                toast("Scroll to the top")
                nestedScrollView.scrollTo(0, 0)
            }
            btnChatWhatsapp.setOnClickListener {
                openWhatsappContact(this@DetailDataOrderActivity, detailOrder?.customerPhone!!)
            }
            btnCancle.setOnClickListener {
                showDialogConfirmDelete(this@DetailDataOrderActivity, "", "Yakin ingin menghapus Pesanan ini?"){
                    vm.deleteOrder(idPartner, dataOrder?.orderId!!)
                }
            }
        }
    }

}