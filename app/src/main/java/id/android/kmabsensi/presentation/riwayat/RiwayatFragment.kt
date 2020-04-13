package id.android.kmabsensi.presentation.riwayat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.invis
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.fragment_riwayat.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class RiwayatFragment : Fragment() {

    private val vm: RiwayatViewModel by inject()
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    lateinit var user: User

    private var skeletonScreen: SkeletonScreen? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.riwayatResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonScreen = Skeleton.bind(rvRiwayat)
                        .adapter(groupAdapter)
                        .load(R.layout.skeleton_list_riwayat)
                        .show()
                    if (layout_empty.isVisible) layout_empty.invis()
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    skeletonScreen?.hide()
                    groupAdapter.clear()
                    if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    it.data.data.forEach {
                        groupAdapter.add(RiwayatItem(it, user.full_name, user.photo_profile_url))
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                    skeletonScreen?.hide()
                }
            }
        })

        user = homeViewModel.getUserData()
        vm.getPresenceHistory(user.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        swipeRefresh.setOnRefreshListener {

            vm.getPresenceHistory(user.id)
        }

    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(context)
        rvRiwayat.apply {
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RiwayatFragment()
    }


}
