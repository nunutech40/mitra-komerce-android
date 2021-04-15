package id.android.kmabsensi.presentation.sdm.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityCariDataSdmBinding
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

    private var skeletonScreen: SkeletonScreen? = null

    var isManagement = false

    private val binding by lazy { ActivityCariDataSdmBinding.inflate(layoutInflater) }

    private var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.text_cari_data_sdm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isManagement = intent.getBooleanExtra(IS_MANAGEMENT_KEY, false)

        initRv()

        iniSearcView()
        binding.searchView.requestFocus()
        binding.btnSearchSdm.setOnClickListener {
            keyword = binding.searchView.text.toString().toLowerCase()
            if (keyword.isNullOrEmpty()){
                binding.searchView.error = getString(R.string.text_anda_belum_memasukan_kata_kuncinya)
                binding.searchView.requestFocus()
            }else{
                vm.searchUser(keyword).observe(this, Observer {
                    when(it){
                        is UiState.Loading -> {
                            skeletonScreen = Skeleton.bind(rvSdm)
                                            .adapter(groupAdapter)
                                            .color(R.color.shimmer_color)
                                            .load(R.layout.skeleton_list_sdm)
                                            .show();
                            Log.d("_searching", "search Loading")
                        }
                        is UiState.Success -> {
                            skeletonScreen?.hide()
                            Log.d("_searching", "search Succes data ${it.data.data}")
                            groupAdapter.clear()
                            if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                            it.data.data.forEach { sdm ->
                                groupAdapter.add(SdmItem(sdm) {
                                    startActivityForResult<DetailKaryawanActivity>(
                                        121,
                                        USER_KEY to it,
                                        IS_MANAGEMENT_KEY to isManagement
                                    )
                                })
                            }
                        }
                        is UiState.Error -> Log.d("_searching", "search error")
                    }
                })
            }
        }
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvSdm.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    private fun iniSearcView() {
        /* Show keyboard */
        searchView.requestFocus()
        val imm: InputMethodManager? =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)

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
