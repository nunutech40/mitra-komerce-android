package id.android.kmabsensi.presentation.komship.detaildataorder

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.komship.DataDetailOrder
import id.android.kmabsensi.data.remote.response.komship.OrderItem
import id.android.kmabsensi.databinding.ActivityDetailDataOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.MyOrderActivity
import id.android.kmabsensi.utils.*
import org.jetbrains.anko.startActivity
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

    private var sklList : SkeletonScreen? = null
    private var sklStatus : SkeletonScreen? = null
    private var sklDetail : SkeletonScreen? = null

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
                is UiState.Loading -> {
                    showSkelekton()
                    Timber.tag("detailOrderState").d("on Loading")
                }
                is UiState.Success -> {
                    hideSkeleton()
                    detailOrder = it.data.data
                    detailOrder.let {
                        setupView()
                        orderAdapter?.setData(it?.product!!)
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                    Timber.d(it.throwable)
                }
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
                if (detailOrder?.orderStatus!!.lowercase() != "diajukan"){
                    btnCancle.isClickable = false
                    btnCancle.isEnabled = false
                    btnCancle.alpha = 0.5f
                    btnCancle.setTextColor(ContextCompat.getColor(this@DetailDataOrderActivity, R.color.cl_grey_dark_tx_new))
                }
                tvNoOrder.text = detailOrder?.orderNo.toString()
                tvOrderTime.text = vm.getTime(detailOrder?.orderDate!!)
                tvOrderDate.text = convertDate(detailOrder?.orderDate!!)
                tvCustomerName.text = detailOrder?.customerName
                tvAddress.text = detailOrder?.customerAddress
                tvPayment.text = convertRupiah((detailOrder?.grandtotal ?: 0).toDouble())
                tvSendingType.text = detailOrder?.shippingType
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            btnToTheTop.setOnClickListener {
                nestedScrollView.scrollTo(0, 0)
            }
            btnChatWhatsapp.setOnClickListener {
                openWhatsappContact(this@DetailDataOrderActivity, detailOrder?.customerPhone!!)
            }
            btnCancle.setOnClickListener {
                showDialogConfirmCancle(this@DetailDataOrderActivity, object : OnSingleCLick{
                    override fun onCLick() {
                        vm.deleteOrder(idPartner, dataOrder?.orderId!!)
                    }
                })
            }
        }
    }

    private fun showSkelekton(){
        if (sklList == null){
            sklList = Skeleton.bind(binding.rvProduct)
                .adapter(orderAdapter)
                .load(R.layout.skeleton_list_order)
                .show()
            sklStatus = Skeleton.bind(binding.tvStatusOrder)
                .load(R.layout.skeleton_item_big)
                .show()
            sklDetail = Skeleton.bind(binding.llDetailProduct)
                .load(R.layout.skeleton_item_layout)
                .show()
        }else{
            sklList?.show()
            sklStatus?.show()
            sklDetail?.show()
        }
    }

    private fun hideSkeleton(){
        sklList?.hide()
        sklStatus?.hide()
        sklDetail?.hide()
    }
}