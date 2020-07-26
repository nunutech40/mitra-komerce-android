package id.android.kmabsensi.presentation.partner.administratif

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Administration
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.AdministrationViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_administratif.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdministratifActivity : BaseActivity() {

    private val administrationVM: AdministrationViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val CRUD_RC = 110

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administratif)
        setupToolbar("Administratif")

        initRv()

        btnAddAdministratif.setOnClickListener {
            startActivityForResult<KelolaAdministratifActivity>(CRUD_RC)
        }

        observeResult()
        observeData()
        administrationVM.getListAdministration()

        swipeRefresh.setOnRefreshListener {
            administrationVM.getListAdministration()
        }
    }

    private fun observeResult() {
        administrationVM.crudResult.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status) {
                        createAlertSuccess(this, state.data.message)
                        administrationVM.getListAdministration()
                    } else {
                        createAlertError(this, "Failed", state.data.message)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                    Timber.e(state.throwable)
                }
            }
        })
    }

    fun observeData() {
        administrationVM.administrations.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is UiState.Success -> {
                    swipeRefresh.isRefreshing = false
                    groupAdapter.clear()
                    if (state.data.administrations.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    state.data.administrations.forEach {
                        groupAdapter.add(AdministratifItem(it, object : OnAdministrationListener {
                            override fun onDeleteClicked(id: Int) {
                                showDialogConfirmDelete(this@AdministratifActivity) {
                                    administrationVM.deleteAdministrationData(id)
                                }
                            }

                            override fun onEditClicked(administration: Administration) {
                                startActivityForResult<KelolaAdministratifActivity>(
                                    CRUD_RC, "isEdit" to true,
                                    ADMINISTRATION_DATA to administration
                                )
                            }

                            override fun onDetailClicked(administration: Administration) {
                                startActivity<KelolaAdministratifActivity>(ADMINISTRATION_DATA to administration)
                            }

                        }))
                    }
                }
                is UiState.Error -> {
                    swipeRefresh.isRefreshing = false
                }
            }
        })
    }

    fun initRv() {
        rvAdministratif.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CRUD_RC && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra(MESSAGE_CRUD)
            createAlertSuccess(this, message.toString())
            administrationVM.getListAdministration()
        }
    }
}