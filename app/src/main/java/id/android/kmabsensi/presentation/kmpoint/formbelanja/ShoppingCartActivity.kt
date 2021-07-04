package id.android.kmabsensi.presentation.kmpoint.formbelanja

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse.Data.DataListShopping
import id.android.kmabsensi.databinding.ActivityShoppingCartBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kmpoint.formbelanja.adapter.ShoppingFinanceAdapter
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.ShoppingDetailsFinanceActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader.ShoppingDetailLeaderActivity
import id.android.kmabsensi.presentation.kmpoint.tambahdaftarbelanja.AddShoppingListActivity
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.isEmpty
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class ShoppingCartActivity : BaseActivity() {
    private val _TAG = "_responseAllShopping"
    private val homeVM: HomeViewModel by inject()
    private val vm: ShoppingCartViewModel by inject()
    lateinit var user: User
    private val binding by lazy {
        ActivityShoppingCartBinding.inflate(layoutInflater) }
    private val isFinance by lazy {
        intent.getBooleanExtra("_isFinance", false)
    }
    private lateinit var shoppingFinanceAdapter : ShoppingFinanceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupListener()
        initRv()
        setupObserve()
    }

    private fun setupObserve() {
        if (isFinance) {
            vm.getAllShoppingListPaged(status = "approved").observe(this, {
                shoppingFinanceAdapter.submitList(it)
                Log.d("setupObserve", "setupObserve: ${it.size}, ${shoppingFinanceAdapter.currentList?.size} ")

            })
        }else {
            vm.getAllShoppingListPaged(user_request_id = user.id).observe(this, {
                shoppingFinanceAdapter.submitList(it)
                Log.d("setupObserve", "setupObserve: ${it.size}, ${shoppingFinanceAdapter.currentList?.size} ")
            })
        }

        vm.getState().observe(this, {
            when(it){
                State.LOADING-> {
                    showSkeletonPaging(binding.rvPenarikan, R.layout.skeleton_list_sdm, rvAdapter2 = shoppingFinanceAdapter)
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
        shoppingFinanceAdapter = ShoppingFinanceAdapter(this, object : ShoppingFinanceAdapter.onAdapterListener{
            override fun onClickde(data: DataListShopping) {
                if (user.position_name.toLowerCase().contains(getString(R.string.category_leader).toLowerCase())) {
                    startActivity<ShoppingDetailLeaderActivity>(
                            "idDetailSHopping" to data.id)
                } else {
                    startActivity<ShoppingDetailsFinanceActivity>(
                            "idDetailSHopping" to data.id)
                }
            }
        })
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPenarikan.apply {
            layoutManager = linearLayoutManager
            adapter = shoppingFinanceAdapter
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
        }
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.fabAddShoppingList.setOnClickListener {
            startActivity<AddShoppingListActivity>()
        }
    }

    private fun setupView() {
        user = homeVM.getUserData()
        binding.toolbar.txtTitle.text = getString(R.string.text_form_belanja)
        if (user.position_name.toLowerCase().contains(getString(R.string.category_leader).toLowerCase())) binding.fabAddShoppingList.visible()
        else binding.fabAddShoppingList.gone()
    }

    override fun onBackPressed() {
        startActivity<HomeActivity>(
                "isShopping" to true)
        finishAffinity()
        super.onBackPressed()
    }
}