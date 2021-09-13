package id.android.kmabsensi.presentation.management


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import iammert.com.expandablelib.Section
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.databinding.FragmentHomeManagementBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.checkin.CheckinActivity
import id.android.kmabsensi.presentation.coworking.CheckinCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.invoice.InvoiceActivity
import id.android.kmabsensi.presentation.invoice.report.InvoiceReportActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingCartActivity
import id.android.kmabsensi.presentation.myevaluation.MyEvaluationActivity
import id.android.kmabsensi.presentation.partner.grafik.GrafikPartnerActivity
import id.android.kmabsensi.presentation.permission.PermissionActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.role.RoleViewModel
import id.android.kmabsensi.presentation.scanqr.ScanQrActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.presentation.sdm.home.MenuModels
import id.android.kmabsensi.presentation.sdm.home.MenusAdapter
import id.android.kmabsensi.presentation.sdm.modekerja.ModeKerjaActivity
import id.android.kmabsensi.presentation.sdm.shift.SdmShiftActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_management.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeManagementFragment : BaseFragmentRf<FragmentHomeManagementBinding>(
    FragmentHomeManagementBinding::inflate
) {

    private val vm: HomeViewModel by sharedViewModel()
    private val roleVM: RoleViewModel by viewModel()
    private lateinit var user: User
    private lateinit var myDialog: MyDialog
    private val REQ_SCAN_QR = 123
    var isCheckinButtonClicked = false
    private val FORMAT = "(- %02d:%02d:%02d )"
    private var countDownTimer: CountDownTimer? = null
    //for expandable layout
    val section = Section<String, Dashboard>()
    var isSectionAdded = false
    private var dashboard: Dashboard? = null

    private val cal = Calendar.getInstance()
    private val holidays = mutableListOf<Holiday>()

    private lateinit var menusAdapter : MenusAdapter
    private lateinit var dataUserCoworking : UserCoworkingSpace
    private var roleMenu = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = vm.getUserData()
        d { user.toString() }
        myDialog = MyDialog(requireContext())
        setupObserver()
        setupView()
        setupGreetings()
        setupListMenu()
        setupListener()

        vm.getJadwalShalat()
        vm.getCoworkUserData(user.id)
        getDashboardData()
        roleVM.getAccessMenuByPosition(user.position_id)
    }


    private fun setupListener() {
        binding?.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                user = vm.getUserData()
                vm.getJadwalShalat()
                vm.getCoworkUserData(user.id)
                getDashboardData()
                setupGreetings()
                roleVM.getAccessMenuByPosition(user.position_id)
            }
            btnDetailPartner.setOnClickListener {
                activity?.startActivity<GrafikPartnerActivity>(DASHBOARD_DATA_KEY to dashboard)
            }

            btnQrCode.setOnClickListener {
                val intent = Intent(context, ScanQrActivity::class.java)
                startActivityForResult(intent, REQ_SCAN_QR)
            }

            btnCheckinCoWorking.setOnClickListener {
                validateCoworking()
            }

            btnDataPresence.setOnClickListener {
                val params = llDdDataPresence.layoutParams
                params.height = if (llDdDataPresence.height == 0) ViewGroup.LayoutParams.WRAP_CONTENT else 0
                imgArrowDd.rotation = if (llDdDataPresence.height == 0) 180f else 0f
                llDdDataPresence.layoutParams = params
            }
        }
        //        btnPartnerCategory.setOnClickListener {
//            activity?.startActivity<KategoriPartnerActivity>()
//        }
    }

    private fun validateCoworking() {
        dataUserCoworking.apply {
            if (cowork_presence.isNotEmpty() && cowork_presence.last().checkout_date_time?.isEmpty()?:true){
                vm.checkOutCoworkingSpace(this.cowork_presence.last().id)
            }else{
                if (available_slot > 0) {
                    if (cowork_presence.size < 2) {
                        requireActivity().startActivityForResult<CheckinCoworkingActivity>(122,
                            "coworking" to dataUserCoworking
                            )
                    } else if (cowork_presence.size >= 2) {
                        createAlertError(
                            requireActivity(),
                            "Gagal",
                            "Kamu hanya bisa check in coworking space sebanyak 2 kali"
                        )
                    }
                }
            }
        }
    }

    /**
        role menu
     1 -> management
     2 -> management lead
     3 -> management growth
     */

    private fun setupView(){
        if (user.position_name.equals("Staff Growth", true)) {
            roleMenu = 3
        }
        if (user.position_name.lowercase().contains(getString(R.string.category_leader))) {
            roleMenu = 2
        }else {
            roleMenu = 1
        }
    }

    private fun setupListMenu() {
        menusAdapter = MenusAdapter(requireContext(), object : MenusAdapter.onAdapterListener{
            override fun onClick(data: MenuModels) {
                requireActivity().toast("data = ${data.name}")
                when(data.name){
                    "Datang" -> {
                        isCheckinButtonClicked = true
                        vm.presenceCheck(user.id)
                    }
                    "Pulang" -> {
                        isCheckinButtonClicked = false
                        vm.presenceCheck(user.id)
                    }
                    "Data Talent" ->{
                        if (user.position_id == 3 || user.position_id == 4 || user.position_id == 5) {
                            context?.startActivity<KelolaDataSdmActivity>(
                                IS_MANAGEMENT_KEY to false
                            )
                        } else {
                            context?.startActivity<KelolaDataSdmActivity>(
                                IS_MANAGEMENT_KEY to true,
                                USER_ID_KEY to user.id
                            )
                        }
                    }
                    "Form Izin" ->{
                        context?.startActivity<PermissionActivity>(USER_KEY to user)
                    }
                    "Data Izin" ->{
                        if (user.position_id == 3 || user.position_id == 4 || user.position_id == 5) {
                            activity?.startActivity<ManajemenIzinActivity>(IS_MANAGEMENT_KEY to false)
                        } else {
                            activity?.startActivity<ManajemenIzinActivity>(
                                IS_MANAGEMENT_KEY to true,
                                USER_ID_KEY to user.id
                            )
                        }
                    }
                    "Invoice" ->{
                        activity?.startActivity<InvoiceActivity>()
                    }
                    "Invoice Report" ->{
                        activity?.startActivity<InvoiceReportActivity>()
                    }
                    "Evaluasi Saya" ->{
                        activity?.startActivity<MyEvaluationActivity>()
                    }
                    "Shift" ->{
                        activity?.startActivity<SdmShiftActivity>()
                    }
                    "Partner" ->{
                        showGroupMenu(1)
                    }
                    "Orderku" ->{
                        activity?.startActivity<ShoppingCartActivity>("roleId" to 2)
                    }
                }
            }
        })
        binding?.rvMenu?.apply {
            setHasFixedSize(true)
            adapter = menusAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }
        menusAdapter.setData(vm.menuHome(roleMenu))

    }

    private fun setupObserver() {
        vm.dashboardData.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {

                    dashboard = it.data.data
                    binding?.apply {
                        dashboard.let { ds ->
                            tvPoint.text = ds?.user_kmpoin.toString()
                            tvTotalPresence.text = "${ds?.total_present}/${ds?.total_user}"
                            tvPartnerTotal.text = ds?.total_partner.toString()

                            tvBelumHadir.text = ds?.total_not_present.toString()
                            tvIzin.text = ds?.total_permission.toString()
                            tvCuti.text = ds?.total_holiday.toString()
                            progressPresence?.apply {
                                    progress = ds?.total_present?.toFloat()!!
                                    progressMax = ds.total_user.toFloat()
                            }
                        }
                    }

                    val workConfigs = it.data.data.work_config
                    val isWFH =
                        workConfigs.find { config -> config.key == ModeKerjaActivity.WORK_MODE }?.value == ModeKerjaActivity.WFH
                    val isManagementWFH =
                        isWFH && workConfigs.find { it.key == ModeKerjaActivity.WFH_USER_SCOPE }?.value?.contains(
                            "management",
                            true
                        ) ?: false
                    setWorkModeUI(isManagementWFH)

                    holidays.clear()
                    holidays.addAll(it.data.data.holidays)

                    if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        setHolidayView()
                    }
                }
                is UiState.Error -> {
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.presenceCheckResponse.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    onPresenceCheck(it.data)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.checkoutResponse.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        createAlertSuccess(activity, it.data.message)
                    } else {
                        createAlertError(requireActivity(), getString(R.string.label_gagal), getString(R.string.message_error_occured))
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        vm.jadwalShalatData.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    if (!swipeRefresh.isRefreshing) {
                    }
                }
                is UiState.Success -> {
                    try {
                        if (it.data.status.lowercase().equals(getString(R.string.ok), true)) {
                            val data = it.data.jadwal.data
                            val dzuhur = data?.dzuhur
                            val ashr = data?.ashar
                            setCountdown(dzuhur, ashr)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                is UiState.Error -> {
                }
            }
        })

        vm.coworkUserData.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    if (it.data.status) {
                        it.data.data[0].apply {
                            dataUserCoworking = this
                            if (status.equals("2")) {
                                binding?.tvChair?.text = "Tutup"
                                binding?.tvCofee?.gone()
                            }else binding?.tvCofee?.visible()
                            if (cowork_presence.isNotEmpty()){
                                if (cowork_presence.last().checkout_date_time.isNullOrEmpty()){
                                    binding?.tvCoworkingPresence?.text = "Check Out"
                                }else{
                                    binding?.tvCoworkingPresence?.text = "Check In"
                                }
                            }
                            binding?.apply {
                                tvChair.text = "$available_slot Kursi"
                            }
                        }
                    }
                }
                is UiState.Error -> {
                }
            }

        })

        vm.checkInCoworkingSpace.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) vm.getCoworkUserData(user.id)
                    else createAlertError(requireActivity(), "Failed", it.data.message)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    Timber.e(it.throwable)
                }
            }
        })

        vm.redeemPoin.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()

                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        showDialogSuccess(requireActivity(), message = it.data.message)
                        vm.getDashboardInfo(user.id)
                    } else {
                        createAlertError(requireActivity(), "Failed", it.data.message)
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })
    }

    private fun onPresenceCheck(presenceCheck: PresenceCheckResponse) {

        val isWFH = presenceCheck.work_config.find { config -> config.key == ModeKerjaActivity.WORK_MODE }?.value == ModeKerjaActivity.WFH

        if (presenceCheck.checkdeIn) {
            if (isCheckinButtonClicked) {
                MaterialDialog(requireContext()).show {
                    cornerRadius(16f)
                    title(text = "Check-In")
                    message(text = "Kamu sudah check-in hari ini")
                    positiveButton(text = "OK") {
                        it.dismiss()
                    }
                }
            } else {
                //checkout button clicked and already check in
                // cek jam pulang terlebih dahulu
                val currentTime = Calendar.getInstance()
                val now: Date = currentTime.time

                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 16)
//                cal.set(Calendar.MINUTE, 30)
                val jamPulang: Date = cal.time

                if (now.before(jamPulang)) {
                    (activity as HomeActivity).showDialogNotYetCheckout()
                } else {
                    // office name contain rumah, can direct selfie
                    if (presenceCheck.office_assigned.office_name.lowercase()
                            .contains("rumah") || isWFH
                    ) {
                        context?.startActivity<CheckinActivity>(
                            DATA_OFFICE_KEY to presenceCheck.office_assigned,
                            PRESENCE_ID_KEY to presenceCheck.presence_id
                        )
                    } else {
                        context?.startActivity<CekJangkauanActivity>(
                            DATA_OFFICE_KEY to presenceCheck.office_assigned,
                            PRESENCE_ID_KEY to presenceCheck.presence_id
                        )
                    }
                }
            }

        } else {
            if (isCheckinButtonClicked) {
                //checkin
                if (presenceCheck.office_assigned.office_name.lowercase()
                        .contains("rumah") || isWFH
                ) {
                    context?.startActivity<CheckinActivity>(
                        DATA_OFFICE_KEY to presenceCheck.office_assigned,
                        PRESENCE_ID_KEY to 0
                    )
                } else {
                    context?.startActivity<CekJangkauanActivity>(DATA_OFFICE_KEY to presenceCheck.office_assigned)
                }
            } else {
                // check in not yet
                val dialog = MaterialDialog(requireContext()).show {
                    cornerRadius(16f)
                    customView(
                        R.layout.dialog_maaf,
                        scrollable = false,
                        horizontalPadding = true,
                        noVerticalPadding = true
                    )
                }
                val customView = dialog.getCustomView()
                val close = customView.findViewById<ImageView>(R.id.close)
                close.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }


    private fun setWorkModeUI(isWFH: Boolean) {
        if (isWFH) {
            binding?.cvWfh?.visible()
        } else {
            binding?.cvWfh?.gone()
        }
    }

    private fun getSectionDashboard(dashboard: Dashboard): Section<String, Dashboard> {
        section.parent = dashboard.total_not_present.toString()
        section.children.add(dashboard)
        isSectionAdded = true
        return section
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 112 && resultCode == Activity.RESULT_OK) {
            vm.getCoworkUserData(user.id)
        }
        if (requestCode == REQ_SCAN_QR && resultCode == Activity.RESULT_OK) {
            val redeemPoin = data?.getIntExtra(getString(R.string.qrdata), 0)
            redeemPoin?.let {
                vm.redeemPoin(user.id, it)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*
     * 0 - SDM
     * 1 - Partner
     */
    private fun showGroupMenu(menu: Int) {
        swipeRefresh.gone()
//        containerHome.visible()
        val fragment = when (menu) {
            0 -> ManagementSdmMenuFragment()
            else -> ManagementPartnerMenuFragment()
        }
        childFragmentManager.beginTransaction().apply {
            replace(R.id.containerHome, fragment).commit()
        }
        (activity as HomeActivity).isOpenGroupMenu = true
    }

    fun hideGroupMenu() {
//        containerHome.gone()
        swipeRefresh.visible()
        childFragmentManager.popBackStack()
        (activity as HomeActivity).isOpenGroupMenu = false
    }

    fun getDashboardData() {
        vm.getDashboardInfo(user.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeManagementFragment()
    }

    private fun setHolidayView() {
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            binding?.tvCountDown?.text = "Hari Minggu"
        } else {
//            if (holidays.isNotEmpty()) {
//                txtNextTime.invis()
//                layoutHoliday.visible()
//                txtHolidayName.text = holidays[0].eventName
//                val dateStart: LocalDate = LocalDate.parse(holidays[0].startDate)
//                val dateEnd: LocalDate = LocalDate.parse(holidays[0].endDate)
//
//                txtHolidayDate.text = if (holidays[0].startDate == holidays[0].endDate)
//                    localDateFormatter(dateStart)
//                else
//                    "${localDateFormatter(dateStart, "dd MMM yyyy")} s.d ${
//                        localDateFormatter(
//                            dateEnd,
//                            "dd MMM yyyy"
//                        )
//                    }"
//            }
        }
        binding?.tvCountDown?.text = "Hari Libur"
        binding?.tvStatusWaktu?.invis()
//        dataHadir.gone()
//        expandableLayout.gone()
    }

    private fun setCountdown(time_zuhur: String?, time_ashar: String?) {

        if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            setHolidayView()
        } else {
            val (statusWaktu, differenceTime) = vm.getCountdownTime(
                time_zuhur,
                time_ashar
            )
            binding?.tvStatusWaktu?.text = statusWaktu

            if (differenceTime != 0.toLong()) {
                countDownTimer(differenceTime)
            } else {
                binding?.tvCountDown?.text = "-"
            }
        }
    }

    private fun setupGreetings() {
        val greeting = vm.setGreeting()
        binding?.tvUsername?.text = greeting
        binding?.imgProfile?.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )

        binding?.tvPosition?.text = user.position_name
    }

    private fun countDownTimer(ms: Long) {
        try {
            countDownTimer = object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    if (binding?.tvCountDown != null) {
                        val hour = (millisUntilFinished / 1000) / (60 * 60) % 24
                        val minute = (millisUntilFinished / 1000) / 60 % 60
                        val second = (millisUntilFinished / 1000) % 60
                        try {
                            binding?.tvCountDown?.text = String.format(
                                FORMAT,
                                hour,
                                minute,
                                second
                            )
                        } catch (e: Exception) {
                            Log.d("_txtCountdown", "onTick: ${e.message}")
                        }
                    }
                }

                override fun onFinish() {
                    binding?.tvCountDown?.text = "Waktu Tiba!"
                }

            }
            countDownTimer?.start()
        } catch (e: Exception) {
            Timber.e(e)
        }

    }
    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
