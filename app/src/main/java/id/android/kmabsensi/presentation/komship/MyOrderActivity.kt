package id.android.kmabsensi.presentation.komship

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doAfterTextChanged
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.komship.OrderByPartnerParams
import id.android.kmabsensi.databinding.ActivityMyOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.presentation.komship.dataorder.DataOrderFragment
import id.android.kmabsensi.presentation.komship.leads.LeadsOrderFragment
import id.android.kmabsensi.presentation.komship.ordercart.OrderCartActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_checkin.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.bottomsheet_filter_data_order.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class MyOrderActivity : BaseActivityRf<ActivityMyOrderBinding>(
    ActivityMyOrderBinding::inflate
) {
    private val vm: MyOrderViewModel by inject()
    private lateinit var pagerAdapter: MyOrderPagerAdapter
    private var pagePosition = 0
    lateinit var dateFrom: Date
    lateinit var dateTo: Date
    private var sklCart : SkeletonScreen? = null

    private lateinit var btnDate: TextInputEditText
    private lateinit var btnApplyLeads: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar(getString(R.string.orderku), isBackable = true, isCart = true)
        setupObserver()
        setupPager()
        setupListener()
        setupCurrentPage()
    }

    private fun setupObserver() {
        vm.getCart()
        vm.cartState.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    showSkeleton()
                    Timber.tag("_cartState").d("on Loading ")
                }
                is UiState.Success -> {
                    sklCart?.hide()
                    val totalCart = it.data.data?.size
                    binding.apply {
                        if (totalCart != 0) {
                            toolbar.tvCartBadge.visible()
                            toolbar.tvCartBadge.text = totalCart.toString()
                        } else toolbar.tvCartBadge.gone()
                    }
                }
                is UiState.Error -> {
                    sklCart?.hide()
                    Timber.d(it.throwable)
                }
            }
        })
    }

    private fun setupCurrentPage() {
        if (intent.getIntExtra("_currentPage", 0) != 0) {
            when (intent.getIntExtra("_currentPage", 0)) {
                1 -> binding.viewPager.currentItem = 1
            }
        }
    }

    private fun setupListener() {

        binding.toolbar.apply {

            btnCart.setOnClickListener {
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

    fun refreshCart(){
        vm.getCart()
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

    private fun setupBottomSheatFilterDataOrder() {
        val bottomSheet = layoutInflater.inflate(R.layout.bottomsheet_filter_data_order, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val btnStartDate = bottomSheet.findViewById<TextInputEditText>(R.id.tie_first_date)
        val btnLastDate = bottomSheet.findViewById<TextInputEditText>(R.id.tie_last_date)
        val btnApply = bottomSheet.findViewById<AppCompatButton>(R.id.btn_apply)
        val chipGroup = bottomSheet.findViewById<ChipGroup>(R.id.chip_group)
        val warningLast = bottomSheet.findViewById<AppCompatTextView>(R.id.tv_warning_last_date)
        val warningFirst = bottomSheet.findViewById<AppCompatTextView>(R.id.tv_warning_first_date)
        dialog.setContentView(bottomSheet)
        dialog.show()
        btnStartDate.text = getSevenDayDate().toEditable()
        btnLastDate.text = getTodayDate().toEditable()
        dialog.semua.isChecked = true
        btnStartDate.setOnClickListener {
            pickDate(btnStartDate)
            warningFirst.gone()
        }

        btnLastDate.setOnClickListener {
            if (btnStartDate.text.toString() != "") {
                pickLastDate(btnLastDate, warningLast)
            } else {
                warningFirst.visible()
            }
        }

        var chipSelected: Int? = View.NO_ID
        chipGroup.setOnCheckedChangeListener { group, id ->
            if (id == View.NO_ID) {
                group.check(chipSelected!!)
                return@setOnCheckedChangeListener
            }
            Log.d("onChipSelected", "setupBottomSheatFilterDataOrder: $id")
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
                        if(chipSelected!! < 0) null else chipSelected
                    )
                )
                Log.d("onStatusOrder", "statusOrder: $chipSelected")
                dialog.dismiss()
            }
        }
    }

    private fun orderStatus(idChip: Int): Int {
        return when (idChip) {
            2131362210 -> 0
            2131362212 -> 1
            2131362221 -> 2
            2131362853 -> 3
            2131361929 -> 4
            else -> -1
        }
    }

    private fun pickDate(view: TextInputEditText) {
        MaterialDialog(this).show {
            datePicker { dialog, date ->
                // Use date (Calendar)
                dialog.dismiss()
                dateFrom = date.time

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selected = dateFormat.format(date.time)
                view.text = selected.toEditable()
            }
        }
    }

    private fun pickLastDate(view: TextInputEditText, warningLast: AppCompatTextView){
        MaterialDialog(this).show {
            datePicker { dialog, date ->
                // Use date (Calendar)
                dialog.dismiss()
                dateTo = date.time
                if (!dateTo.before(dateFrom)){
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val selected = dateFormat.format(dateTo)
                    view.text = selected.toEditable()
                    warningLast.gone()
                }else{
                    view.text = "".toEditable()
                    warningLast.visible()
                }
            }
        }
    }

    //leads
    private fun setupBottomSheatFilterLeads() {
        val bottomSheet = layoutInflater.inflate(R.layout.bottomsheet_filter_leads, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        btnDate = bottomSheet.findViewById<TextInputEditText>(R.id.tie_date)
        btnApplyLeads = bottomSheet.findViewById<AppCompatButton>(R.id.btn_apply)
        btnDate.addTextChangedListener(filterLeadsTextWatcher)
        dialog.setContentView(bottomSheet)
        dialog.show()
        btnDate.setOnClickListener {
            pickDate(btnDate)
        }

        btnApplyLeads.setOnClickListener {
            val date = btnDate.text.toString()
            val fm = supportFragmentManager
            val fLeads = fm.fragments[1] as LeadsOrderFragment

            fLeads.filterLeads(
                PreferencesHelper(this).getInt(PreferencesHelper.ID_USER.toString()),
                PreferencesHelper(this).getInt(PreferencesHelper.ID_PARTNER.toString()),
                date
            )
            dialog.dismiss()
        }
    }

    private fun showSkeleton(){
        if (sklCart!=null){
            sklCart = Skeleton.bind(binding.toolbar.tvCartBadge)
                .load(R.layout.skeleton_badge)
                .show()
        }else{
            sklCart?.show()

        }
    }

    private val filterLeadsTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val filerDate: String = btnDate.text.toString().trim()
            if (filerDate.isNotEmpty()){
                btnApplyLeads.isEnabled = true
                btnApplyLeads.background = getDrawable(R.drawable.background_orange_10dp)
            } else {
                btnApplyLeads.isEnabled = false
                btnApplyLeads.background =getDrawable(R.drawable.bg_button_disable_komboard)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
}
const val partnerOrder = "Partner_Order"

