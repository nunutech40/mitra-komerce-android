package id.android.kmabsensi.presentation.management


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber.e

import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.fragment_home_management.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class HomeManagementFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var user : User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home_management, container, false)

        user = vm.getUserData()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.dashboardData.observe(viewLifecycleOwner, Observer {
            when(it){
                is UiState.Success -> {
                    txtPresent.text = it.data.data.total_present.toString()
                    txtTotalUser.text = "/ ${it.data.data.total_user}"
                    txtNotPresent.text = "${it.data.data.total_not_present} orang belum hadir"
                }
                is UiState.Error -> {
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.getDashboardInfo(user.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgProfile.loadCircleImage(user.photo_profile_url ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg")
        txtHello.text = "Hello, ${user.full_name}"
        txtRoleName.text = user.role_name

        btnKelolaKaryawan.setOnClickListener {
            context?.startActivity<KelolaDataSdmActivity>("isManagement" to true,
                "userId" to user.id)
        }



    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeManagementFragment()
    }


}
