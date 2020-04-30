package id.android.kmabsensi.presentation.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetail
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailBasicItem
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailItem
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_detail_invoice.*

class DetailInvoiceActivity : BaseActivity() {

    private val groupAdaper = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_invoice)

        setupToolbar("Invoice Detail Admin")
        initRv()

        val items = listOf(
            InvoiceDetail("Item A", 1000000, false),
            InvoiceDetail("Item B", 500000, false)
        )

        items.forEach {
            groupAdaper.add(InvoiceDetailBasicItem(it))
        }

    }

    fun initRv(){
        val linearLayoutManager = LinearLayoutManager(this)
        rvInvoice.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            adapter = groupAdaper
        }
    }
}
