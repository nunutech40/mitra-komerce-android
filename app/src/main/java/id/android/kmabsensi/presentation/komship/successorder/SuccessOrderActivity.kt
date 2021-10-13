package id.android.kmabsensi.presentation.komship.successorder

import android.os.Bundle
import id.android.kmabsensi.data.remote.response.komship.DataOrderSuccess
import id.android.kmabsensi.databinding.ActivitySuccessOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.MyOrderActivity
import id.android.kmabsensi.presentation.komship.detaildataorder.DetailDataOrderActivity
import id.android.kmabsensi.utils.convertDate
import id.android.kmabsensi.utils.convertTime
import org.jetbrains.anko.startActivity

class SuccessOrderActivity : BaseActivityRf<ActivitySuccessOrderBinding>(
    ActivitySuccessOrderBinding::inflate
) {
    private val data by lazy {
        intent.getParcelableExtra<DataOrderSuccess>("_successOrder")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupListener()
    }

    private fun setupListener() {
        binding.apply {
            btnAddOrder.setOnClickListener {
                startActivity<MyOrderActivity>()
                finishAffinity()
            }
            btnShowDetailOrder.setOnClickListener {
                startActivity<MyOrderActivity>("_currentPage" to 1)
                finishAffinity()
            }
        }
    }

    private fun setupView() {
        binding.apply {
            tvUsername.text = data?.customerName
            tvDateOrder.text = convertDate(data?.orderDate!!)
            tvTimeOrder.text = convertTime(data?.orderDate!!)
            tvNoOrder.text = data?.orderNo
        }
    }
}