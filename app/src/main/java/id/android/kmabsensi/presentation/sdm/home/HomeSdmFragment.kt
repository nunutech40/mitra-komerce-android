package id.android.kmabsensi.presentation.sdm.home


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Holiday
import id.android.kmabsensi.data.remote.response.PresenceCheckResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserCoworkingSpace
import id.android.kmabsensi.databinding.FragmentHomeSdmBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.checkin.CheckinActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingCartActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.permission.tambahizin.FormIzinActivity
import id.android.kmabsensi.presentation.scanqr.ScanQrActivity
import id.android.kmabsensi.presentation.sdm.modekerja.ModeKerjaActivity
import id.android.kmabsensi.presentation.sdm.productknowledge.ProductKnowledgeActivity
import id.android.kmabsensi.presentation.sdm.shift.SdmShiftActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeSdmFragment : BaseFragmentRf<FragmentHomeSdmBinding>(
    FragmentHomeSdmBinding :: inflate
) {
    private val vm: HomeViewModel by inject()

    private val REQ_FORM_IZIN = 212

    private lateinit var user: User
    private lateinit var myDialog: MyDialog

    private val REQ_SCAN_QR = 123

    var isCheckinButtonClicked = false

    private val FORMAT = "(- %02d:%02d:%02d )"
    private var countDownTimer: CountDownTimer? = null

    private val cal = Calendar.getInstance()
    private val holidays = mutableListOf<Holiday>()
    private lateinit var menusAdapter : MenusAdapter
    private lateinit var dataUserCoworking : UserCoworkingSpace

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
        setupGreetings()
        setupListMenu()
        setupListener()

//        btnLaporan.setOnClickListener {
//            if (user.position_name.toLowerCase() == "customer service") activity?.startActivity<SdmLaporanActivity>()
//            else activity?.startActivity<ListLaporanAdvertiserActivity>()}

    }

    private fun setupView() {
        user = vm.getUserData()
        myDialog = MyDialog(requireContext())
        vm.getJadwalShalat()
        vm.getDashboardInfo(user.id)
        vm.getCoworkUserData(user.id)
    }

    private fun setupObserver() {
        vm.dashboardData.observe(viewLifecycleOwner,{
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    if (it.data.status){
                        binding?.apply {
                            tvPoint.text = it.data.data.user_kmpoin.toString()
                        }
                        val workConfigs = it.data.data.work_config
                        val isWFH =
                            workConfigs.find { config -> config.key == ModeKerjaActivity.WORK_MODE }?.value == ModeKerjaActivity.WFH
                        val isSdmWFH =
                            isWFH && workConfigs.find { it.key == ModeKerjaActivity.WFH_USER_SCOPE }?.value?.contains(
                                "sdm",
                                true
                            ) ?: false
                        if (isSdmWFH) binding?.cvWfh?.visible() else binding?.cvWfh?.gone()

                        holidays.clear()
                        holidays.addAll(it.data.data.holidays)
                        if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                            setHolidayView()
                        }
                    }
                }
                is UiState.Error -> {
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.presenceCheckState.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status){
                        onPresenceCheck(it.data)
                    } else {
                        createAlertError(requireActivity(), getString(R.string.label_gagal), getString(R.string.message_error_occured))
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.checkoutState.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status){
                        createAlertSuccess(requireActivity(), it.data.message)
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
//                    if (!swipeRefresh.isRefreshing){
//                        showSkeletonMenu()
//                    }
                }
                is UiState.Success -> {
                    if (it.data.status.lowercase().equals(getString(R.string.ok), true)) {
                        val data = it.data.jadwal.data
                        val dzuhur = data?.dzuhur
                        val ashr = data?.ashar
                        setCountdown(dzuhur, ashr)
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
                                binding?.tvChair?.text = getString(R.string.tutup)
                                binding?.tvCofee?.gone()
                            }else binding?.tvCofee?.visible()
                            if (cowork_presence.isNotEmpty()){
                                if (cowork_presence.last().checkout_date_time.isNullOrEmpty()){
                                    binding?.tvCoworkingPresence?.text = getString(R.string.checkout)
                                }else{
                                    binding?.tvCoworkingPresence?.text = getString(R.string.checkin)
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

        vm.checkInCoworkingSpace.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        vm.getCoworkUserData(user.id)
                    } else {
                        createAlertError(requireActivity(), "Failed", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e(it.throwable)
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

    private fun setupListener() {
        binding?.apply {
            swipeRefresh.setOnRefreshListener {
                binding?.swipeRefresh?.isRefreshing = false
                user = vm.getUserData()
                vm.getJadwalShalat()
                vm.getCoworkUserData(user.id)
                vm.getDashboardInfo(user.id)
                setupGreetings()
            }
            btnQrCode.setOnClickListener {
                val intent = Intent(requireContext(), ScanQrActivity::class.java)
                startActivityForResult(intent, REQ_SCAN_QR)
            }

            btnCheckinCoWorking.setOnClickListener {

            }
        }
    }

    private fun setupListMenu() {
        menusAdapter = MenusAdapter(requireContext(), object : MenusAdapter.onAdapterListener{
            override fun onClick(data: MenuModels) {
                when(data.name){
                    "Datang" -> {
                        isCheckinButtonClicked = true
                        vm.presenceCheck(user.id)
                    }
                    "Pulang" -> {
                        isCheckinButtonClicked = false
                        vm.presenceCheck(user.id)
                    }
                    "Form Izin" ->{
                        startActivityForResult(Intent(requireContext(),
                        FormIzinActivity::class.java).putExtra(USER_KEY, user),
                        REQ_FORM_IZIN)
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
                    "Pengetahuan Produk" ->{
                        activity?.startActivity<ProductKnowledgeActivity>(NO_PARTNER_KEY to user.no_partners!![0].toInt())
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
        menusAdapter.setData(vm.menuHome(4))

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        if (user.position_name.lowercase() != "customer service" && user.position_name.lowercase() != "advertiser") btnLaporanLayout.invis()

    }

    private fun setHolidayView() {
        binding?.apply {
            tvCountDown.text = "Hari Libur"
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                tvCountDown.text = "Hari Minggu"
            }
            else {
                if (holidays.isNotEmpty()) {
                    tvCountDown.text = holidays[0].eventName
//                    txtNextTime.invis()
//                    layoutHoliday.visible()
//                    val dateStart: LocalDate = LocalDate.parse(holidays[0].startDate)
//                    val dateEnd: LocalDate = LocalDate.parse(holidays[0].endDate)
//                    txtHolidayDate.text = if (holidays[0].startDate == holidays[0].endDate)
//                        localDateFormatter(dateStart)
//                    else
//                        "${localDateFormatter(dateStart, "dd MMM yyyy")} s.d ${
//                            localDateFormatter(
//                                dateEnd,
//                                "dd MMM yyyy"
//                            )
//                        }"
                }
            }
            tvStatusWaktu.invis()
        }

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

    private fun onPresenceCheck(presenceCheck: PresenceCheckResponse) {
        var isEligibleToCheckInOutside = false
        var isEligibleToCheckoutOutside = false
        val isWFH = presenceCheck.work_config.find { config -> config.key == ModeKerjaActivity.WORK_MODE }?.value == ModeKerjaActivity.WFH
        val isShiftMode = presenceCheck.work_config.find { config -> config.key == ModeKerjaActivity.SHIFT_MODE }?.value == ModeKerjaActivity.MODE_ON
        val sdmConfig = presenceCheck.sdm_config

        if (isShiftMode){
            isEligibleToCheckInOutside = sdmConfig.shiftMode == SdmShiftActivity.SHIFT_SIANG
            isEligibleToCheckoutOutside = sdmConfig.shiftMode == SdmShiftActivity.SHIFT_PAGI
        }

        if (presenceCheck.checkdeIn) {
            if (isCheckinButtonClicked) {
                val dialogChecked = MaterialDialog(requireContext()).show {
                    cornerRadius(16f)
                    customView(
                        R.layout.dialog_already_checkin,
                        scrollable = false,
                        horizontalPadding = true,
                        noVerticalPadding = true
                    )
                }
                val customView = dialogChecked.getCustomView()
                val btn_oke = customView.findViewById<Button>(R.id.btn_oke)
                btn_oke.setOnClickListener {
                    dialogChecked.dismiss()
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
                    showDialogNotYetCheckout(requireContext())
                } else {
                    // office name contain rumah, can direct selfie
                    if (presenceCheck.office_assigned.office_name.lowercase()
                            .contains("rumah") || isWFH || isEligibleToCheckoutOutside
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
                        .contains("rumah") || isWFH || isEligibleToCheckInOutside
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
                        R.layout.dialog_retake_foto,
                        scrollable = false,
                        horizontalPadding = true,
                        noVerticalPadding = true
                    )
                }
                val customView = dialog.getCustomView()
                val btn_retake = customView.findViewById<Button>(R.id.btn_retake)
                val txtKeterangan = customView.findViewById<TextView>(R.id.tv_detection)
                btn_retake.text = getString(R.string.ok)
                txtKeterangan.text = getString(R.string.belum_checkin)
                btn_retake.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }


    private fun setupGreetings() {
        binding?.apply {
            val greeting = vm.setGreeting()
            imgProfile.loadCircleImage(
                user.photo_profile_url
                    ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
            )
            tvUsername.text = greeting
            tvPosition.text = user.position_name
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeSdmFragment()
    }

    private fun setCountdown(time_zuhur: String?, time_ashar: String?) {

        if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            setHolidayView()
        } else {

            val (statusWaktu, differenceTime) = vm.getCountdownTime(
                time_zuhur,
                time_ashar
            )
            binding?.apply {
                tvStatusWaktu.visible()
                tvCountDown.visible()
                tvStatusWaktu.text = statusWaktu

                if (differenceTime != 0.toLong()) {
                    countDownTimer(differenceTime)
                } else {
                    tvCountDown.text = "-"
                    tvStatusWaktu.text = ""
                }
            }
        }
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
            e(e)
        }

    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }


}
