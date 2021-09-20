package id.android.kmabsensi.presentation.komship.leads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.FragmentLeadsOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf

class LeadsOrderFragment : BaseFragmentRf<FragmentLeadsOrderBinding>(
    FragmentLeadsOrderBinding::inflate
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tiePartner?.setOnClickListener {
            setupBottomSheat()
        }
    }

    fun setupBottomSheat(){
        val btnSheet = layoutInflater.inflate(R.layout.custom_toolbar_v2, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(btnSheet)
        dialog.show()
    }
}