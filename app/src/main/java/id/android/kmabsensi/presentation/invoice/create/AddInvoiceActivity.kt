package id.android.kmabsensi.presentation.invoice.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.CreateInvoiceBody
import id.android.kmabsensi.data.remote.body.InvoiceItem
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.InvoiceDetailData
import id.android.kmabsensi.presentation.invoice.InvoiceViewModel
import id.android.kmabsensi.presentation.invoice.ManageInvoiceDetailActivity
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetail
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailBasic
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailBasicItem
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_add_invoice.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddInvoiceActivity : BaseActivity() {

    private val invoiceVM: InvoiceViewModel by viewModel()
    private val partnerVM: PartnerViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var listInvoiceDetail = mutableListOf<InvoiceDetail>()
    private var partnerSelected: Partner? = null
    private val PICK_PARTNER_RC = 112

    private val calendar = Calendar.getInstance()
    private val monthCalendar = calendar.get(Calendar.MONTH)
    private var yearSelected: Int = 0
    private var monthSelected: Int = 0

    private var titleYear = 0
    private var titleMonth = ""

    private var isAdminInvoice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_invoice)
        isAdminInvoice = intent.getBooleanExtra(IS_INVOICE_ADMIN_KEY, false)
        setupToolbar(if (isAdminInvoice) "Invoice Admin" else "Invoice Gaji SDM")
        initRv()
        initView()

        switchStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switchLabel.text = "Free"
                layoutDetailTagihan.gone()
                rvInvoiceDetail.gone()
                textTotalTagihan.text = "Rp 0"
                InvoiceDetailData.clear()
            } else {
                switchLabel.text = "Not Free"
                layoutDetailTagihan.visible()
                rvInvoiceDetail.visible()
            }
        }

        InvoiceDetailData.invoiceItemsData.observe(this, Observer { invoices ->
            if (invoices.isNotEmpty()) {
                btnUbahTagihan.text = if (isAdminInvoice) "UBAH TAGIHAN" else "MASUKKAN GAJI SDM"

                listInvoiceDetail.clear()
                groupAdapter.clear()
                listInvoiceDetail.addAll(invoices)
                invoices.map { InvoiceDetailBasic(it.itemName, it.itemPrice, it.itemDescription) }
                    .forEach {
                        groupAdapter.add(InvoiceDetailBasicItem(it))
                    }
                textTotalTagihan.text = convertRp(invoices.sumBy { it.itemPrice }.toDouble())
            } else {
                listInvoiceDetail.clear()
                btnUbahTagihan.text = if (isAdminInvoice) "TAMBAH TAGIHAN" else "MASUKKAN GAJI SDM"
                groupAdapter.clear()
                textTotalTagihan.text = "Rp 0"
            }
        })

        edtPilihPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(PICK_PARTNER_RC)
        }

        btnUbahTagihan.setOnClickListener {
            startActivity<ManageInvoiceDetailActivity>(
                PARTNER_DATA_KEY to partnerSelected,
                IS_INVOICE_ADMIN_KEY to isAdminInvoice
            )
        }

        //spinner month
        ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMonth.adapter = adapter

                spinnerMonth.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) {
                                monthSelected = position
                                titleMonth = parent?.getItemAtPosition(position).toString()
                                setTitleInvoice(titleMonth, titleYear.toString())
                            }
                        }

                    }
            }

        //spinner year
        ArrayAdapter(this, R.layout.spinner_item, getYearData())
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerYear.adapter = adapter

                spinnerYear.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) {
                                yearSelected = spinnerYear.selectedItem.toString().toInt()
                                titleYear = yearSelected
                                setTitleInvoice(titleMonth, titleYear.toString())
                            }

                        }

                    }
            }

        buttonAddInvoice.setOnClickListener {
            if (!formValidation()) {
                return@setOnClickListener
            }
            if (yearSelected == 0 || monthSelected == 0) {
                createAlertError(
                    this,
                    "Warning!",
                    "Tentukan periode tagihan dahulu",
                    duration = 3000
                )
                return@setOnClickListener
            }
            if (listInvoiceDetail.isEmpty()) {
                if (!switchStatus.isChecked) {
                    createAlertError(
                        this,
                        "Warning!",
                        "Tambahkan item tagihan terlebih dahulu",
                        duration = 3000
                    )
                    return@setOnClickListener
                }
            }
            val month = if (monthSelected < 10) "0$monthSelected" else "$monthSelected"
            val body = CreateInvoiceBody(
                user_requester_id = invoiceVM.getUser().id,
                user_to_id = partnerSelected?.id ?: 0,
                title = edtInvoiceTitle.text.toString(),
                description = "",
                invoice_type = if (isAdminInvoice) 1 else 2,
                invoice_period = "$yearSelected-$month-01",
                items = listInvoiceDetail.map { InvoiceItem.from(it) }
            )
            invoiceVM.createInvoice(body)
        }

        observeData()
        observeDataSdm()

        initDateInvoice()
    }

    private fun initDateInvoice() {
        titleMonth = if (monthCalendar == 0) {
            resources.getStringArray(R.array.month_array).toList()[12]
        } else {
            resources.getStringArray(R.array.month_array).toList()[monthCalendar]
        }
        titleYear =
            if (monthCalendar == 0) calendar.get(Calendar.YEAR) - 1 else calendar.get(Calendar.YEAR)
    }

    private fun observeData() {
        invoiceVM.createInvoiceResponse.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status) {
                        val intent = Intent()
                        intent.putExtra("message", state.data.message)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Warning!", state.data.message, duration = 3000)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    private fun initView() {
        textDate.text = getTodayDateTimeDay()
        if (!isAdminInvoice) {
            labelTagihan.gone()
            switchLabel.gone()
            switchStatus.gone()
        }
        btnUbahTagihan.text = if (isAdminInvoice) "TAMBAH TAGIHAN" else "MASUKKAN GAJI SDM"
    }

    fun initRv() {
        rvInvoiceDetail.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        this.context,
                        R.drawable.divider
                    ), false
                )
            )
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_PARTNER_RC && resultCode == Activity.RESULT_OK) {
            partnerSelected = data?.getParcelableExtra<Partner>(PARTNER_DATA_KEY)
            edtPilihPartner.error = null
            edtPilihPartner.setText(partnerSelected?.fullName)


            val invoiceType = if (isAdminInvoice) "Admin" else "Gaji SDM"
            val title = "Invoice $invoiceType Partner ${partnerSelected!!.partnerDetail.noPartner} ${partnerSelected!!.fullName} $titleMonth $titleYear"
            edtInvoiceTitle.setText(title)

            spinnerMonth.setSelection(
                resources.getStringArray(R.array.month_array).toList()
                    .indexOfFirst { it == "$titleMonth" })
            spinnerYear.setSelection(getYearData().indexOfFirst { it == "$titleYear" })

            if (!isAdminInvoice) partnerVM.getSdmByPartner(partnerSelected!!.partnerDetail.noPartner.toInt())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun observeDataSdm() {
        partnerVM.sdmByPartner.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    InvoiceDetailData.clear()
                    state.data.data.forEach {
                        InvoiceDetailData.addInvoiceItem(
                            InvoiceDetail(
                                it.full_name,
                                0,
                                "",
                                userId = it.id
                            )
                        )
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    fun setTitleInvoice(month: String, year: String) {
        partnerSelected?.let {
            val invoiceType = if (isAdminInvoice) "Admin" else "Gaji SDM"
            val title = "Invoice $invoiceType Partner ${it.partnerDetail.noPartner} ${it.fullName} $month $year"
            edtInvoiceTitle.setText(title)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InvoiceDetailData.clear()
    }

    private fun formValidation(): Boolean {
        val title =
            ValidationForm.validationInput(edtInvoiceTitle, "Judul Invoice tidak boleh kosong")
        val partner = ValidationForm.validationInput(edtPilihPartner, "Partner tidak boleh kosong")

        return title && partner
    }
}
