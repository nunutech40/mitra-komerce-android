package id.android.kmabsensi.presentation.coworking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_list_coworking.*
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListCoworkingActivity : BaseActivity() {

    private val vm: CoworkingSpaceViewModel by viewModel()

    private val groupAdapter = GroupAdapter<ViewHolder>()
    private lateinit var myDialog: MyDialog

    private var REQUEST_MANAGE_COWORKING = 210

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_coworking)

        setupToolbar("Co-Working")
        myDialog = MyDialog(this)

        btnAddCoworking.setOnClickListener {
            startActivityForResult<CoworkingActivity>(REQUEST_MANAGE_COWORKING)
        }

        initRv()

        vm.coworkingSpace.observe(this, Observer {
            when(it){
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    btnAddCoworking.visible()
                    it.data.data.forEach { coworking ->
                        groupAdapter.add(CoworkingItem(coworking){
                            startActivityForResult<CoworkingActivity>(REQUEST_MANAGE_COWORKING,
                                COWORKING_KEY to coworking)
                        })
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                    Timber.e(it.throwable)
                }
            }
        })

        vm.getCoworkingSpace()

    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvCoworkingSpace.apply {
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_MANAGE_COWORKING){
            if (resultCode == Activity.RESULT_OK){
                val message = data?.getStringExtra("message")
                createAlertSuccess(this, message.toString())
                groupAdapter.clear()
                vm.getCoworkingSpace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)

    }
}
