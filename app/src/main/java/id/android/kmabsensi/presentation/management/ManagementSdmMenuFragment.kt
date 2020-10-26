package id.android.kmabsensi.presentation.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.admin.HomeAdminFragment
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.jabatan.ManajemenJabatanActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.presentation.sdm.dataalfa.DataAlfaActivity
import id.android.kmabsensi.presentation.sdm.device.DataDeviceActivity
import id.android.kmabsensi.presentation.sdm.holiday.HolidayActivity
import id.android.kmabsensi.presentation.sdm.modekerja.ModeKerjaActivity
import id.android.kmabsensi.presentation.sdm.nonjob.SdmNonJobActivity
import id.android.kmabsensi.utils.IS_MANAGEMENT_KEY
import id.android.kmabsensi.utils.USER_ID_KEY
import kotlinx.android.synthetic.main.fragment_sdm_menu.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.btnBack
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ManagementSdmMenuFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_management_sdm_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = vm.getUserData()

        btnBack.setOnClickListener {
            (parentFragment as HomeManagementFragment).hideGroupMenu()
        }

        btnKelolaIzin.setOnClickListener {
            if (user.position_id == 3 || user.position_id == 4 || user.position_id == 5) {
                activity?.startActivity<ManajemenIzinActivity>(IS_MANAGEMENT_KEY to false)
            } else {
                activity?.startActivity<ManajemenIzinActivity>(
                    IS_MANAGEMENT_KEY to true,
                    USER_ID_KEY to user.id
                )
            }
        }

        btnKelolaSdm.setOnClickListener {
            if (user.position_id == 3 || user.position_id == 4 || user.position_id == 5) {
                context?.startActivity<KelolaDataSdmActivity>(
                    IS_MANAGEMENT_KEY to false
                )
            } else {
                context?.startActivity<KelolaDataSdmActivity>(
                    IS_MANAGEMENT_KEY to true,
                    USER_ID_KEY to user.id
                )
            }
        }

        btnKelolaJabatan.setOnClickListener {
            activity?.startActivity<ManajemenJabatanActivity>()
        }

        btnSdmNonJob.setOnClickListener {
            activity?.startActivity<SdmNonJobActivity>()
        }

        btnModeKerja.setOnClickListener {
            activity?.startActivity<ModeKerjaActivity>()
        }

        btnDataDevice.setOnClickListener {
            activity?.startActivity<DataDeviceActivity>()
        }

        btnHariLibur.setOnClickListener {
            activity?.startActivity<HolidayActivity>()
        }

        btnDataAlfa.setOnClickListener {
            activity?.startActivity<DataAlfaActivity>()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ManagementSdmMenuFragment().apply {
            }
    }
}