package id.android.kmabsensi.presentation.sdm.search

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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


class CariDataSdmActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by inject()
    private val groupAdapter = GroupAdapter<ViewHolder>()
    var dataFilter: List<User> = listOf()


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
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()

                    dataFilter = it.data.data

//                    if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
//
//                    groupAdapter.clear()
//                    it.data.data.forEach { sdm ->
//                        groupAdapter.add(SdmItem(sdm) {
//                            startActivityForResult<DetailKaryawanActivity>(
//                                121,
//                                USER_KEY to it,
//                                IS_MANAGEMENT_KEY to false
//                            )
//                        })
//                    }

                }
                is UiState.Error -> {
                    progressBar.gone()
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

        val filter = dataFilter.filter { it.full_name.toLowerCase().contains(key.toLowerCase()) }
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
