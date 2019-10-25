package id.android.kmabsensi.presentation.admin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.jabatan.ManajemenJabatanActivity
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.kantor.report.PresentasiReportKantorActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import kotlinx.android.synthetic.main.fragment_home_admin.btnManajemenIzin
import kotlinx.android.synthetic.main.fragment_home_admin.imgProfile
import kotlinx.android.synthetic.main.fragment_home_admin.txtHello
import kotlinx.android.synthetic.main.fragment_home_admin.txtNotPresent
import kotlinx.android.synthetic.main.fragment_home_admin.txtPresent
import kotlinx.android.synthetic.main.fragment_home_admin.txtRoleName
import kotlinx.android.synthetic.main.fragment_home_admin.txtTotalUser
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class HomeAdminFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var user : User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_admin, container, false)

        user = vm.getUserData()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.dashboardData.observe(viewLifecycleOwner, Observer {
            when(it){
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    txtPresent.text = it.data.data.total_present.toString()
                    txtTotalUser.text = "/ ${it.data.data.total_user}"
                    txtNotPresent.text = "${it.data.data.total_not_present} orang belum hadir"
                }
                is UiState.Error -> {
                    progressBar.gone()
                    e { it.throwable.message.toString() }
                }
            }
        })


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getDashboardInfo(user.id)

        imgProfile.loadCircleImage(user.photo_profile_url ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg")
        txtHello.text = "Hello, ${user.full_name}"
        txtRoleName.text = getRoleName(user.role_id).capitalize()

        btnKelolaDataKantor.setOnClickListener {
            activity?.startActivity<KelolaKantorActivity>()
        }

        btnKelolaDataSdm.setOnClickListener {
            activity?.startActivity<KelolaDataSdmActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnManajemenIzin.setOnClickListener {
            activity?.startActivity<ManajemenIzinActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnManajemenJabatan.setOnClickListener {
            activity?.startActivity<ManajemenJabatanActivity>()
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            txtPresent.text = ""
            txtTotalUser.text = ""
            txtNotPresent.text = ""
            vm.getDashboardInfo(user.id)

        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeAdminFragment()
    }


}
