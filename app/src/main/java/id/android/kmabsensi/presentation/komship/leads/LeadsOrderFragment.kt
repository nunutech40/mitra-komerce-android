package id.android.kmabsensi.presentation.komship.leads

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.FragmentLeadsOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.utils.onWarningClicked
import id.android.kmabsensi.utils.showDialogWarningConfirm

class LeadsOrderFragment : BaseFragmentRf<FragmentLeadsOrderBinding>(
    FragmentLeadsOrderBinding::inflate
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tiePartner?.setOnClickListener {
//            showDialogWarningConfirm(requireContext(), object : onWarningClicked{
//                override fun onCLick() {
//
//                }
//            })
            setupBottomSheat()
        }
    }

    fun setupBottomSheat(){
        val bottomSheet = layoutInflater.inflate(R.layout.bottomsheet_filter_data_order, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheet)
        dialog.show()
    }
}
