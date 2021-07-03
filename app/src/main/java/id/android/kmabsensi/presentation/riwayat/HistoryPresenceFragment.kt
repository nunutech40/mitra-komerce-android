package id.android.kmabsensi.presentation.riwayat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.FragmentHistoryPresenceBinding
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.invis
import id.android.kmabsensi.utils.visible
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HistoryPresenceFragment : Fragment() {

    private lateinit var binding: FragmentHistoryPresenceBinding

    private val vm: RiwayatViewModel by inject()
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private val adapter = GroupAdapter<GroupieViewHolder>()

    lateinit var user: User

    private var skeletonScreen: SkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance() = RiwayatFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryPresenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = homeViewModel.getUserData()
        setupListener()
    }

    private fun setupListener() {
        binding.swipeRefresh.setOnRefreshListener {
            vm.getPresenceHistory(user.id)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRv()
        vm.getPresenceHistory(user.id)
        setupObserver()
    }

    private fun setupObserver() {
        vm.riwayatResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonScreen = Skeleton.bind(binding.rvRiwayat)
                        .adapter(adapter)
                        .load(R.layout.skeleton_list_riwayat)
                        .show()
                    if (binding.layoutEmpty.layoutEmpty.isVisible) binding.layoutEmpty.layoutEmpty.invis()
                }
                is UiState.Success -> {
                    binding.swipeRefresh.isRefreshing = false

                    skeletonScreen?.hide()
                    adapter.clear()

                    if (it.data.data.isEmpty()) binding.layoutEmpty.layoutEmpty.visible() else binding.layoutEmpty.layoutEmpty.gone()

                    it.data.data.forEach {
                        Log.d(
                            "_history",
                            "history = ${user.full_name} url Photo = ${user.photo_profile_url}"
                        )
                        adapter.add(RiwayatItem(it))
                    }
                }
                is UiState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    skeletonScreen?.hide()
                }
            }
        })
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvRiwayat.apply {
            layoutManager = linearLayoutManager
            adapter = adapter
        }
    }

}