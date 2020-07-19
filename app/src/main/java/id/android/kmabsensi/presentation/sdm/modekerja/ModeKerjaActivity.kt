package id.android.kmabsensi.presentation.sdm.modekerja

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils.split
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suke.widget.SwitchButton
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.WorkConfigParams
import id.android.kmabsensi.data.remote.response.WorkConfig
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.WorkConfigViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_mode_kerja.*
import kotlinx.android.synthetic.main.item_row_partner_off.*
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class ModeKerjaActivity : BaseActivity() {

    companion object {
        const val WORK_MODE = "WORK_MODE"
        const val WFH_USER_SCOPE = "WFH_USER_SCOPE"
        const val WFO = "WFO"
        const val WFH = "WFH"
    }

    private val workConfigVM: WorkConfigViewModel by viewModel()
    private val prefHelper: PreferencesHelper by inject()
    private val gson: Gson by inject()

    private var userScopeSelected = ""
    private var workingConfigParams: WorkConfigParams = WorkConfigParams()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_kerja)
        setupToolbar(getString(R.string.mode_kerja))

        initView()

        observeResult()
    }

    private fun initView(){
        val isWFH = prefHelper.getBoolean(PreferencesHelper.WORK_MODE_IS_WFH)
        userScopeSelected = prefHelper.getString(PreferencesHelper.WORK_MODE_SCOPE)
        switchButton.isChecked = isWFH
        if (isWFH) {
            layoutDetailWorkConfig.visible()
            txtWorkScope.text = userScopeSelected
        }

        btnSwitchButton.setOnClickListener {
            showDialogModeKerja(isWFH)
        }
    }

    private fun observeResult(){
        workConfigVM.workConfigUpdateResult.observe(this, androidx.lifecycle.Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    createAlertSuccess(this, state.data.message)
                    switchButton.isChecked = !switchButton.isChecked
                    val isWFH = workingConfigParams.kmConfigs.find { it.key == WORK_MODE }?.value == WFH
                    if (isWFH){
                        layoutDetailWorkConfig.visible()
                        txtWorkScope.text = userScopeSelected
                    } else {
                        layoutDetailWorkConfig.gone()
                    }
                    prefHelper.saveString(PreferencesHelper.WORK_MODE_SCOPE, userScopeSelected)
                    prefHelper.saveBoolean(PreferencesHelper.WORK_MODE_IS_WFH, switchButton.isChecked)
                }
                is UiState.Error -> {
                    hideDialog()
                    createAlertError(this, "Failed", state.throwable.message.toString())
                }
            }
        })
    }

    private fun showDialogModeKerja(isWFH: Boolean){
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_mode_kerja, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val spinnerRole = customView.findViewById<Spinner>(R.id.spinnerRole)
        val btnSimpan = customView.findViewById<Button>(R.id.btnSimpan)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.work_scope,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRole.adapter = adapter
        }

        btnSimpan.setOnClickListener {
            dialog.dismiss()
            userScopeSelected = if (spinnerRole.selectedItem.toString() == "ALL") "SDM|MANAGEMENT" else spinnerRole.selectedItem.toString()
            val userScope = userScopeSelected
            val configs = mutableListOf<WorkConfig>()
            configs.add(WorkConfig(WORK_MODE, if (!isWFH) WFH else WFO))
            configs.add(WorkConfig(WFH_USER_SCOPE, userScope))
            workingConfigParams = WorkConfigParams(configs)
            workConfigVM.updateWorkConfig(workingConfigParams)
        }

        dialog.show()
    }

}