package id.android.kmabsensi.presentation.partner.partnerpicker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.utils.SIMPLE_PARTNER_DATA_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_partner_picker.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartnerPickerActivity : BaseActivity() {

    private val vm: PartnerViewModel by viewModel()
    private val groupAdapter: GroupAdapter<ViewHolder> by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_picker)

        setupToolbar("Pilih Partner")

        initRv()
        observePartners()

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
                if (state.data.status){
                    state.data.partners.forEach {
                        groupAdapter.add(SimplePartnerItem(it){
                            val intent = Intent()
                            intent.putExtra(SIMPLE_PARTNER_DATA_KEY, it)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        })
                    }
                }
            }
            is UiState.Error -> {
                hideSkeleton()
            }
        } })
    }

    fun initRv(){
        rvPartners.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider)))
            adapter = groupAdapter
        }
    }
}
