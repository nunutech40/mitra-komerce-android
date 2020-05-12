package id.android.kmabsensi.presentation.partner.kategori

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.PartnerCategory
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_kategori_partner.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class KategoriPartnerActivity : BaseActivity() {

    private val vm: PartnerCategoryViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kategori_partner)

        setupToolbar("Daftar Kategori Partner")

        initRv()
        vm.getPartnerCategories()
        observeData()

        fabAddKategoriPartner.setOnClickListener {
            addPartnerCategoryDialog()
        }

    }

    private fun observeData() {
        vm.partnerCategories.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    if (layout_empty.isVisible) layout_empty.invis()
                    showSkeleton(rvPartnerCategory, R.layout.skeleton_list_jabatan, groupAdapter)
                }
                is UiState.Success -> {
                    hideSkeleton()
                    if (state.data.categories.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    if (state.data.status) {
                        state.data.categories.forEach {
                            groupAdapter.add(PartnerCategoryItem(it, ::onPartnerCategoryClicked))
                        }
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })

        vm.crudResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (it.data.status) {
                        createAlertSuccess(this, it.data.message)
                        groupAdapter.clear()
                        vm.getPartnerCategories()
                    } else {
                        createAlertError(this, "Gagal", it.data.message)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })

    }

    fun initRv() {
        rvPartnerCategory.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.divider
                    ), true
                )
            )
            adapter = groupAdapter
        }
    }

    private fun addPartnerCategoryDialog() {
        MaterialDialog(this).show {
            cornerRadius(16f)
            title(text = "Tambah Kategori")
            input(
                hint = "Nama Kategori Partner",
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            ) { dialog, text ->
                vm.addPartnerCategory(text.toString())
            }
            positiveButton(text = "Tambah")
            negativeButton(text = "Batal")
        }
    }

    private fun onPartnerCategoryClicked(partnerCategory: PartnerCategory) {
        MaterialDialog(this)
            .show {
                cornerRadius(16f)
                title(text = "Manajemen Kategori Partner")
                input(
                    prefill = partnerCategory.partnerCategoryName,
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                ) { dialog, text ->
                    MaterialDialog(this@KategoriPartnerActivity).show {
                        title(text = "Ubah Kategori")
                        message(text = "Apakah anda yakin ingin mengubah kategori ${partnerCategory.partnerCategoryName} menjadi $text?")
                        positiveButton(text = "Ya") {
                            vm.editPartnerCategory(partnerCategory.id, text.toString())
                        }
                        negativeButton(text = "Batal") {
                            it.dismiss()
                        }
                    }
                }
                positiveButton(text = "Ubah")
                negativeButton(text = "Hapus") {
                    MaterialDialog(this@KategoriPartnerActivity).show {
                        title(text = "Hapus Kategori")
                        message(text = "Apakah anda yakin ingin menghapus kategori ${partnerCategory.partnerCategoryName}?")
                        positiveButton(text = "Ya") {
                            vm.deletePartnerCategory(partnerCategory.id)
                        }
                        negativeButton(text = "Batal") {
                            it.dismiss()
                        }
                    }
                }
            }

    }
}
