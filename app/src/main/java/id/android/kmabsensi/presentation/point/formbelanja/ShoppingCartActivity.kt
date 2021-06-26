package id.android.kmabsensi.presentation.point.formbelanja

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.AllShoppingRequestParams
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse.Data.DataListShopping
import id.android.kmabsensi.databinding.ActivityShoppingCartBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.point.formbelanja.adapter.FormBelanjaItem
import id.android.kmabsensi.presentation.point.formbelanjadetailfinance.ShoppingDetailsActivity
import id.android.kmabsensi.presentation.point.formbelanjadetailleader.ShoppingDetailLeaderActivity
import id.android.kmabsensi.presentation.point.penarikan.WithdrawalListActivity
import id.android.kmabsensi.presentation.point.tambahdaftarbelanja.AddShoppingListActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class ShoppingCartActivity : BaseActivity() {
    private val homeVM: HomeViewModel by inject()
    private val vm: ShoppingCartViewModel by inject()
    lateinit var user: User
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var listShopping: ArrayList<DataListShopping> = arrayListOf()
    private var groupDataBelanja: ArrayList<FormBelanjaMainModel> = arrayListOf()
    private val binding by lazy { ActivityShoppingCartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupListener()
        initRv()
        setupObserve()
    }

    private fun setupObserve() {
        val params = AllShoppingRequestParams(user_requester_id = user.id)
        vm.getAllShoppingList(params).observe(this, {
            when (it) {
                is UiState.Loading -> Log.d("_responseTAG", "Loading 3 .. ")

                is UiState.Success -> {
                    Log.d("_responseTAG", "listShopping 3: ${it?.data.message}")
                    it.data.data.let {
                        Log.d("_responseTAG", "listShopping 3: ${it?.data}")
                        setupDataList(it)
                        if (it?.data?.size == 0) binding.layoutEmpty.layoutEmpty.visible() else binding.layoutEmpty.layoutEmpty.gone()
                    }
                }
                is UiState.Error -> Log.d("_responseTAG", "Error3 ${it.throwable} ")
            }
        })
    }

    private fun setupDataList(data: AllShoppingRequestResponse.Data?) {
        listShopping.addAll(data?.data!!)
        /**
        set Date for header list
        */
        var date = ""
        groupDataBelanja.clear()
        listShopping.forEach {
            var type = 0
            if (!date.equals(it.createdAt!!.split(" ")[0])) {
                type = WithdrawalListActivity.TYPE_HEADER
                date = it.createdAt.split(" ")[0]
            } else {
                type = WithdrawalListActivity.TYPE_WITHDRAWAL
            }
            groupDataBelanja.add(
                    FormBelanjaMainModel(
                            type,
                            it
                    )
            )
        }
        groupAdapter.clear()
        groupDataBelanja.forEach {
            groupAdapter.add(FormBelanjaItem(this, it) {
                if (user.position_name.toLowerCase().contains(getString(R.string.category_leader).toLowerCase())) {
                    startActivity<ShoppingDetailLeaderActivity>(
                            "idDetailSHopping" to it.data.id)
                    Log.d("TAGidDetailSHopping", "setupDataList 1: ${it.data.id}")
                } else {
                    startActivity<ShoppingDetailsActivity>(
                            "idDetailSHopping" to it.data.id)
                    Log.d("TAGidDetailSHopping", "setupDataList: ${it.data.id}")
                }
            })
        }
    }

    private fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPenarikan.apply {
            layoutManager = linearLayoutManager
            adapter = groupAdapter
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
        user.partner_assignments?.forEach {
        }
        binding.toolbar.btnSearch.visible()
        binding.toolbar.txtTitle.text = getString(R.string.text_form_belanja)
        if (user.position_name.toLowerCase().contains(getString(R.string.category_leader).toLowerCase())) binding.fabAddShoppingList.visible() else binding.fabAddShoppingList.gone()
    }

    override fun onBackPressed() {
        startActivity<HomeActivity>()
        super.onBackPressed()
    }
}