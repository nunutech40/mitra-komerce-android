package id.android.kmabsensi.presentation.role

import android.graphics.Point
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.AssignReleasePositionParams
import id.android.kmabsensi.data.remote.response.Position
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.jabatan.JabatanViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_role.*
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RoleActivity : BaseActivity() {

    private val roleVM: RoleViewModel by viewModel()
    private val jabatanVM: JabatanViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val groupAdapterPosition = GroupAdapter<GroupieViewHolder>()

    private val positions = mutableListOf<Position>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)
        setupToolbar(getString(R.string.role))

        rvRole.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        roleVM.menuRoles.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    groupAdapter.clear()
                    state.data.menuRoles.forEach {
                        groupAdapter.add(RoleItem(it) {
                            showDialogBerikanAkses(it.positions, it.id)
                        })
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })
        roleVM.getMenuRole()

        jabatanVM.positionResponse.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    positions.addAll(state.data.data)
                }
                is UiState.Error -> {

                }
            }
        })
        jabatanVM.getPositions()

        roleVM.assignReleaseResult.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {

                }
                is UiState.Error -> {
                    Timber.e(state.throwable)
                }
            }
        })

    }

    private fun showDialogBerikanAkses(menu: List<Position>, menuId: Int) {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_berikan_akses, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val rvPosition = customView.findViewById<RecyclerView>(R.id.rvPosition)
        val btnSimpan = customView.findViewById<Button>(R.id.btnSimpan)

        rvPosition.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(context, R.drawable.divider),
                    false
                )
            )
            adapter = groupAdapterPosition
        }

        btnSimpan.setOnClickListener {
            dialog.dismiss()
        }

        val idAssigned = menu.joinToString(transform = { position -> position.id.toString() })
        Timber.d(idAssigned.toString())
        val jabatan = positions.map { if (menu.find { menu -> menu.id == it.id } != null) it.copy(isChecked = true) else it }
        Timber.d(jabatan.toString())
        groupAdapterPosition.clear()
        jabatan.forEach {
            groupAdapterPosition.add(PositionAssignItem(it){ jabatan, isChecked ->
                val params = AssignReleasePositionParams(menuId, jabatan.id)
                if (isChecked) roleVM.assignPosition(params) else roleVM.releasePosition(params)
            })
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.onDismiss {
            roleVM.getMenuRole()
        }

    }

}