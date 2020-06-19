package id.android.kmabsensi.presentation.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.login.LoginActivity
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordActivity
import id.android.kmabsensi.presentation.ubahprofile.UbahProfileActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_admin.imgProfile
import kotlinx.android.synthetic.main.fragment_my_profile.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private lateinit var myDialog: MyDialog

    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)


        myDialog = MyDialog(context!!)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = vm.getUserData()

        if (user.role_id == 1) {
            btnUbahProfile.gone()
            divider1.gone()
        }


        btnUbahProfile.setOnClickListener {
            context?.startActivity<UbahProfileActivity>(USER_KEY to user)
        }

        btnLogout.setOnClickListener {
            vm.logout()
        }

        btnUbahPassword.setOnClickListener {
            context?.startActivity<EditPasswordActivity>(USER_ID_KEY to user.id)
        }

        vm.logoutState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        context?.startActivity<LoginActivity>()
                        vm.clearPref()
                        activity?.finish()
                    } else {
                        createAlertError(activity!!, "Failed", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    Timber.e(it.throwable)
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()

        user = vm.getUserData()

        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )
        textNama.text = user.full_name
        txtDivisi.text = user.division_name
        txtPhone.text = user.no_hp
        txtJabatan.text = user.position_name

    }


}
