package id.android.kmabsensi.presentation.partner.device

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.device.item.PartnerDeviceItem
import kotlinx.android.synthetic.main.activity_partner_device.*
import org.jetbrains.anko.startActivity

class PartnerDeviceActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_device)
        setupToolbar(getString(R.string.label_data_device), isSearchVisible = true)

        initRv()

        for (i in 0..2){
            groupAdapter.add(PartnerDeviceItem(){
                startActivity<DetailPartnerDeviceActivity>()
            })
        }

        btnAddDevice.setOnClickListener {
            startActivity<AddPartnerDeviceActivity>()
        }
    }

    fun initRv(){
        rvDevice.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }
}