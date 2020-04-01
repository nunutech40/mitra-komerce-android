package id.android.kmabsensi.presentation.partner

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.IS_SORT_KEY
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_customize_partner.*

/* sort & filter partner page */
class CustomizePartnerActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_partner)

        val isSort = intent.getBooleanExtra(IS_SORT_KEY, false)

        setToolbarTitle(if (isSort) "Sort" else "Filter", true)

        labelTitleContent.text = if (isSort) "Jumlah SDM" else "Kategori"

        rvContent.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider), true))
            adapter = groupAdapter
        }

        val filterData = listOf(
            "Semua", "Buku & Software", "Kategori A", "Kategori B"
        )
        val sortData = listOf(
            "Jumlah Terbanyak", "Jumlah Terendah"
        )

        if (isSort){
            sortData.forEach { groupAdapter.add(SortFilterItem(it, ::onContentClicked)) }
        } else {
            filterData.forEach { groupAdapter.add(SortFilterItem(it, ::onContentClicked)) }
        }
    }

    private fun onContentClicked(content: String){
        val intent = Intent()
        intent.putExtra("content", content)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
