package id.android.kmabsensi.presentation.partner.partnerpicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.databinding.ActivityPartnerPickerBinding
import id.android.kmabsensi.presentation.base.BaseSearchActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import org.koin.android.ext.android.inject

class PartnerPickerActivity : BaseSearchActivity() {

    private val groupAdapter: GroupAdapter<GroupieViewHolder> by inject()
    private var partners = mutableListOf<Partner>()
    private val binding by lazy { ActivityPartnerPickerBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSearchToolbar("Pilih Partner")
        partners = intent.getParcelableArrayListExtra<Partner>("listPartner") as ArrayList
        populateData(partners)

        initRv()
    }

    private fun populateData(partners: List<Partner>){
        groupAdapter.clear()
        partners.forEach {
            groupAdapter.add(PartnerPickerItem(it){
                val intent = Intent()
                intent.putExtra(PARTNER_DATA_KEY, it)
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }

        if (partners.isEmpty()){
            binding.layoutEmpty.layoutEmpty.visible()
        } else {
            binding.layoutEmpty.layoutEmpty.gone()
        }
    }

    fun initRv(){
        binding.rvPartners.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider)))
            adapter = groupAdapter
        }
    }

    override fun search(keyword: String) {
        val partners = partners.filter {
            it.fullName.toLowerCase().contains(keyword.toLowerCase()) || it.partnerDetail.noPartner == keyword
        }
        populateData(partners)
    }


    override fun restoreData() {
        populateData(partners)
    }
}
