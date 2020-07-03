package id.android.kmabsensi.presentation.partner.administratif

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_administratif.*
import kotlinx.android.synthetic.main.activity_manage_invoice_detail.*
import org.jetbrains.anko.startActivity

class AdministratifActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administratif)
        setupToolbar("Administratif")

        initRv()
        for (i in 0..2){
            groupAdapter.add(AdministratifItem())
        }

        btnAddAdministratif.setOnClickListener {
            startActivity<AddAdministratifActivity>()
        }
    }

    fun initRv(){
        rvAdministratif.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }
}