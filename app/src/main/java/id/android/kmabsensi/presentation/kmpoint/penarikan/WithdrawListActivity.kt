package id.android.kmabsensi.presentation.kmpoint.penarikan

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse
import id.android.kmabsensi.databinding.ActivityWithdrawalListBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kmpoint.penarikan.adapter.PenarikanItem
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.WithdrawalDetailActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class WithdrawListActivity : BaseActivity() {
    companion object {
        val TYPE_HEADER = 1
        val TYPE_WITHDRAWAL = 0
    }
    private val vm : WithdrawViewModel by inject()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var groupDataPenarikan: ArrayList<PenarikanMainModel> = arrayListOf()

    private val binding by lazy { ActivityWithdrawalListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupObserver()
        setupListener()
        initRv()
    }

    private fun setupObserver() {
        vm.getDataWithdraw().observe(this, {
            when (it) {
                is UiState.Loading -> {
                    Log.d("_getDataWithDraw", "Loading... ")
                }
                is UiState.Success ->{
                    Log.d("_getDataWithDraw", "Success: $it")
                    setupData(it.data.data)
                }
                is UiState.Error -> Log.d("_getDataWithDraw", "Error : $it")
            }
        })
    }

    private fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPenarikan.apply {
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }

    private fun setupData(data: GetWithdrawResponse.DataWithDraw) {
        var date = ""
        data.data.forEach {
            var type = 0
//            if (!date.equals(it.date)) {
//                type = TYPE_HEADER
//                date = it.date!!
//            } else {
//                type = TYPE_WITHDRAWAL
//            }
            groupDataPenarikan.add(PenarikanMainModel(type, it))
        }
        groupAdapter.clear()
        groupDataPenarikan.forEach {
            groupAdapter.add(PenarikanItem(this, it) {
                var type = 0
                if (it.data.transactionType!!.toLowerCase().equals("withdraw_to_me")) type = 1 else type = 0
                startActivity<WithdrawalDetailActivity>(
                        "_typePenarikan" to type,
                        "_idWithdraw" to it.data.id
                        )
            })
        }
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        binding.toolbar.btnSearch.visible()
        binding.toolbar.txtTitle.text = getString(R.string.text_penarikan_poin)
    }
}