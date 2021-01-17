package id.android.kmabsensi.presentation.notes

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Note
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.NotesViewModel
import id.android.kmabsensi.utils.UiState
import kotlinx.android.synthetic.main.activity_list_note.*
import kotlinx.android.synthetic.main.activity_manage_invoice_detail.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListNoteActivity : BaseActivity() {

    private val notesVM: NotesViewModel by viewModel()
    private val noteAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_note)
        setupToolbar("Notes")
        initRv()
        observeData()
        notesVM.getListNote()

        btnAddNote.setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)
//            startActivity<NotesActivity>()
        }
    }

    fun observeData(){
        notesVM.notes.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    populateNoteData(state.data.data)
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    private  fun initRv(){
        rvNote.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = noteAdapter
        }
    }

    private fun populateNoteData(data: List<Note>){
        data.forEach {
            noteAdapter.add(NoteItem(it))
        }
    }
}