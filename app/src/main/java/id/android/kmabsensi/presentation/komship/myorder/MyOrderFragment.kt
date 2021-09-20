package id.android.kmabsensi.presentation.komship.myorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.FragmentMyOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.custom.style
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class MyOrderFragment : BaseFragmentRf<FragmentMyOrderBinding>(
    FragmentMyOrderBinding::inflate
) {
    private val vm : MyOrderViewModel by inject()
    private var totalProduct = 0
    private var maxProduct = 10
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupView()
    }

    private fun setupView() {
        binding?.llVariant1?.visible()
        for (idx in 0..10){
            val chip = Chip(binding?.chipVarian1?.context)
            chip.apply {
                text = "item ke $idx"
                isClickable = true
                isCheckable = true
                chipCornerRadius = 5f
                isCheckedIconVisible = false
                setChipBackgroundColor(AppCompatResources.getColorStateList(requireContext(), R.color.cl_white))
                setChipStrokeColorResource(R.color.cl_grey_2)
                setChipStrokeWidthResource(R.dimen._1dp)
            }

            binding?.chipVarian1?.addView(chip)
        }
    }

    private fun setupListener() {
        binding?.apply {
            btnPlus.setOnClickListener {
                if (vm.validateMaxProduct(totalProduct, maxProduct)){
                    totalProduct++
                    tvTotal.text = totalProduct.toString()
                }else{
                    requireActivity().toast("sudah mencapai batas max")
                }
            }
            btnMinus.setOnClickListener {
                if (vm.validateMinProduct(totalProduct)){
                    totalProduct--
                    tvTotal.text = totalProduct.toString()
                }else{
                    requireActivity().toast("sudah mencapai batas min")
                }
            }
        }
    }


}