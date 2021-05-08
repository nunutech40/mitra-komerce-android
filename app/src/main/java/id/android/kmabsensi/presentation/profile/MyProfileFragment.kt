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
import id.android.kmabsensi.databinding.FragmentMyProfileBinding
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
    private lateinit var binding : FragmentMyProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        myDialog = MyDialog(context!!)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = vm.getUserData()

        if (user.role_id == 1) {
            binding.btnUbahProfile.gone()
            binding.divider1.gone()
        }


        binding.btnUbahProfile.setOnClickListener {
            context?.startActivity<UbahProfileActivity>(USER_KEY to user)
        }

        binding.btnLogout.setOnClickListener {
            vm.logout()
        }

        binding.btnUbahPassword.setOnClickListener {
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

        vm.userdData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    setProfile()
                }
                is UiState.Error -> {
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        vm.getProfileUserData(user.id)
    }


    private fun setProfile() {
        user = vm.getUserData()

        if (user.role_id == 1){
            binding.imgProfile.setImageResource(R.drawable.logo)
        } else {
            binding.imgProfile.loadCircleImage(
                user.photo_profile_url
                    ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
            )
        }

        binding.textNama.text = user.full_name
        binding.txtDivisi.text = user.division_name
        binding.txtPhone.text = user.no_hp
        binding.txtJabatan.text = user.position_name

    }


}
