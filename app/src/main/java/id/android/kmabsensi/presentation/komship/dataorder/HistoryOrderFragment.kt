package id.android.kmabsensi.presentation.komship.dataorder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.databinding.FragmentHistoryOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.utils.UiState
import org.koin.android.ext.android.inject

class HistoryOrderFragment : BaseFragmentRf<FragmentHistoryOrderBinding>(
    FragmentHistoryOrderBinding::inflate
) {
    private val vm : MyOrderViewModel by inject()
    private var dataPartner : MutableList<KomPartnerItem> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListsner()
        setupObserver()
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
    }

    private fun setupListsner(){
        binding?.apply {
            btnToTheTop.setOnClickListener {
                rvDataOrder.scrollTo(0,0)
            }
        }
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
                            vm.getProduct(201)
//                            vm.getProduct(dataPartner[position].partnerId!!)
                        }
                    }
            }
    }
}