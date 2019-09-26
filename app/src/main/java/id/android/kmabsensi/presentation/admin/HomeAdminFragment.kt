package id.android.kmabsensi.presentation.admin


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.kantor.report.PresentasiReportKantorActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.fragment_home_admin.*
import org.jetbrains.anko.startActivity

/**
 * A simple [Fragment] subclass.
 */
class HomeAdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgProfile.loadCircleImage("https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg")
        txtHello.text = "Hello, Giselle"

        layoutReport.setOnClickListener {
            activity?.startActivity<PresentasiReportKantorActivity>()
        }

        btnKelolaDataKantor.setOnClickListener {
            activity?.startActivity<KelolaKantorActivity>()
        }

        btnKelolaDataSdm.setOnClickListener {
            activity?.startActivity<KelolaDataSdmActivity>()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeAdminFragment()
    }


}
