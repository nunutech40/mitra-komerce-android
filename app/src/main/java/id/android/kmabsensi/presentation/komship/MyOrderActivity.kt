package id.android.kmabsensi.presentation.komship

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doAfterTextChanged
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.komship.OrderByPartnerParams
import id.android.kmabsensi.databinding.ActivityMyOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.dataorder.DataOrderFragment
import id.android.kmabsensi.presentation.komship.ordercart.OrderCartActivity
import kotlinx.android.synthetic.main.activity_checkin.*
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class MyOrderActivity : BaseActivityRf<ActivityMyOrderBinding>(
    ActivityMyOrderBinding::inflate
) {
    private lateinit var pagerAdapter: MyOrderPagerAdapter
    private var pagePosition = 0
    lateinit var dateFrom: Date
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Orderku", isBackable = true, isCart = true)
        setupPager()
        setupListener()
        setupCurrentPage()
    }

    private fun setupCurrentPage() {
        if (intent.getIntExtra("_currentPage", 0) != 0) {
            val page = intent.getIntExtra("_currentPage", 0)
            when (page) {
                1 -> binding.viewPager.currentItem = 1
            }
        }
    }

    private fun setupListener() {

        binding.toolbar.apply {

            btnMyOrder.setOnClickListener {
                startActivity<OrderCartActivity>()
            }

            btnFilter.setOnClickListener {
                if (pagePosition == 1) {
                    setupBottomSheatFilterDataOrder()
                } else if (pagePosition == 2) {
                    setupBottomSheatFilterLeads()
                }
            }

            etSearch.doAfterTextChanged {
                if (pagePosition == 1) {
                    val fm2 = supportFragmentManager
                    val fDataOrder2 = fm2.fragments[1] as DataOrderFragment
                    fDataOrder2.searchOrder(it.toString())
                }
            }
        }
    }

    private fun setupPager() {
        pagerAdapter = MyOrderPagerAdapter(supportFragmentManager)
        binding.viewPager.apply {
            adapter = pagerAdapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    pagePosition = position
                    when (position) {
                        0 -> setupToolbar("Orderku", isBackable = true, isCart = true)
                        1 -> setupToolbar(
                            "Orderku",
                            isBackable = true,
                            isSearch = true,
                            isFilter = true
                        )
                        2 -> setupToolbar("Orderku", isBackable = true, isFilter = true)
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
        }
        binding.tabLayout.apply {
            setupWithViewPager(binding.viewPager)
        }

    }

    fun setupBottomSheatFilterDataOrder() {
        val bottomSheet = layoutInflater.inflate(R.layout.bottomsheet_filter_data_order, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val btnStartDate = bottomSheet.findViewById<TextInputEditText>(R.id.tie_first_date)
        val btnLastDate = bottomSheet.findViewById<TextInputEditText>(R.id.tie_last_date)
        val btnApply = bottomSheet.findViewById<AppCompatButton>(R.id.btn_apply)
        val chipGroup = bottomSheet.findViewById<ChipGroup>(R.id.chip_group)
        dialog.setContentView(bottomSheet)
        dialog.show()

        btnStartDate.setOnClickListener {
            pickDate(btnStartDate)
        }

        btnLastDate.setOnClickListener {
            if (btnStartDate.text.toString() != "") {
                pickLastDate(btnStartDate.text.toString(), btnLastDate)
            } else {
                toast("Pilih Tanggal Awal terlebih dahulu!")
            }
        }

        var chipSelected: Int? = View.NO_ID
        chipGroup.setOnCheckedChangeListener { group, id ->
            if (id == View.NO_ID) {
                group.check(chipSelected!!)
                return@setOnCheckedChangeListener
            }
            chipSelected = orderStatus(id)
        }

        btnApply.setOnClickListener {
            val sDate = btnStartDate.text.toString()
            val lDate = btnLastDate.text.toString()
            if (PreferencesHelper(this).getInt(partnerOrder) != 0) {
                val fm = supportFragmentManager
                val fDataOrder = fm.fragments[1] as DataOrderFragment
                fDataOrder.filterOrder(
                    PreferencesHelper(this).getInt(partnerOrder),
                    OrderByPartnerParams(
                        1,
                        sDate,
                        lDate,
                        "COD",
                        if (chipSelected == 121) null else chipSelected
                    )
                )
                dialog.dismiss()
            }
        }
    }

    private fun orderStatus(idChip: Int): Int {
        return when (idChip) {
            2131362203 -> 0
            2131362205 -> 1
            2131362214 -> 2
            2131362838 -> 3
            2131361928 -> 4
            else -> 121
        }
    }

    private fun pickDate(view: TextInputEditText) {
        var selected = ""
        MaterialDialog(this).show {
            datePicker { dialog, date ->
                // Use date (Calendar)
                dialog.dismiss()
                dateFrom = date.time

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selected = dateFormat.format(date.time)
                view.text = selected.toEditable()
            }
        }
    }

    private fun pickLastDate(sDate: String, view: TextInputEditText) {
        MaterialDialog(this).show {
            datePicker { dialog, date ->
                // Use date (Calendar)
                dialog.dismiss()
                dateFrom = date.time
                // TODO { validate start date < last date }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selected = dateFormat.format(date.time)
                view.text = selected.toEditable()
            }
        }
    }

    fun setupBottomSheatFilterLeads() {
        val bottomSheet = layoutInflater.inflate(R.layout.bottomsheet_filter_leads, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val btnDate = bottomSheet.findViewById<TextInputEditText>(R.id.tie_date)
        dialog.setContentView(bottomSheet)
        dialog.show()
        btnDate.setOnClickListener {
            pickDate(btnDate)
        }
    }
}

const val partnerOrder = "Partner_Order"

