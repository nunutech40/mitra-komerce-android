package id.android.kmabsensi.presentation.partner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.kategori.PartnerCategoryViewModel
import id.android.kmabsensi.utils.IS_SORT_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_customize_partner.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/* sort & filter partner page */
class CustomizePartnerActivity : BaseActivity() {

    companion object {
        val sortData = listOf(
            "Jumlah Terbanyak", "Jumlah Terendah"
        )
    }

    private val categoryViewModel: PartnerCategoryViewModel by viewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_partner)

        val isSort = intent.getBooleanExtra(IS_SORT_KEY, false)

        setupToolbar(if (isSort) "Sort" else "Filter", true)

        labelTitleContent.text = if (isSort) "Jumlah SDM" else "Kategori"

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
            observeCategoryPartner()
        }
    }

    private fun observeCategoryPartner() {
        categoryViewModel.getPartnerCategories()
        categoryViewModel.partnerCategories.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    groupAdapter.add(SortFilterItem("Semua", ::onContentClicked))
                    state.data.categories.forEach {
                        groupAdapter.add(SortFilterItem(it.partnerCategoryName, ::onContentClicked))
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })
    }

    private fun onContentClicked(content: String) {
        val intent = Intent()
        intent.putExtra("content", content)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
