package id.android.kmabsensi.presentation.sdm.modekerja

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.gson.Gson
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.WorkConfigParams
import id.android.kmabsensi.data.remote.response.WorkConfig
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.WorkConfigViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_mode_kerja.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModeKerjaActivity : BaseActivity() {

    companion object {
        const val WORK_MODE = "WORK_MODE"
        const val SHIFT_MODE = "SHIFT_MODE"
        const val WFH_USER_SCOPE = "WFH_USER_SCOPE"
        const val WFO = "WFO"
        const val WFH = "WFH"
        const val MODE_ON = "ON"
        const val MODE_OFF = "OFF"
    }

    private val workConfigVM: WorkConfigViewModel by viewModel()
    private val prefHelper: PreferencesHelper by inject()
    private val gson: Gson by inject()

    private var userScopeSelected = ""
    private var isWFHMode = false
    private var isSHiftMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_kerja)
        setupToolbar(getString(R.string.mode_kerja))

        initListener()
        observeResult()
        workConfigVM.getWorkConfig()
    }

    private fun initConfig(configs: List<WorkConfig>) {
        isWFHMode =
            configs.find { config -> config.key == WORK_MODE }?.value == WFH
        userScopeSelected =
            configs.find { config -> config.key == WFH_USER_SCOPE }?.value.toString()
        isSHiftMode =
            configs.find { config -> config.key == SHIFT_MODE }?.value == MODE_ON
        wfhSwitchButton.isChecked = isWFHMode
        shiftSwitchButton.isChecked = isSHiftMode
        if (isWFHMode) {
            layoutDetailWorkConfig.visible()
            txtWorkScope.text = userScopeSelected.replace("|", ",")
        } else {
            layoutDetailWorkConfig.gone()
        }

    }

    private fun initListener() {
        btnWfhSwitchButton.setOnClickListener {
            if (isSHiftMode) {
                createAlertError(
                    this,
                    "Failed",
                    "Mode shift sedang aktif, silahkan non aktifkan terlebih dahulu"
                )
                return@setOnClickListener
            }
            if (isWFHMode) {
                userScopeSelected = ""
                val userScope = userScopeSelected
                val configs = mutableListOf<WorkConfig>()
                configs.add(WorkConfig(WORK_MODE, WFO))
                configs.add(WorkConfig(WFH_USER_SCOPE, userScope))
                val workingConfigParams = WorkConfigParams(configs)
                workConfigVM.updateWorkConfig(workingConfigParams)
            } else {
                showDialogModeKerja(isWFHMode)
            }
        }


        btnShiftSwitchButton.setOnClickListener {
            if (isWFHMode) {
                createAlertError(
                    this,
                    "Failed",
                    "Mode WFH sedang aktif, silahkan non aktifkan terlebih dahulu"
                )
                return@setOnClickListener
            }
            val configs = mutableListOf<WorkConfig>()
            val shiftMode = if (isSHiftMode) MODE_OFF else MODE_ON
            configs.add(WorkConfig(SHIFT_MODE, shiftMode))
            val workingConfigParams = WorkConfigParams(configs)
            workConfigVM.updateWorkConfig(workingConfigParams)
        }
    }

    private fun observeResult() {
        workConfigVM.workConfigUpdateResult.observe(this, androidx.lifecycle.Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    initConfig(state.data.data)
                }
                is UiState.Error -> {
                    hideDialog()
                    createAlertError(this, "Failed", state.throwable.message.toString())
                }
            }
        })

        workConfigVM.workConfigResult.observe(this, androidx.lifecycle.Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    initConfig(state.data.data)
                }
                is UiState.Error -> {
                    hideDialog()
                    createAlertError(this, "Failed", state.throwable.message.toString())
                }
            }
        })
    }

    private fun showDialogModeKerja(isWFH: Boolean) {
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
            userScopeSelected =
                if (spinnerRole.selectedItem.toString() == "ALL") "SDM|MANAGEMENT" else spinnerRole.selectedItem.toString()
            val userScope = userScopeSelected
            val configs = mutableListOf<WorkConfig>()
            configs.add(WorkConfig(WORK_MODE, if (!isWFH) WFH else WFO))
            configs.add(WorkConfig(WFH_USER_SCOPE, userScope))
            val workingConfigParams = WorkConfigParams(configs)
            workConfigVM.updateWorkConfig(workingConfigParams)
        }

        dialog.show()
    }

}