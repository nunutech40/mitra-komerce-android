package id.android.kmabsensi.presentation.invoice

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
import com.google.android.material.tabs.TabLayoutMediator
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.UiState
import kotlinx.android.synthetic.main.activity_invoice.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvoiceActivity : BaseActivity() {

    private val sdmVM: KelolaDataSdmViewModel by viewModel()
    private val vm: PartnerViewModel by viewModel()

    // tab titles
    private val tabTitles = arrayOf("Aktif", "Riwayat")
    private var invoiceType = 1
    private var partnerFilterSelectedId = 0
    private val partners = mutableListOf<SimplePartner>()
    private var leaderIdSelected = 0
    private var leaders = mutableListOf<User>()

    private lateinit var activeInvoiceFragment: InvoiceActiveFragment
    private lateinit var historyInvoiceFragment: HistoryInvoiceFragment

    private lateinit var dialogFilter: MaterialDialog
    private var spinnerLeader: Spinner? = null
    private var spinnerPartner: Spinner? = null


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


        btnFilter.setOnClickListener {
            showDialogFilter()
            if (leaders.isEmpty()) sdmVM.getUserManagement(2)
            if (partners.isEmpty()) vm.getSimplePartners()
        }

        observePartners()

    }

    private fun observePartners() {
        vm.simplePartners.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    hideSkeleton()
                    if (state.data.status) {
                        partners.addAll(state.data.partners)
                        val partnerName = mutableListOf<String>()
                        partnerName.add("Semua")
                        partners.forEach { partnerName.add(it.noPartner + " - " + it.fullName) }

                        ArrayAdapter(this, R.layout.spinner_item, partnerName).also { adapter ->
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerPartner?.adapter = adapter
                            spinnerPartner?.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
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

                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })

        sdmVM.userManagementData.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    leaders.addAll(state.data.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })

                    val userManagementNames = mutableListOf<String>()
                    userManagementNames.add("Semua")
                    leaders.forEach { userManagementNames.add(it.full_name) }
                    ArrayAdapter<String>(
                        this,
                        R.layout.spinner_item,
                        userManagementNames
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinnerLeader?.adapter = adapter

                        spinnerLeader?.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    leaderIdSelected = if (position == 0) {
                                        0
                                    } else {
                                        leaders[position - 1].id
                                    }
                                }

                            }
                    }
                }
                is UiState.Error -> {

                }
            }
        })
    }

    private fun showDialogFilter() {
        if (!::dialogFilter.isInitialized) {

            dialogFilter = MaterialDialog(this)
                .customView(R.layout.dialog_filter_invoice, noVerticalPadding = true)
            val customView = dialogFilter.getCustomView()
            val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
            val spinnerInvoiceType = customView.findViewById<Spinner>(R.id.spinnerInvoiceType)
            spinnerPartner = customView.findViewById(R.id.spinnerPartner)
            spinnerLeader = customView.findViewById(R.id.spinnerLeader)
            val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

            // spinner invoice type
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
                            invoiceType = position
                        }

                    }
            }

            btnClose.setOnClickListener {
                dialogFilter.dismiss()
            }

            buttonFilter.setOnClickListener {
                dialogFilter.dismiss()
                if (pager.currentItem == 0) {
                    activeInvoiceFragment.filterInvoice(
                        invoiceType,
                        partnerFilterSelectedId,
                        leaderIdSelected
                    )
                } else {
                    historyInvoiceFragment.filterInvoice(
                        invoiceType,
                        partnerFilterSelectedId,
                        leaderIdSelected
                    )
                }
            }
        }
        dialogFilter.show()
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
