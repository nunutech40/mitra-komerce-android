package id.android.kmabsensi.presentation.report


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kantor.report.PresentasiReportKantorActivity
import id.android.kmabsensi.utils.CATEGORY_REPORT_KEY
import id.android.kmabsensi.utils.gone
import kotlinx.android.synthetic.main.fragment_report.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnReportKantor.setOnClickListener {
            activity?.startActivity<PresentasiReportKantorActivity>(CATEGORY_REPORT_KEY to 0)
        }

        btnReportSdm.setOnClickListener {
            activity?.startActivity<PresentasiReportKantorActivity>(CATEGORY_REPORT_KEY to 2)
        }

        btnReportManajemen.setOnClickListener {
            activity?.startActivity<PresentasiReportKantorActivity>(CATEGORY_REPORT_KEY to 1)
        }

        if (vm.getUserData().role_id == 2){
            btnReportManajemen.gone()
        }


    }

    companion object {
        @JvmStatic
        fun newInstance() = ReportFragment()
    }


}
