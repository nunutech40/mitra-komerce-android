package id.android.kmabsensi.presentation.partner.partnerpicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.utils.SIMPLE_PARTNER_DATA_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_cari_data_sdm.*
import kotlinx.android.synthetic.main.activity_partner_picker.*
import kotlinx.android.synthetic.main.edittext_search.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartnerPickerActivity : BaseActivity() {

    private val vm: PartnerViewModel by viewModel()
    private val groupAdapter: GroupAdapter<GroupieViewHolder> by inject()

    private var mHandler = Handler()

    private var partners = mutableListOf<SimplePartner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_picker)

        setupToolbar("Pilih Partner", isSearchVisible = true)

        initRv()
        observePartners()

        searchView.et_search.addTextChangedListener {
            /* Ketika query kosong, maka tampil view not found */
            if (searchView.et_search.text.isNullOrEmpty()) {
                populateData(partners)
                return@addTextChangedListener
            }

            layout_empty.visibility = View.GONE
            rvPartners.visibility = View.VISIBLE
            handleOnTextChange(searchView.et_search.text.toString())
        }

    }

    override fun onBackPressed() {
        if (isSearchMode){
            isSearchMode = false
            populateData(partners)
            toolbarContent.visibility = View.GONE
            toolbarContent.removeView(searchView)
        } else {
            super.onBackPressed()
        }
    }

    private fun observePartners(){
        vm.getSimplePartners()
        vm.simplePartners.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                showSkeleton(rvPartners, R.layout.skeleton_list_jabatan, groupAdapter)
            }
            is UiState.Success -> {
                hideSkeleton()
                partners.addAll(state.data.partners)
                if (state.data.status){
                    populateData(partners)
                }
            }
            is UiState.Error -> {
                hideSkeleton()
            }
        } })
    }

    private fun populateData(partners: List<SimplePartner>){
        groupAdapter.clear()
        partners.forEach {
            groupAdapter.add(SimplePartnerItem(it){
                val intent = Intent()
                intent.putExtra(SIMPLE_PARTNER_DATA_KEY, it)
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }

        if (partners.isEmpty()){
            layout_empty.visible()
        } else {
            layout_empty.gone()
        }
    }

    fun initRv(){
        rvPartners.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider)))
            adapter = groupAdapter
        }
    }

    private fun handleOnTextChange(keyword: String) {
        mHandler.removeCallbacksAndMessages(null)
        if (keyword.isNotEmpty()) {
            mHandler.postDelayed({
                search(keyword)
            }, 500)
        }
    }

    private fun search(keyword: String) {

        val partners = partners.filter {
            it.fullName.toLowerCase().contains(keyword.toLowerCase()) || it.noPartner == keyword
        }
        populateData(partners)
    }
}
