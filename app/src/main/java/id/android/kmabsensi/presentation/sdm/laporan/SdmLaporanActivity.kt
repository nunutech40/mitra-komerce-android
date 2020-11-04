package id.android.kmabsensi.presentation.sdm.laporan

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.cabang.TambahCabangActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createAlertSuccess
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_sdm_laporan.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class SdmLaporanActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var RC_CRUD = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdm_laporan)
        setupToolbar(getString(R.string.laporan_title), isFilterVisible = true)

        initRv()
        sdmVM.getCsPerformances()
        sdmVM.csPerformances.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                swipeRefresh.isRefreshing = true
            }
            is UiState.Success -> {
                swipeRefresh.isRefreshing = false
                val data = state.data.data
                if (data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                data.forEach {
                    groupAdapter.add(SdmReportItem(it))
                }
            }
            is UiState.Error -> {
                swipeRefresh.isRefreshing = false
            }
        } })

        fabAdd.setOnClickListener {
            startActivityForResult<AddSdmLaporanActivity>(RC_CRUD)
        }

    }

    private fun initRv(){
        rvLaporan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CRUD){
            if (resultCode == Activity.RESULT_OK){
                val message = data?.getStringExtra("message")
                createAlertSuccess(this, message.toString())
                groupAdapter.clear()
                sdmVM.getCsPerformances()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}