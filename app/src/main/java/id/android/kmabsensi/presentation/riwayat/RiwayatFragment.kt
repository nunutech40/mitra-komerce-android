package id.android.kmabsensi.presentation.riwayat


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.FragmentRiwayatBinding
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.invis
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_riwayat.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.toolbar.view.btnBack
import kotlinx.android.synthetic.main.toolbar.view.txtTitle
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class RiwayatFragment : Fragment() {

    private lateinit var binding : FragmentRiwayatBinding
    private val vm: RiwayatViewModel by inject()
    lateinit var user: User
    private val homeViewModel: HomeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.btnBack.gone()
        binding.toolbar.btnFilter.gone()
        binding.toolbar.txtTitle.visible()
        binding.toolbar.txtTitle.text = getString(R.string.text_riwayat)

        user = homeViewModel.getUserData()
        setupListener()
        vm.getPresenceHistory(user.id)
    }

    private fun updateToggle(type: Int, checked: Boolean) {
        if (type == 1){
            if (checked){
                binding.btnIzin.isChecked = !checked
                binding.navHistory.findNavController().navigate(R.id.action_historyPermissionFragment_to_historyPresenceFragment2)
                setDisableButton()
            }
        }else if (type == 0){
            if (checked){
                binding.btnKehadiran.isChecked = !checked
                binding.navHistory.findNavController().navigate(R.id.action_historyPresenceFragment2_to_historyPermissionFragment)
                setDisableButton()
            }
        }
    }

    private fun setDisableButton() {

        if (binding.btnIzin.isChecked){
            binding.btnIzin.isClickable = false
            binding.btnKehadiran.setTextColor(resources.getColor(R.color.cl_black))
        } else{
            binding.btnIzin.isClickable = true
            binding.btnKehadiran.setTextColor(resources.getColor(R.color.cl_white))
        }

        if (binding.btnKehadiran.isChecked) {
            binding.btnKehadiran.isClickable = false
            binding.btnIzin.setTextColor(resources.getColor(R.color.cl_black))
        } else {
            binding.btnKehadiran.isClickable = true
            binding.btnIzin.setTextColor(resources.getColor(R.color.cl_white))
        }
    }

    private fun setupListener() {
        binding.btnKehadiran.setOnClickListener {
            updateToggle(1, binding.btnKehadiran.isChecked)
        }
        binding.btnIzin.setOnClickListener {
            updateToggle(0, binding.btnIzin.isChecked)
        }
        binding.toolbar.btnFilter.setOnClickListener {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RiwayatFragment()
    }
}
