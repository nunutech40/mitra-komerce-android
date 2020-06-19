package id.android.kmabsensi.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.jabatan.ManajemenJabatanActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.IS_MANAGEMENT_KEY
import kotlinx.android.synthetic.main.fragment_home_admin.*
import kotlinx.android.synthetic.main.fragment_sdm_menu.*
import kotlinx.android.synthetic.main.fragment_sdm_menu.btnKelolaSdm
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.btnBack
import org.jetbrains.anko.startActivity

/*
 * sementara untuk role admin
 */
class SdmMenuFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sdm_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            (parentFragment as HomeAdminFragment).hideGroupMenu()
        }

        btnKelolaIzin.setOnClickListener {
            activity?.startActivity<ManajemenIzinActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnKelolaSdm.setOnClickListener {
            activity?.startActivity<KelolaDataSdmActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnKelolaJabatan.setOnClickListener {
            activity?.startActivity<ManajemenJabatanActivity>()
        }
    }
}