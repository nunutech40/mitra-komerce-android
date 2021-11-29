package id.android.kmabsensi.presentation.komship.dataorder

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.komship.OrderByPartnerParams
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.data.remote.response.komship.OrderItem
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.presentation.komship.detaildataorder.DetailDataOrderActivity
import id.android.kmabsensi.presentation.komship.partnerOrder
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.getTodayDate
import id.android.kmabsensi.databinding.FragmentHistoryOrderBinding
import id.android.kmabsensi.utils.getSevenDayDate
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class DataOrderFragment : BaseFragmentRf<FragmentHistoryOrderBinding>(
    FragmentHistoryOrderBinding::inflate
) {
    private val vm : MyOrderViewModel by inject()
    private var dataPartner : MutableList<KomPartnerItem> = ArrayList()
    private lateinit var orderAdapter : DataOrderAdapter
    private var listOrder : MutableList<OrderItem> = ArrayList()
    private var listOrderShow : MutableList<OrderItem> = ArrayList()
    private lateinit var mLayoutManager: LinearLayoutManager

    private var idPartner = 0
    private var pageOrder = 1
    private var lastPage = 0
    private var filterMode = false
    private var startDate = ""
    private var endDate = ""
    private var payMethod = ""
    private var statusOrder: Int = 0


    private var sklList: SkeletonScreen? = null
    private var sklPartner: SkeletonScreen? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupList()
        setupListsner()
        setupObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferencesHelper(requireContext()).saveInt(partnerOrder, 0)
    }

    private fun setupList(){
        mLayoutManager = LinearLayoutManager(requireContext())
        orderAdapter = DataOrderAdapter(requireContext(), object : DataOrderAdapter.onAdapterListener{
            override fun onCLick(data: OrderItem) {
                requireActivity().startActivity<DetailDataOrderActivity>(
                    "_idPartner" to idPartner,
                    "_dataOrder" to data
                )
            }
        })
        binding?.rvDataOrder?.apply {
            adapter = orderAdapter
            layoutManager = mLayoutManager
            setHasFixedSize(true)
        }
    }

    private fun setupView(){
        vm.getPartner()
    }

    private fun setupObserver(){
        vm.partnerState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> {
                    Timber.tag("_partnerState").d("on loading")
                    showSkeleton()
                }
                is UiState.Success -> {
                    sklPartner?.hide()
                    dataPartner.clear()
                    dataPartner.addAll(it.data.data!!)
                    setupSpinnerPartner(it.data.data)
                }
                is UiState.Error -> {
                    sklPartner?.hide()
                    Timber.d(it.throwable)
                }
            }
        })
        vm.orderByPartnerState.observe(requireActivity(),{
            when(it){
                is UiState.Loading -> {
                    Timber.tag("_orderByPartnerState").d( "on Loading")
                }
                is UiState.Success -> {
                    binding?.srDataOrder?.isRefreshing = false
                    listOrder.clear()
                    listOrder.addAll(it.data.data?.data!!)
                    listOrderShow.addAll(listOrder)
                    orderAdapter.setData(listOrderShow)
                    lastPage = it.data.data?.lastPage!!
                    sklList?.hide()
                    binding?.progressMore?.visibility = View.GONE
                }
                is UiState.Error -> {
                    Timber.tag("_orderByPartnerState").d( "on error ${it.throwable}")
                    sklList?.hide()
                    binding?.srDataOrder?.isRefreshing = false
                    Timber.d(it.throwable)
                }
            }
        })
    }

    private fun setupListsner(){
        binding?.apply {
            btnToTheTop.setOnClickListener {
                mLayoutManager.scrollToPositionWithOffset(0, 0)
                mLayoutManager.isSmoothScrolling
                rvDataOrder.smoothScrollToPosition(0)
            }
            srDataOrder.setOnRefreshListener {
                srDataOrder.isRefreshing = true
                vm.getPartner()
                pageOrder = 1
                filterMode = false
                binding?.progressMore?.visibility = View.GONE
                listOrderShow.clear()
            }
            rvDataOrder.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val itemOn = mLayoutManager.childCount
                    val itemonVisible = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val dataCount = orderAdapter.itemCount
                    if ((itemOn+itemonVisible) >= dataCount && pageOrder < lastPage){
                        pageOrder += 1
                        binding?.progressMore?.visibility = View.VISIBLE
                        when(filterMode){
                            true -> vm.getMoreOrderByPartner(
                                idPartner, OrderByPartnerParams(
                                    pageOrder,
                                    startDate,
                                    endDate,
                                    payMethod,
                                    if (statusOrder < 0) null else statusOrder
                                )
                            )
                            false -> vm.getMoreOrderByPartner(
                                idPartner, OrderByPartnerParams(
                                    pageOrder,
                                    getSevenDayDate(),
                                    getTodayDate()
                                )
                            )
                        }
                    }
                    binding?.progressMore?.visibility = View.GONE
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
    }

    fun filterOrder(idPartner: Int, params: OrderByPartnerParams){
        filterMode = true
        vm.getOrderByPartner(idPartner, params)
        listOrderShow.clear()
        startDate = params.startDate
        endDate = params.lastDate
        payMethod = params.paymentMethode.toString()
        if (params.orderStatus == null){
            statusOrder = -1
        }else{
            statusOrder = params.orderStatus
        }
        lastPage = 0
        pageOrder = 1
    }

    fun searchOrder(char: String){
        orderAdapter.filter.filter(char)
    }


    private fun setupSpinnerPartner(data: List<KomPartnerItem>?) {
        val partner = ArrayList<String>()
        data?.forEach {
            partner.add(it.partnerName!!)
        }

        ArrayAdapter(requireContext(), R.layout.spinner_item, partner)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.spPartner?.adapter = adapter

                binding?.spPartner?.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            idPartner = vm.getIdPartnerFromList(dataPartner, position)
                            PreferencesHelper(requireContext()).saveInt(partnerOrder, idPartner)
                            vm.getOrderByPartner(idPartner, OrderByPartnerParams(1,
                                getSevenDayDate(),
                                getTodayDate()))
                        }
                    }
            }
    }

    private fun showSkeleton(){
        if (sklList == null){
            sklList = Skeleton.bind(binding?.rvDataOrder)
                .adapter(orderAdapter)
                .load(R.layout.skeleton_list_permission)
                .show()
            sklPartner = Skeleton.bind(binding?.spPartner)
                .load(R.layout.skeleton_item_big)
                .show()
        }else{
            sklList?.show()
            sklPartner?.show()
        }
    }
}