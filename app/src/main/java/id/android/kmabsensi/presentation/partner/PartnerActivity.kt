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
import id.android.kmabsensi.presentation.partner.detail.DetailPartnerActivity
import id.android.kmabsensi.presentation.partner.tambahpartner.FormPartnerActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_partner.*
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartnerActivity : BaseActivity() {

    private val vm: PartnerViewModel by viewModel()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner)

        setupToolbar(getString(R.string.text_data_partner))

        initRv()

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
            startActivityForResult<FormPartnerActivity>(CRUD_PARTNER_RC)
        }

        vm.getPartners()
        observeData()

    }

    private fun observeData(){
        vm.partners.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                progressBar.visible()
            }
            is UiState.Success -> {
                progressBar.gone()
                if (state.data.status){
                    state.data.partners.forEach {
                        groupAdapter.add(PartnerItem(it){
                            startActivityForResult<DetailPartnerActivity>(CRUD_PARTNER_RC, PARTNER_DATA_KEY to it)
                        })
                    }
                }

            }
            is UiState.Error -> {
                progressBar.gone()
            }
        } })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CUSTOMIZE_RC && resultCode == Activity.RESULT_OK){
            val content = data?.getStringExtra("content")
        } else if (requestCode == CRUD_PARTNER_RC && resultCode == Activity.RESULT_OK){
            val message = data?.getStringExtra("message")
            createAlertSuccess(this, message.toString())
            groupAdapter.clear()
            vm.getPartners()
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
        const val CRUD_PARTNER_RC = 122
    }
}
