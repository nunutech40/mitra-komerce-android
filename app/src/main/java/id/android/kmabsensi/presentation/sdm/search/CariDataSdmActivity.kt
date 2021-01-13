package id.android.kmabsensi.presentation.sdm.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
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
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    var dataFilter: List<User> = listOf()

    private var skeletonScreen: SkeletonScreen? = null

    private var mHandler = Handler()

    var isManagement = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cari_data_sdm)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cari Data SDM"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)

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
                    dataFilter = it.data.data.filter { it.role_id != 4 }
                    d { "jumlahdatafilter : ${dataFilter.size.toString()}" }
                    if (searchView.text.toString().isNotEmpty()){
                        rvSdm.visible()
                        search(searchView.text.toString())
                    }
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

        d { dataFilter.size.toString() }

        val filter = dataFilter.filter { it.full_name.toLowerCase().contains(key.toLowerCase()) || it.no_partners.find { it == key } != null }
        if (filter.isEmpty()) layout_empty.visible() else layout_empty.gone()
        filter.forEach { sdm ->
            groupAdapter.add(SdmItem(sdm) {
                startActivityForResult<DetailKaryawanActivity>(
                    121,
                    USER_KEY to it,
                    IS_MANAGEMENT_KEY to isManagement
                )
            })
        }
    }

    private fun iniSearcView() {
        /* Show keyboard */
        searchView.requestFocus()
        val imm: InputMethodManager? =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)

        searchView.addTextChangedListener {
            /* Ketika query kosong, maka tampil view not found */
            if (searchView.text.isNullOrEmpty()) {
                layout_empty.visibility = View.VISIBLE
                rvSdm.visibility = View.GONE
                return@addTextChangedListener
            }

            layout_empty.gone()
            rvSdm.visible()
            handleOnTextChange(searchView.text.toString())
        }
    }

    private fun handleOnTextChange(keyword: String) {
        if (dataFilter.isNotEmpty()){
            mHandler.removeCallbacksAndMessages(null)
            if (keyword.isNotEmpty()) {
                mHandler.postDelayed({
                    search(keyword)
                }, 500)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 121 && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra("message")
            val intent = Intent()
            intent.putExtra("message", message)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}
