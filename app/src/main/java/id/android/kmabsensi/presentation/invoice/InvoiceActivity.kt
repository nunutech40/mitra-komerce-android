package id.android.kmabsensi.presentation.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.d
import com.google.android.material.tabs.TabLayoutMediator
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.data.remote.response.invoice.Invoice
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.utils.UiState
import kotlinx.android.synthetic.main.activity_invoice.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvoiceActivity : BaseActivity() {

    private val vm: PartnerViewModel by viewModel()

    // tab titles
    private val tabTitles = arrayOf("Aktif", "Riwayat")
    private var invoiceType = 1
    private var partnerFilterSelectedId = 0
    private val partners = mutableListOf<SimplePartner>()

    private lateinit var activeInvoiceFragment: InvoiceActiveFragment
    private lateinit var historyInvoiceFragment: HistoryInvoiceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        setupToolbar("Invoice", isFilterVisible = true)

        activeInvoiceFragment = InvoiceActiveFragment()
        historyInvoiceFragment = HistoryInvoiceFragment()

        val fragments = mutableListOf<Fragment>()
        fragments.add(activeInvoiceFragment)
        fragments.add(historyInvoiceFragment)

        pager.isUserInputEnabled = false
        pager.adapter = ViewPagerOrderFragmentAdapter(fragments, supportFragmentManager, lifecycle)
        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        observePartners()

        btnFilter.setOnClickListener {
            showDialogFilter()
        }
    }

    private fun observePartners() {
        vm.getSimplePartners()
        vm.simplePartners.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    hideSkeleton()
                    if (state.data.status) {
                        partners.addAll(state.data.partners)
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })
    }

    private fun showDialogFilter() {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_filter_invoice, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val spinnerInvoiceType = customView.findViewById<Spinner>(R.id.spinnerInvoiceType)
        val spinnerPartner = customView.findViewById<Spinner>(R.id.spinnerPartner)
        val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

        val partnerName = mutableListOf<String>()
        partnerName.add("Semua")
        partners.forEach { partnerName.add(it.noPartner + " - " + it.fullName) }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // spinner izin
        ArrayAdapter.createFromResource(
            this,
            R.array.invoice_type,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerInvoiceType.adapter = adapter

            spinnerInvoiceType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        invoiceType = position + 1
                    }

                }
        }

        ArrayAdapter(this, R.layout.spinner_item, partnerName).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPartner.adapter = adapter
            spinnerPartner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    partnerFilterSelectedId = if (position == 0) {
                        0
                    } else {
                        partners[position - 1].id
                    }
                }

            }
        }

        buttonFilter.setOnClickListener {
            dialog.dismiss()
            if (pager.currentItem == 0) {
                activeInvoiceFragment.filterInvoice(invoiceType, partnerFilterSelectedId)
            } else {
                historyInvoiceFragment.filterInvoice(invoiceType, partnerFilterSelectedId)
            }
        }

        dialog.show()

    }
}

class ViewPagerOrderFragmentAdapter(
    private val fragments: List<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    /* because this is static, i think its ok if i hardcode that size */
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}
