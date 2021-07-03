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
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.kmpoint.penarikan.adapter.WithdrawAdapter
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.WithdrawalDetailActivity
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.isEmpty
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
    private var groupDataWithdraw: ArrayList<WithdrawMainModel> = arrayListOf()
    private lateinit var withdrawAdapter : WithdrawAdapter
    private val binding by lazy { ActivityWithdrawalListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        initRv()
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        vm.getAllWithdrawPaged().observe(this, {
            withdrawAdapter.submitList(it)
        })


        vm.getState().observe(this,{
            when(it){
                State.LOADING-> {
                    showSkeletonPaging(binding.rvPenarikan, R.layout.skeleton_list_sdm, rvAdapter3 = withdrawAdapter)
                }
                State.DONE-> hideSkeletonPaging()
                State.ERROR-> {
                    Log.d("_state", "ERROR")
                    hideSkeletonPaging()
                }
            }
        })

        vm.isEmpty().observe(this, {
            when (it) {
                isEmpty.ISTRUE -> {
                    Log.d("_isEmpty", "isEmpty.ISTRUE")
                    binding.layoutEmpty.layoutEmpty.visible()
                }
                isEmpty.ISFALSE -> {
                    Log.d("_isEmpty", "isEmpty.ISFALSE")
                    binding.layoutEmpty.layoutEmpty.gone()
                }
            }
        })
    }

    private fun initRv() {
        withdrawAdapter = WithdrawAdapter(this, object : WithdrawAdapter.onAdapterListener{
            override fun onClickde(data: GetWithdrawResponse.DataWithDraw.DataDetailWithDraw) {
                var type = if (data.transactionType!!.toLowerCase().equals("withdraw_to_me")) 1 else  0
                startActivity<WithdrawalDetailActivity>(
                        "_typePenarikan" to type,
                        "_idWithdraw" to data.id
                )
            }
        })
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPenarikan.apply {
            layoutManager = linearLayoutManager
            adapter = withdrawAdapter
        }
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        binding.toolbar.txtTitle.text = getString(R.string.text_penarikan_poin)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity<HomeActivity>(
                "isShopping" to true)
        finishAffinity()
    }
}