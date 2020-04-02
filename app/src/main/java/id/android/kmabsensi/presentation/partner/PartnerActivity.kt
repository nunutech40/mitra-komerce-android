package id.android.kmabsensi.presentation.partner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.manajemenpartner.FormPartnerActivity
import id.android.kmabsensi.utils.IS_SORT_KEY
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_partner.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class PartnerActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner)

        setupToolbar(getString(R.string.text_data_partner))

        initRv()

        val data = mutableListOf<Partner>()
        for (i in 1..4) {
            data.add(Partner())
        }
        data.forEach {
            groupAdapter.add(PartnerItem(it))
        }

        buttonSort.setOnClickListener {
            startActivityForResult<CustomizePartnerActivity>(
                CUSTOMIZE_RC, IS_SORT_KEY to true
            )
        }

        buttonFilter.setOnClickListener {
            startActivityForResult<CustomizePartnerActivity>(
                CUSTOMIZE_RC, IS_SORT_KEY to false
            )
        }

        btnAddPartner.setOnClickListener {
            startActivity<FormPartnerActivity>()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CUSTOMIZE_RC && resultCode == Activity.RESULT_OK){
            val content = data?.getStringExtra("content")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun initRv() {
        rvPartner.apply {
            layoutManager = LinearLayoutManager(this@PartnerActivity)
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
    }

    companion object {
        const val CUSTOMIZE_RC = 123
    }
}


data class Partner(
    var number: String = "1231231",
    val name: String = "Alodokter",
    val category: String = "Kesehatan",
    val totalSdm: Int = 300
)