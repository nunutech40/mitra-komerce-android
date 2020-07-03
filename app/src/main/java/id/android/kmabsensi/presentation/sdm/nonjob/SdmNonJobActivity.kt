package id.android.kmabsensi.presentation.sdm.nonjob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.base.BaseSearchActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.UiState
import kotlinx.android.synthetic.main.activity_sdm_non_job.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SdmNonJobActivity : BaseSearchActivity() {

    private val sdmVM: KelolaDataSdmViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var sdm = mutableListOf<User>()

    override fun search(keyword: String) {
        val sdm = sdm.filter {
            it.full_name.toLowerCase().contains(keyword.toLowerCase())
        }
        populateData(sdm)
    }

    override fun restoreData() {
        populateData(sdm)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdm_non_job)
        setupSearchToolbar("SDM Non Job")
        initRv()

        observeDataSdmNonJob()
    }

    private fun observeDataSdmNonJob(){
        sdmVM.getSdmNonJob()
        sdmVM.sdmNonJob.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    populateData(state.data.data)
                    txtJumlahSdmNonJob.text = "${state.data.data.size}"
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    fun initRv(){
        rvSdmNonJob.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun populateData(sdm: List<User>){
        groupAdapter.clear()
        sdm.forEach {
            groupAdapter.add(SdmNonJobItem(it))
        }
    }


}