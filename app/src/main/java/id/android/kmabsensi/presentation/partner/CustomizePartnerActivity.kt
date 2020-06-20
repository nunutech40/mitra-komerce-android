package id.android.kmabsensi.presentation.partner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.PartnerCategory
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.kategori.PartnerCategoryViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.IS_SORT_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_customize_partner.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/* sort & filter partner page */
class CustomizePartnerActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by viewModel()

    companion object {
        val sortData = listOf(
            "Jumlah Terbanyak", "Jumlah Terendah"
        )
    }

    private val categoryViewModel: PartnerCategoryViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val leaders = mutableListOf<User>()
    private val categories = mutableListOf<PartnerCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_partner)

        val isSort = intent.getBooleanExtra(IS_SORT_KEY, false)

        setupToolbar(if (isSort) "Sort" else "Filter", true)

        labelTitleContent.text = if (isSort) "Jumlah SDM" else "Filter"

        rvContent.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.divider
                    ), true
                )
            )
            adapter = groupAdapter
        }




        if (isSort) {
            sortData.forEach { groupAdapter.add(SortFilterItem(it, ::onContentClicked)) }
        } else {
//            labelTitleContent.text = "Filter by"
            labelTitleContent.gone()

            spinnerFilterBy.visible()
            val filters = listOf("Kategori", "Leader")

            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                filters
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinnerFilterBy.adapter = adapter

                spinnerFilterBy.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position == 0){
                                populateDataCategory(categories)
                            } else {
                                populateDataLeader(leaders)
                            }
                        }

                    }
            }

            categoryViewModel.getPartnerCategories()
            observeCategoryPartner()

            vm.getUserManagement(2)
            observeLeaders()
        }
    }

    private fun observeCategoryPartner() {
        categoryViewModel.partnerCategories.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showSkeleton(rvContent, R.layout.skeleton_list_jabatan, groupAdapter)
                }
                is UiState.Success -> {
                    hideSkeleton()
                    if (categories.isNotEmpty()) categories.clear()
                    categories.addAll(state.data.categories)
                    populateDataCategory(categories)
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })
    }

    private fun populateDataCategory(categories: List<PartnerCategory>){
        groupAdapter.clear()
        groupAdapter.add(SortFilterItem("Semua", ::onContentClicked))
        categories.forEach {
            groupAdapter.add(SortFilterItem(it.partnerCategoryName, ::onContentClicked))
        }
    }

    private fun observeLeaders(){
        vm.userManagementData.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {

            }
            is UiState.Success -> {
                if (leaders.isNotEmpty()) leaders.clear()
                leaders.addAll(state.data.data.filter {
                    it.position_name.toLowerCase().contains("leader")
                })
            }
            is UiState.Error -> {

            }
        } })
    }

    private fun populateDataLeader(leaders: List<User>){
        groupAdapter.clear()
        leaders.forEach {
            groupAdapter.add(SortFilterItem(it.full_name, ::onContentClicked))
        }
    }

    private fun onContentClicked(content: String) {
        val intent = Intent()
        intent.putExtra("content", content)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
