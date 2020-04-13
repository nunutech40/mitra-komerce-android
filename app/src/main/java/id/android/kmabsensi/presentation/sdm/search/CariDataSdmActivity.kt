package id.android.kmabsensi.presentation.sdm.search

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.presentation.sdm.SdmItem
import id.android.kmabsensi.presentation.sdm.detail.DetailKaryawanActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_cari_data_sdm.*
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject


class CariDataSdmActivity : AppCompatActivity() {

    private val vm: KelolaDataSdmViewModel by inject()
    private val groupAdapter = GroupAdapter<ViewHolder>()
    var dataFilter: List<User> = listOf()

    private var skeletonScreen: SkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cari_data_sdm)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cari Data SDM"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        iniSearcView()
        vm.getUserData(0, 0)
        vm.userData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonScreen = Skeleton.bind(rvSdm)
                        .adapter(groupAdapter)
                        .color(R.color.shimmer_color)
                        .load(R.layout.skeleton_list_sdm)
                        .show();
                }
                is UiState.Success -> {
                    skeletonScreen?.hide()

                    dataFilter = it.data.data

                }
                is UiState.Error -> {
                    skeletonScreen?.hide()
                }
            }
        })
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvSdm.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    private fun search(key: String) {
        groupAdapter.clear()

        val filter = dataFilter.filter {
            it.full_name.toLowerCase().contains(key.toLowerCase()) || it.no_partner == key
        }
        filter.forEach { sdm ->
            groupAdapter.add(SdmItem(sdm) {
                startActivityForResult<DetailKaryawanActivity>(
                    121,
                    USER_KEY to it,
                    IS_MANAGEMENT_KEY to false
                )
            })
        }
    }

    private fun iniSearcView() {
        search.setIconifiedByDefault(true)
        search.isFocusable = true
        search.isIconified = false
        search.clearFocus();
        search.requestFocusFromTouch()

        search.findViewById<ImageView>(R.id.search_close_btn).gone()

        search.findViewById<ImageView>(R.id.search_close_btn).setOnClickListener {
            search.findViewById<ImageView>(R.id.search_close_btn).gone()
            finish()
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                search.findViewById<ImageView>(R.id.search_close_btn).gone()
                if (newText.isNotEmpty()) {
                    layout_empty.gone()
                    rvSdm.visible()
                    search(newText)
                } else {
                    layout_empty.visible()
                    rvSdm.gone()
                }

                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })
    }

}
