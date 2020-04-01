package id.android.kmabsensi.presentation.partner

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_partner.*

class PartnerActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner)

        setToolbarTitle(getString(R.string.text_data_partner))

        initRv()

        val data = mutableListOf<Partner>()
        for (i in 1..4){
            data.add(Partner())
        }
        data.forEach {
            groupAdapter.add(PartnerItem(it))
        }



    }

    fun initRv(){
        rvPartner.apply {
            layoutManager = LinearLayoutManager(this@PartnerActivity)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable( context, R.drawable.divider), true))
            adapter = groupAdapter
        }
    }
}

data class Partner(
    var number: String = "1231231",
    val name: String = "Alodokter",
    val category: String = "Kesehatan",
    val totalSdm: Int = 300
)