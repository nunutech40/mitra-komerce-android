package id.android.kmabsensi.presentation.komship.dataorder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.komship.OrderByPartnerParams
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.data.remote.response.komship.OrderItem
import id.android.kmabsensi.databinding.FragmentHistoryOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.presentation.komship.partnerOrder
import id.android.kmabsensi.utils.UiState
import org.koin.android.ext.android.inject

class DataOrderFragment : BaseFragmentRf<FragmentHistoryOrderBinding>(
    FragmentHistoryOrderBinding::inflate
) {
    private val vm : MyOrderViewModel by inject()
    private var dataPartner : MutableList<KomPartnerItem> = ArrayList()
    private lateinit var orderAdapter : DataOrderAdapter
    private var listOrder : MutableList<OrderItem> = ArrayList()
    private lateinit var mLayoutManager: LinearLayoutManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListsner()
        setupObserver()
        setupList()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferencesHelper(requireContext()).saveInt(partnerOrder, 0)
    }

    private fun setupList(){
        mLayoutManager = LinearLayoutManager(requireContext())
        orderAdapter = DataOrderAdapter(requireContext(), object : DataOrderAdapter.onAdapterListener{
            override fun onCLick(data: OrderItem) {

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
                is UiState.Loading -> Log.d("_partnerState", "on loading")
                is UiState.Success -> {
                    dataPartner.addAll(it.data.data!!)
                    setupSpinnerPartner(it.data.data)
                }
                is UiState.Error -> Log.d("_partnerState", "on error ${it.throwable}")
            }
        })
        vm.orderByPartnerState.observe(requireActivity(),{
            when(it){
                is UiState.Loading -> Log.d("_orderByPartnerState", "on Loading")
                is UiState.Success -> {
                    for (id in 1..10){
                        listOrder.addAll(it.data.data?.data!!)
                    }
                    orderAdapter.setData(listOrder)

                }
                is UiState.Error -> Log.d("_orderByPartnerState", "on Error ${it.throwable}")
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
        }
    }

    fun filterOrder(idPartner: Int, params: OrderByPartnerParams){
        vm.getOrderByPartner(idPartner, params)
    }

    fun searchOrder(char: String){
        orderAdapter.filter.filter(char)
    }


    private fun setupSpinnerPartner(data: List<KomPartnerItem>?) {
        var partner = ArrayList<String>()
        data?.forEach {
            partner.add(it.partnerName!!)
        }

        ArrayAdapter<String>(requireContext(), R.layout.spinner_item, partner)
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
                            PreferencesHelper(requireContext()).saveInt(partnerOrder, 201)
                            vm.getOrderByPartner(201, OrderByPartnerParams(1,
                                "2021-09-08",
                                "2021-09-30",
                            "COD",
                            0))
//                            vm.getProduct(dataPartner[position].partnerId!!)
                        }
                    }
            }
    }
}