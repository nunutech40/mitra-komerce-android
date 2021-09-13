package id.android.kmabsensi.presentation.partner.partnerpicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
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
    private val vm : PartnerPickViewModel by inject()
    private val groupAdapter: GroupAdapter<GroupieViewHolder> by inject()
    private var partnersNew = mutableListOf<Partner>()
    private val binding by lazy { ActivityPartnerPickerBinding.inflate(layoutInflater) }
    private var partnerSkeleton : SkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSearchToolbar("Pilih Partner")
        setupObserver()
        initRv()
    }

    private fun setupObserver() {
        vm.getDataPartners()
        vm.getPartnerState.observe(this, {
            when(it){
                is UiState.Loading -> {
                    Log.d("_getPartnerState", "on Loading")
                    showSkeletonPartner()
                }
                is UiState.Success -> {
                    Log.d("_getPartnerState", "on Succes")
                    hideSkeletonPartner()
                    partnersNew.addAll(it.data.partners)
                    populateData(partnersNew)
                }
                is UiState.Error -> {
                    hideSkeletonPartner()
                    Log.d("_getPartnerState", "on Error")
                }
            }
        })
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
        val partners = partnersNew.filter {
            it.fullName.toLowerCase().contains(keyword.toLowerCase()) || it.partnerDetail.noPartner == keyword
        }
        populateData(partners)
    }


    override fun restoreData() {
        populateData(partnersNew)
    }

    private fun showSkeletonPartner(){
        partnerSkeleton = Skeleton.bind(binding.rvPartners)
            .adapter(groupAdapter)
            .load(R.layout.skeleton_item_big)
            .show()
    }

    private fun hideSkeletonPartner(){
        partnerSkeleton?.hide()
    }
}
