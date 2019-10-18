package id.android.kmabsensi.presentation.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.login.LoginActivity
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordActivity
import id.android.kmabsensi.presentation.ubahprofile.UbahProfileActivity
import id.android.kmabsensi.utils.USER_KEY
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.fragment_home_admin.imgProfile
import kotlinx.android.synthetic.main.fragment_my_profile.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = vm.getUserData()

        if (user.role_id == 1){
            btnUbahProfile.gone()
            divider1.gone()
        }


        btnUbahProfile.setOnClickListener {
            context?.startActivity<UbahProfileActivity>(USER_KEY to user)
        }

        btnLogout.setOnClickListener {
            context?.startActivity<LoginActivity>()
            vm.clearPref()
            activity?.finish()
        }

        btnUbahPassword.setOnClickListener {
            context?.startActivity<EditPasswordActivity>(USER_KEY to user)
        }

    }

    override fun onResume() {
        super.onResume()

        user = vm.getUserData()

        imgProfile.loadCircleImage(user.photo_profile_url ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg")
        textNama.text = user.full_name
        txtDivisi.text = user.division_name
        txtPhone.text = user.no_hp
        txtJabatan.text = user.position_name

    }


}
