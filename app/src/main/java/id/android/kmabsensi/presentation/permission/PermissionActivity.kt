package id.android.kmabsensi.presentation.permission

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.permission.tambahizin.FormIzinActivity
import id.android.kmabsensi.utils.USER_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_permission.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class PermissionActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    private val groupAdapter = GroupAdapter<ViewHolder>()

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "History Izin"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = intent.getParcelableExtra(USER_KEY)

        initRv()

        vm.listPermissionData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    it.data.data.forEach {
                        groupAdapter.add(PermissionItem(it){

                        })
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })

        fabFromIzin.setOnClickListener {
            startActivity<FormIzinActivity>(USER_KEY to user)
        }

        vm.getListPermission(userId = user.id)

    }

    fun initRv(){
        rvPermission.apply {
            layoutManager = LinearLayoutManager(this@PermissionActivity)
            adapter = groupAdapter
        }
    }
}
