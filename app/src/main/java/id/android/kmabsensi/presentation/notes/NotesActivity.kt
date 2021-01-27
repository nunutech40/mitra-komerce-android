package id.android.kmabsensi.presentation.notes

import android.os.Bundle
import androidx.lifecycle.Observer
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.AddNoteParams
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.NotesViewModel
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.createAlertSuccess
import kotlinx.android.synthetic.main.activity_notes.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesActivity : BaseActivity() {

    private val notesVM: NotesViewModel by viewModel()
    private val userVM: SdmViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        setupToolbar(title = "Tambah Note")

        observeData()

        btnSimpan.setOnClickListener {
            //todo validation

            val user = userVM.getUserData()
            val params = AddNoteParams(
                user_id = user.id,
                title = edtTitle.text.toString(),
                description = edtDesc.text.toString()
            )
            notesVM.addNote(params)
        }
    }

    private fun observeData(){
        notesVM.crudResponse.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                showDialog()
            }
            is UiState.Success -> {
                hideDialog()
                createAlertSuccess(this, state.data.message)
            }
            is UiState.Error -> {
                hideDialog()
            }
        } })
    }

}