package id.android.kmabsensi.presentation.management


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.timberkt.Timber.e

import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.CekJangkauanActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.loadCircleImage
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_management.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class HomeManagementFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var user: User

    private lateinit var myDialog: MyDialog

    var isCheckin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_management, container, false)

        user = vm.getUserData()
        myDialog = MyDialog(context!!)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.dashboardData.observe(viewLifecycleOwner, Observer {
            when (it) {
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

        vm.presenceCheckResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.checkdeIn){
                        if (isCheckin){
                            MaterialDialog(context!!).show {
                                cornerRadius(16f)
                                title(text = "Check-In")
                                message(text = "Anda sudah check-in hari ini")
                                positiveButton(text = "OK"){
                                    it.dismiss()
                                }
                            }
                        } else {
                            //checkout
                            context?.startActivity<CekJangkauanActivity>("data" to it.data.office_assigned,
                                "absenId" to it.data.presence_id)
                        }

                    } else {
                        if (isCheckin){
                            //checkin
                            context?.startActivity<CekJangkauanActivity>("data" to it.data.office_assigned)
                        } else {
                            MaterialDialog(context!!).show {
                                cornerRadius(16f)
                                title(text = "Tidak bisa Check-Out")
                                message(text = "Silahkan checkin terlebih dahulu.")
                                positiveButton(text = "OK"){
                                    it.dismiss()
                                }
                            }
                        }

                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.getDashboardInfo(user.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )
        txtHello.text = "Hello, ${user.full_name}"
        txtRoleName.text = user.role_name

        btnKelolaKaryawan.setOnClickListener {
            context?.startActivity<KelolaDataSdmActivity>(
                "isManagement" to true,
                "userId" to user.id
            )
        }

        btnCheckIn.setOnClickListener {
            isCheckin = true
            vm.presenceCheck(user.id)
        }

        btnCheckOut.setOnClickListener {
            isCheckin = false
            vm.presenceCheck(user.id)
        }


    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeManagementFragment()
    }


}
