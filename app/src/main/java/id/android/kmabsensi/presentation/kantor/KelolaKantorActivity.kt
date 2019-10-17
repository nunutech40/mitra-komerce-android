package id.android.kmabsensi.presentation.kantor

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.cabang.TambahCabangActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_kelola_kantor.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject

class KelolaKantorActivity : BaseActivity() {

    private val vm : OfficeViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    private var REQUEST_MANAGE_OFFICE = 210

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_kantor)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kelola Data Kantor"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        btnTambahKantor.setOnClickListener {
            startActivityForResult<TambahCabangActivity>(REQUEST_MANAGE_OFFICE)
        }

        vm.officeState.observe(this, Observer {
            when(it){
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    it.data.data.forEach {
                        groupAdapter.add(KantorItem(it){ office ->
                            startActivityForResult<TambahCabangActivity>(REQUEST_MANAGE_OFFICE,
                                OFFICE_KEY to office)
                        })
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })

        vm.getOffices()

    }

    private fun initRv(){
        rvKantor.apply {
            layoutManager = LinearLayoutManager(this@KelolaKantorActivity)
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_MANAGE_OFFICE){
            if (resultCode == Activity.RESULT_OK){
                val message = data?.getStringExtra("message")
                createAlertSuccess(this, message.toString())
                groupAdapter.clear()
                vm.getOffices()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


}
