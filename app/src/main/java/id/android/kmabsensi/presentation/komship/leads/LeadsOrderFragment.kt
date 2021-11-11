package id.android.kmabsensi.presentation.komship.leads

import android.os.Bundle
import android.view.View
import id.android.kmabsensi.databinding.FragmentLeadsOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf

class LeadsOrderFragment : BaseFragmentRf<FragmentLeadsOrderBinding>(
    FragmentLeadsOrderBinding::inflate
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            tiePartner.setOnClickListener {
            }
        }

    }
}
