package id.android.kmabsensi.presentation.partner.administratif

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst.KEY_SELECTED_DOCS
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Administration
import id.android.kmabsensi.data.remote.response.Position
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.jabatan.JabatanViewModel
import id.android.kmabsensi.presentation.viewmodels.AdministrationViewModel
import id.android.kmabsensi.presentation.viewmodels.AttachmentViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_add_administratif.*
import kotlinx.android.synthetic.main.activity_evaluation_collaboration_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class KelolaAdministratifActivity : BaseActivity() {

    private val jabatanVM: JabatanViewModel by viewModel()
    private val administrationVM: AdministrationViewModel by viewModel()
    private val attachmentVM: AttachmentViewModel by viewModel()

    private val positions = mutableListOf<Position>()
    private var positionIdSelected = 0
    private var docFile: File? = null

    private var isEditMode = false
    private var administration: Administration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_administratif)
        setupToolbar("Tambah Administratif")

        isEditMode = intent.getBooleanExtra("isEdit", false)
        administration = intent.getParcelableExtra(ADMINISTRATION_DATA)
        initView()

        btnPilihFile.setOnClickListener {
            FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.DocPicker)
                .pickFile(this, 123)
        }

        btnDelete.setOnClickListener {
            if (isEditMode) {
                showDialogConfirmDelete(this, title = "Hapus dokumen") {
                    administration?.let {
                        if (it.attachments.isNotEmpty()) {
                            attachmentVM.deleteAttachment(it.attachments[0].id)
                        }
                        docFile = null
                        btnPilihFile.visible()
                        edtDokumen.hint = "Upload Dokumen"
                        layoutDocFile.gone()
                    }
                }
            } else {
                docFile = null
                btnPilihFile.visible()
                edtDokumen.hint = "Upload Dokumen"
                layoutDocFile.gone()
            }

        }

        btnSimpan.setOnClickListener {
            if (!validationForm()) return@setOnClickListener
            if (!isEditMode) {
                if (docFile == null) {
                    createAlertError(this, "Peringatan", "Pilih dokumen terlebih dahulu", 3000)
                    return@setOnClickListener
                }
                administrationVM.addAdministration(
                    edtJudul.text.toString(),
                    "",
                    positionIdSelected.toString(),
                    docFile
                )
            } else {
                administrationVM.editAdministration(
                    administration?.id.toString(),
                    edtJudul.text.toString(),
                    "",
                    positionIdSelected.toString(),
                    docFile
                )
            }

        }

        observeJabatan()
        observeResult()
        observeAttachment()
        jabatanVM.getPositions()
    }

    private fun initView() {
        administration?.let {
            setupToolbar(if (isEditMode) "Edit Data" else "Detail Data")
            edtJudul.setText(it.title)
            btnPilihFile.gone()
            edtDokumen.hint = ""
            layoutDocFile.visible()
            if (it.attachments.isNotEmpty()) {
                val name = it.attachments[0].attachmentPath.split("/")
                txtFileName.text = name.last()
                txtFileName.setOnClickListener { view ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.attachments[0].attachmentUrl)))
                }
            }
            positionIdSelected = it.leader.id

            if (!isEditMode){
                btnDelete.gone()
                edtJudul.isEnabled = false
                spinnerLeaderPosition.isEnabled = false
                btnSimpan.gone()
            }
        }
    }

    private fun observeJabatan() {
        jabatanVM.positionResponse.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    positions.addAll(state.data.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })
                    initSpinner()
                }
                is UiState.Error -> {

                }
            }
        })
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
                        val intent = Intent()
                        intent.putExtra(MESSAGE_CRUD, state.data.message)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
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

    private fun observeAttachment() {
        attachmentVM.deleteResult.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    d { state.data.message }
                }
                is UiState.Error -> {
                    e { state.throwable.localizedMessage }
                }
            }
        })
    }

    private fun initSpinner() {
        ArrayAdapter(this, R.layout.spinner_item, positions.map { it.position_name })
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerLeaderPosition.adapter = adapter

                spinnerLeaderPosition.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            positionIdSelected = positions[position].id

                        }

                    }

            }
        spinnerLeaderPosition.setSelection(positions.indexOfFirst { it.id == positionIdSelected })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data != null) {
            val docPaths = ArrayList<Uri>()
            data.getParcelableArrayListExtra<Uri>(KEY_SELECTED_DOCS)?.let { docPaths.addAll(it) }
            docPaths.forEach {
                val path = ContentUriUtils.getFilePath(this, it)
                dumpFileMetaData(it)
                docFile = File(path)
            }

            btnPilihFile.gone()
            edtDokumen.hint = ""
            layoutDocFile.visible()
        }
    }

    private fun dumpFileMetaData(uri: Uri?) {
        if (uri != null) {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayName: String =
                        it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    d { "Display Name: $displayName" }
                    txtFileName.text = displayName

                    val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                    val size: String = if (!it.isNull(sizeIndex)) {
                        it.getString(sizeIndex)
                    } else {
                        "Unknown"
                    }
                    d { "Size: $size" }
                }
            }
        }
    }

    private fun validationForm(): Boolean {
        val title = ValidationForm.validationInput(edtJudul, "Input judul terlebih dahulu")
        return title
    }
}