package id.android.kmabsensi.presentation.sdm.laporan.advertiser

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.AdvertiserReport
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_list_laporan_advertiser.*
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ListLaporanAdvertiserActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_laporan_advertiser)
        setupToolbar("Laporan")
        initRv()

        observeData()
        sdmVM.getAdvertiserReports()

        fabAdd.setOnClickListener {
            startActivityForResult<KelolaLaporanAdvertiserActivity>(112)
        }

        swipeRefresh.setOnRefreshListener {
            sdmVM.getAdvertiserReports()
        }
    }

    private fun initRv(){
        rvLaporan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun observeData(){
        sdmVM.advertiserReports.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                swipeRefresh.isRefreshing = true
            }
            is UiState.Success -> {
                swipeRefresh.isRefreshing = false
                val data = state.data.data
                groupAdapter.clear()
                if (data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                data.forEach {
                    groupAdapter.add(ListLaporanItem(it, object : OnAdvertiserReportListener {
                        override fun onEditClicked(report: AdvertiserReport) {
                            startActivityForResult<KelolaLaporanAdvertiserActivity>(112, "report" to report)
                        }

                        override fun onDeleteClicked(report: AdvertiserReport) {
                            showDialogConfirmDelete(
                                this@ListLaporanAdvertiserActivity,
                                "Hapus Data Laporan"
                            ) {
                                sdmVM.deleteAdvertiserReport(report.id)
                            }
                        }

                    }))
                }
            }
            is UiState.Error -> {
                swipeRefresh.isRefreshing = false
                Timber.e(state.throwable)
            }
        } })

        sdmVM.crudResponse.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                showDialog()
            }
            is UiState.Success -> {
                hideDialog()
                if (state.data.status){
                    createAlertSuccess(this, state.data.message)
                    sdmVM.getAdvertiserReports()
                } else {
                    createAlertError(this, "Gagal", state.data.message)
                }
            }
            is UiState.Error -> {
                hideDialog()
            }
        } })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 112 && resultCode == RESULT_OK){
            val message = data?.getStringExtra("message")
            createAlertSuccess(this, message.toString())
            sdmVM.getAdvertiserReports()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}