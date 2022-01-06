package id.android.kmabsensi.presentation.profile


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.github.ajalt.timberkt.Timber
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.AllBankResponse
import id.android.kmabsensi.data.remote.response.DataBank
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.FragmentMyProfileBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.komboard.KomboardActivity
import id.android.kmabsensi.presentation.login.LoginActivity
import id.android.kmabsensi.presentation.sdm.editpassword.EditPasswordActivity
import id.android.kmabsensi.presentation.ubahprofile.UbahProfileActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFragment : BaseFragmentRf<FragmentMyProfileBinding>(
    FragmentMyProfileBinding::inflate
) {

    private val vm: HomeViewModel by sharedViewModel()
    private lateinit var myDialog: MyDialog

    lateinit var user: User
    var allBankResponse: AllBankResponse ? =null
//
    companion object {
        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
        setupListener()
        setupToolbar("Akun", isBgWhite = true)
    }

    private fun setupView(){
        myDialog = MyDialog(requireContext())
        user = vm.getUserData()
        Log.d("TAG", "setupView: $allBankResponse")
        binding?.apply {
            swSavePhoto.isChecked = PreferencesHelper(requireContext()).getBoolean(IS_SAVE_PHOTO) ?: true
            if (user.role_id == 1) {
                btnChangeProfile.gone()
            }

            if (user.position_id != 1){
                llKomboard.gone()
            }
        }
    }

    private fun setupObserver(){
        vm.logoutState.observe(viewLifecycleOwner, {
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
                        createAlertError(requireActivity(), "Failed", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    Timber.e(it.throwable)
                }
            }
        })

        vm.allBankData.observe(viewLifecycleOwner, { state ->
            when(state) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    setAlBank()
                }
                is UiState.Error -> {
                }
            }
        })

        vm.userdData.observe(viewLifecycleOwner, { state ->
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

    private fun setAlBank() {
        allBankResponse = vm.getAllBankData()
        Log.d("TAG", "setAlBank: $allBankResponse")
    }

    private fun setupListener(){
        binding?.apply {
            swSavePhoto.setOnCheckedChangeListener{ _, isChecked ->
                PreferencesHelper(requireContext()).saveBoolean(IS_SAVE_PHOTO, isChecked)
            }
            btnChangeProfile.setOnClickListener {
                context?.startActivity<UbahProfileActivity>(
                    USER_KEY to user,
                    BANK_KEY to allBankResponse
                )
            }

            btnLogOut.setOnClickListener {
                vm.logout()
            }

            btnForgotPass.setOnClickListener {
                context?.startActivity<EditPasswordActivity>(USER_ID_KEY to user.id)
            }

            btnKomboard.setOnClickListener {
                context?.startActivity<KomboardActivity>()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.getAllBanks()
        vm.getProfileUserData(user.id)
    }


    private fun setProfile() {
        user = vm.getUserData()
        binding?.apply {
            if (user.role_id == 1){
                imgProfile.setImageResource(R.drawable.logo)
            } else {
                imgProfile.loadCircleImage(
                    user.photo_profile_url
                        ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
                )
            }
            tvUsername.text = user.full_name
            tvPosition.text = user.position_name
        }
    }


}
