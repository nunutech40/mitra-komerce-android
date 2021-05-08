package id.android.kmabsensi.presentation.report


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.FragmentReportBinding
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kantor.report.PresentasiReportKantorActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.fragment_report.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private lateinit var binding : FragmentReportBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReportKantor.setOnClickListener {
            activity?.startActivity<PresentasiReportKantorActivity>(CATEGORY_REPORT_KEY to 0)
        }

        binding.btnReportSdm.setOnClickListener {
            activity?.startActivity<PresentasiReportKantorActivity>(CATEGORY_REPORT_KEY to 2,
                IS_MANAGEMENT_KEY to (vm.getUserData().role_id == 2),
                USER_KEY to vm.getUserData())
        }

        binding.btnReportManajemen.setOnClickListener {
            activity?.startActivity<PresentasiReportKantorActivity>(CATEGORY_REPORT_KEY to 1)
        }

        if (vm.getUserData().role_id == 2){
            binding.btnReportManajemen.gone()
        }


    }

    companion object {
        @JvmStatic
        fun newInstance() = ReportFragment()
    }


}
