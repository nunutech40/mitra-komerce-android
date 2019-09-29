package id.android.kmabsensi.presentation.sdm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.detail.DetailKaryawanActivity
import id.android.kmabsensi.presentation.sdm.tambahsdm.TambahSdmActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.*
import kotlinx.android.synthetic.main.activity_kelola_data_sdm.toolbar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import java.io.StringReader

class KelolaDataSdmActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    val roles = mutableListOf<String>("Management", "SDM")

    var roleId = 0

    var isManagement = false
    var userManagementId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_data_sdm)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kelola Data Karyawan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isManagement = intent.getBooleanExtra("isManagement", false)
        userManagementId = intent.getIntExtra("userId", 0)
        if (isManagement) roles.removeAt(0)

        initRv()

        btnTambahSdm.setOnClickListener {
            startActivityForResult<TambahSdmActivity>(121,"isManagement" to true,
                "userId" to userManagementId)
        }

        vm.userData.observe(this, Observer {
            when(it){
                is UiState.Loading -> { progressBar.visible() }
                is UiState.Success -> {
                    progressBar.gone()
                    it.data.data.forEach { sdm ->
                        groupAdapter.add(SdmItem(sdm){
                            startActivityForResult<DetailKaryawanActivity>(121,"karyawan" to it, "isManagement" to true)
                        })
                    }

                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })

        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    groupAdapter.clear()
                    roleId = if (isManagement) position+3 else position+2
                    vm.getUserData(roleId, userManagementId)
                }

            }
        }


    }

    fun initRv(){
        val linearLayoutManager = LinearLayoutManager(this)
        rvSdm.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 121 && resultCode == Activity.RESULT_OK){
            groupAdapter.clear()
            vm.getUserData(roleId)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
