package id.android.kmabsensi.presentation.jabatan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_manajemen_jabatan.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class ManajemenJabatanActivity : BaseActivity() {

    private val vm: JabatanViewModel by inject()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manajemen_jabatan)
//        setSupportActionBar(toolbar)
//        supportActionBar?.title = "Daftar Jabatan"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle("Daftar Jabatan")
        myDialog = MyDialog(this)
        initRv()

        vm.positionResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    if (it.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    it.data.data.forEach {
                        groupAdapter.add(JabatanItem(it) { jabatan ->
                            MaterialDialog(this)
                                .show {
                                cornerRadius(16f)
                                title(text = "Manajemen Jabatan")
                                input(
                                    prefill = jabatan.position_name,
                                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                                ) { dialog, text ->
                                    MaterialDialog(this@ManajemenJabatanActivity).show {
                                        title(text = "Ubah Jabatan")
                                        message(text = "Apakah anda yakin ingin mengubah jabatan ini menjadi $text?")
                                        positiveButton(text = "Ya") {
                                            vm.editPosition(jabatan.id, text.toString())
                                        }
                                        negativeButton(text = "Batal") {
                                            it.dismiss()
                                        }
                                    }
                                }
                                positiveButton(text = "Ubah")
                                negativeButton(text = "Hapus") {
                                    MaterialDialog(this@ManajemenJabatanActivity).show {
                                        title(text = "Hapus Jabatan")
                                        message(text = "Apakah anda yakin ingin menghapus jabatan ini?")
                                        positiveButton(text = "Ya") {
                                            vm.deletePosition(jabatan.id)
                                        }
                                        negativeButton(text = "Batal") {
                                            it.dismiss()
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })

        vm.crudResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        createAlertSuccess(this, it.data.message)
                        groupAdapter.clear()
                        vm.getPositions()
                    } else {
                        createAlertError(this, "Gagal", it.data.message)
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        btnTambahJabatan.setOnClickListener {
            MaterialDialog(this).show {
                cornerRadius(16f)
                title(R.string.tambah_jabatan)
                input(
                    hint = "Nama Jabatan",
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                ) { dialog, text ->
                    vm.addPosition(text.toString())
                }
                positiveButton(text = "Tambah")
                negativeButton(text = "Batal")

            }
        }


        vm.getPositions()
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvJabatan.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }
}
