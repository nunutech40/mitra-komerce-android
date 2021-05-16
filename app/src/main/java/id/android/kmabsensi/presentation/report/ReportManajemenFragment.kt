package id.android.kmabsensi.presentation.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.FragmentReportManajemenBinding
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.report.performa.PilihPartnerActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.IS_CS
import id.android.kmabsensi.utils.USER_ID_KEY
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.fragment_report_manajemen.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportManajemenFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private lateinit var binding : FragmentReportManajemenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportManajemenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = vm.getUserData()
        if (user.position_name.toLowerCase().contains(getString(R.string.category_leader))) {
            binding.menuPerforma.visible()
        }
        if (user.position_name.toLowerCase().contains(getString(R.string.category_leader_advertiser))) {
            binding.menuAdvertiser.visible()
        }

        binding.btnAbsensi.setOnClickListener {
            activity?.startActivity<ReportAbsensiActivity>()
        }

        binding.btnPerforma.setOnClickListener {
            activity?.startActivity<PilihPartnerActivity>(
                USER_ID_KEY to user.id,
                IS_CS to true
            )
        }

        binding.btnPerformaAdvertiser.setOnClickListener {
            activity?.startActivity<PilihPartnerActivity>(
                USER_ID_KEY to user.id,
                IS_CS to false
            )
        }


    }

}