package id.android.kmabsensi.presentation.invoice

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetail
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailItem
import id.android.kmabsensi.presentation.invoice.item.OnInvoiceDetailListener
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_manage_invoice_detail.*
import kotlinx.android.synthetic.main.item_row_invoice_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageInvoiceDetailActivity : BaseActivity() {

    private val invoiceVM: InvoiceViewModel by viewModel()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val invoiceDetailItems = mutableListOf<InvoiceDetailItem>()
    private val deleteItemSelected = mutableListOf<Int>()

    private var partnerSelected: SimplePartner? = null
    private var isAdminInvoice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_invoice_detail)

        setupToolbar("Tagihan", isWhiteBackground = true)
        isAdminInvoice = intent.getBooleanExtra(IS_INVOICE_ADMIN_KEY, false)
        partnerSelected = intent.getParcelableExtra(SIMPLE_PARTNER_DATA_KEY)
        initView()
        initRv()

        btnAdd.setOnClickListener {
            showCrudDialog("Tambah Tagihan")
        }

        btnOke.setOnClickListener {
            finish()
        }

        btnDelete.setOnClickListener {
            invoiceDetailItems.forEach {
                it.showDeleteSession()
                initDeleteViewSession()
            }
        }

        observeInvoiceItems()
    }

    private fun initView() {
        if (!isAdminInvoice) {
            btnAdd.gone()
        } else {
            btnAdd.visible()
        }
    }


    private fun initDeleteViewSession() {
        btnAdd.gone()
        btnDelete.gone()
        layoutTotalTagian.gone()
        layoutItemDelete.visible()
        btnPilihSemua.visible()

        btnDeleteItem.setOnClickListener {
            if (deleteItemSelected.isNotEmpty()) {
                deleteItemSelected.forEach {
                    InvoiceDetailData.removeItem(it)
                }
                layoutTotalTagian.visible()
                layoutItemDelete.gone()
                btnPilihSemua.gone()
                Handler().postDelayed({
                    invoiceDetailItems.forEach {
                        it.cancelDeleteSession()
                    }
                }, 300)

            } else {
                invoiceDetailItems.forEach {
                    it.cancelDeleteSession()
                }
                layoutTotalTagian.visible()
                layoutItemDelete.gone()
                btnAdd.visible()
                btnDelete.visible()
                btnPilihSemua.gone()
            }
        }

        btnPilihSemua.setOnClickListener {
            invoiceDetailItems.forEach {
                it.viewHolder.itemView.checkBoxDelete.isChecked = true
            }
        }
    }

    private fun observeInvoiceItems() {
        InvoiceDetailData.invoiceItemsData.observe(this, Observer {
            groupAdapter.clear()
            invoiceDetailItems.clear()
            it.forEach {
                val item = InvoiceDetailItem(it, object : OnInvoiceDetailListener {
                    override fun onEditClicked(invoiceDetail: InvoiceDetail, position: Int) {
                        showCrudDialog(
                            "Ubah Tagihan",
                            invoiceDetail.itemName,
                            invoiceDetail.itemPrice,
                            invoiceDetail.itemDescription,
                            position
                        )
                    }

                    override fun onCheckBoxClicked(invoiceDetail: InvoiceDetail, position: Int) {
                        if (invoiceDetail.selected) {
                            deleteItemSelected.add(position)
                        } else {
                            deleteItemSelected.removeAt(deleteItemSelected.indexOfFirst { it == position })
                        }
                        textDeleteSelected.text = "${deleteItemSelected.size} item terpilih"

                        if (deleteItemSelected.isEmpty()) btnDeleteItem.text =
                            "BATAL" else btnDeleteItem.text = "HAPUS ITEM"
                    }
                })
                invoiceDetailItems.add(item)
                groupAdapter.add(item)
            }
            textTotalTagihan.text = convertRp(it.sumBy { it.itemPrice }.toDouble())

            if (it.isNotEmpty()) btnDelete.visible() else btnDelete.gone()

        })
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvInvoiceDetail.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }


    private fun showCrudDialog(
        title: String,
        itemName: String? = null,
        itemPrice: Int? = null,
        itemDescription: String? = null,
        position: Int = 0
    ) {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_crud_detail_tagihan, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val textTitle = customView.findViewById<TextView>(R.id.textTitle)
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val edtItemName = customView.findViewById<EditText>(R.id.edtItemName)
        val edtItemPrice = customView.findViewById<EditText>(R.id.edtItemPrice)
        val edtDescription = customView.findViewById<EditText>(R.id.edtDescription)
        val btnOke = customView.findViewById<Button>(R.id.btnOke)

        textTitle.text = title

        edtItemPrice.addTextChangedListener(RupiahTextWatcher(edtItemPrice))

        itemName?.let { edtItemName.setText(it) }
        itemPrice?.let { edtItemPrice.setText(it.toString()) }
        itemDescription?.let { edtDescription.setText(it) }

        fun validation(): Boolean {
            val item = ValidationForm.validationInput(edtItemName, "Tidak boleh kosong")
            val price = ValidationForm.validationInput(edtItemPrice, "Tidak boleh kosong")
            return item && price
        }

        btnOke.setOnClickListener {
            if (!validation()) {
                return@setOnClickListener
            }
            if (itemName == null) {
                InvoiceDetailData.addInvoiceItem(
                    InvoiceDetail(
                        edtItemName.text.toString(),
                        edtItemPrice.text.toString().replace(".", "").toInt(),
                        edtDescription.text.toString()
                    )
                )
            } else {
                InvoiceDetailData.updateInvoiceItem(
                    InvoiceDetail(
                        edtItemName.text.toString(),
                        edtItemPrice.text.toString().replace(".", "").toInt(),
                        edtDescription.text.toString()
                    ),
                    position
                )
            }

            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
