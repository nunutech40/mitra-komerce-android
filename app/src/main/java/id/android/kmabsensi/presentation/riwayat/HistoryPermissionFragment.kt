package id.android.kmabsensi.presentation.riwayat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.FragmentHistoryPermissionBinding
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.permission.PermissionItem
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.presentation.permission.detailizin.DetailIzinActivity
import id.android.kmabsensi.utils.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class HistoryPermissionFragment : Fragment() {
    private val vm: PermissionViewModel by inject()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var user: User? = null

    private var dateFrom: String = ""
    private var dateTo: String = ""
    private var permissionType: Int = 0
    private var skeletonScreen: SkeletonScreen? = null

    private lateinit var binding: FragmentHistoryPermissionBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateFrom = getTodayDate()
        dateTo = getTodayDate()
        user = homeViewModel.getUserData()
        user?.let { user ->
            vm.filterPermission(
                    userId = user.id,
                    dateFrom = "2021-01-10",
                    dateTo = dateTo,
                    permissionType = permissionType
            )
        }
        initRv()
        setupObserver()
    }

    private fun initRv() {
        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvPermission.apply {
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }

    private fun setupObserver() {
        vm.listPermissionData.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> {
                    if (binding.layoutEmpty.layoutEmpty.isVisible) binding.layoutEmpty.layoutEmpty.gone()
                    skeletonScreen = Skeleton.bind(binding.rvPermission)
                            .adapter(groupAdapter)
                            .load(R.layout.skeleton_list_permission)
                            .show()
                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    skeletonScreen?.hide()
                    if (it.data.data.isEmpty()) binding.layoutEmpty.layoutEmpty.visible() else binding.layoutEmpty.layoutEmpty.gone()
                    it.data.data.forEach {
                        groupAdapter.add(PermissionItem(requireContext(), it) {
                            startActivity(Intent(requireContext(), DetailIzinActivity::class.java)
                                    .putExtra(IS_FROM_MANAJEMEN_IZI, false)
                                    .putExtra(PERMISSION_DATA_KEY, it))
                        })
                    }
                }
                is UiState.Error -> {
                    skeletonScreen?.hide()
                }
            }
        })
    }

}