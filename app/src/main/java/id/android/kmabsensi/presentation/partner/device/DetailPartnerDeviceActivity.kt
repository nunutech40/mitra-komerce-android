package id.android.kmabsensi.presentation.partner.device

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.device.item.DeviceItem
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_detail_partner_device.*

class DetailPartnerDeviceActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_partner_device)
        setupToolbar("Detail Device")

        initRv()
        for (i in 0..1){
            groupAdapter.add(DeviceItem())
        }

    }

    fun initRv(){
        rvDevice.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(
                ContextCompat.getDrawable(context, R.drawable.divider)
            ))
            adapter = groupAdapter
        }
    }
}