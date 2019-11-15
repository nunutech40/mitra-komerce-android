package id.android.kmabsensi.presentation.sdm.home


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
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.permission.PermissionActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_sdm.*
import kotlinx.android.synthetic.main.fragment_home_sdm.imgProfile
import kotlinx.android.synthetic.main.fragment_home_sdm.txtHello
import kotlinx.android.synthetic.main.fragment_home_sdm.txtRoleName
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class HomeSdmFragment : Fragment() {

    private val vm: HomeViewModel by inject()

    private lateinit var user: User

    private lateinit var myDialog: MyDialog

    var isCheckin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_sdm, container, false)

        user = vm.getUserData()
        myDialog = MyDialog(context!!)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.dashboardData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    txtPresent.text = it.data.data.total_present.toString()
                    txtTotalUser.text = " /${it.data.data.total_user}"
                    txtNotPresent.text = "${it.data.data.total_not_present}"
                }
                is UiState.Error -> {
                    progressBar.gone()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.presenceCheckState.observe(viewLifecycleOwner, Observer {
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
                            context?.startActivity<CekJangkauanActivity>(DATA_OFFICE_KEY to it.data.office_assigned,
                                PRESENCE_ID_KEY to it.data.presence_id)
                        }

                    } else {
                        if (isCheckin){
                            //checkin
                            context?.startActivity<CekJangkauanActivity>(DATA_OFFICE_KEY to it.data.office_assigned)
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

        vm.checkoutState.observe(viewLifecycleOwner, Observer {
            when(it){
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    createAlertSuccess(activity, it.data.message)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        vm.getDashboardInfo(user.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupGreetings()

        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )
        txtHello.text = "Hello, ${user.full_name}"
        txtRoleName.text = getRoleName(user.role_id).capitalize()

        btnCheckIn.setOnClickListener {
            isCheckin = true
            vm.presenceCheck(user.id)
        }

        btnCheckOut.setOnClickListener {
            isCheckin = false
            vm.presenceCheck(user.id)
        }

        btnFormIzin.setOnClickListener {
            context?.startActivity<PermissionActivity>(USER_KEY to user)
        }

    }

    private fun setupGreetings(){
        val (greeting, header) = (activity as HomeActivity).setGreeting()
        txtHello.text = greeting
        header_waktu.setImageResource(header)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeSdmFragment()
    }


}
