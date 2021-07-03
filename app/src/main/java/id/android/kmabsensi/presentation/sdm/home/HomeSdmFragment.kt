package id.android.kmabsensi.presentation.sdm.home


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber.e
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Holiday
import id.android.kmabsensi.data.remote.response.PresenceCheckResponse
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.checkin.CheckinActivity
import id.android.kmabsensi.presentation.checkin.ReportAbsensiActivity
import id.android.kmabsensi.presentation.coworking.CheckinCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.management.CoworkingSpaceItem
import id.android.kmabsensi.presentation.permission.PermissionActivity
import id.android.kmabsensi.presentation.permission.tambahizin.FormIzinActivity
import id.android.kmabsensi.presentation.scanqr.ScanQrActivity
import id.android.kmabsensi.presentation.sdm.laporan.cs.SdmLaporanActivity
import id.android.kmabsensi.presentation.sdm.laporan.advertiser.ListLaporanAdvertiserActivity
import id.android.kmabsensi.presentation.sdm.modekerja.ModeKerjaActivity
import id.android.kmabsensi.presentation.sdm.productknowledge.ProductKnowledgeActivity
import id.android.kmabsensi.presentation.sdm.shift.SdmShiftActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_management.*
import kotlinx.android.synthetic.main.fragment_home_sdm.*
import kotlinx.android.synthetic.main.fragment_home_sdm.btnCheckIn
import kotlinx.android.synthetic.main.fragment_home_sdm.btnCheckOut
import kotlinx.android.synthetic.main.fragment_home_sdm.btnFormIzin
import kotlinx.android.synthetic.main.fragment_home_sdm.btnGagalAbsen
import kotlinx.android.synthetic.main.fragment_home_sdm.btnScanQr
import kotlinx.android.synthetic.main.fragment_home_sdm.header_waktu
import kotlinx.android.synthetic.main.fragment_home_sdm.imgProfile
import kotlinx.android.synthetic.main.fragment_home_sdm.labelWaktu
import kotlinx.android.synthetic.main.fragment_home_sdm.layoutHoliday
import kotlinx.android.synthetic.main.fragment_home_sdm.layoutKmPoint
import kotlinx.android.synthetic.main.fragment_home_sdm.layoutMenu1
import kotlinx.android.synthetic.main.fragment_home_sdm.rvCoworkingSpace
import kotlinx.android.synthetic.main.fragment_home_sdm.swipeRefresh
import kotlinx.android.synthetic.main.fragment_home_sdm.textCoworkingSpace
import kotlinx.android.synthetic.main.fragment_home_sdm.textView24
import kotlinx.android.synthetic.main.fragment_home_sdm.txtCountdown
import kotlinx.android.synthetic.main.fragment_home_sdm.txtHello
import kotlinx.android.synthetic.main.fragment_home_sdm.txtHolidayDate
import kotlinx.android.synthetic.main.fragment_home_sdm.txtHolidayName
import kotlinx.android.synthetic.main.fragment_home_sdm.txtKmPoin
import kotlinx.android.synthetic.main.fragment_home_sdm.txtNextTime
import kotlinx.android.synthetic.main.fragment_home_sdm.txtRoleName
import kotlinx.android.synthetic.main.fragment_home_sdm.txtStatusWaktu
import kotlinx.android.synthetic.main.layout_wfh_mode.*
import org.jetbrains.anko.startActivity
import org.joda.time.LocalDate
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeSdmFragment : Fragment() {

    private val vm: HomeViewModel by inject()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val REQ_FORM_IZIN = 212

    private lateinit var user: User

    private lateinit var myDialog: MyDialog

    private val REQ_SCAN_QR = 123

    var isCheckinButtonClicked = false

    private val FORMAT = "(- %02d:%02d:%02d )"
    private var countDownTimer: CountDownTimer? = null

    private var skeletonNextTime: SkeletonScreen? = null
    private var skeletonContdown: SkeletonScreen? = null
    private var skeletonStatusWaktu: SkeletonScreen? = null
    private var skeletonKmPoin: SkeletonScreen? = null
    private var skeletonDate: SkeletonScreen? = null
    private var skeletonMenu1: SkeletonScreen? = null
    private var skeletonCoworking: SkeletonScreen? = null
    private var skeletonLabelCoworking: SkeletonScreen? = null

    private val cal = Calendar.getInstance()
    private val holidays = mutableListOf<Holiday>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_sdm, container, false)

        user = vm.getUserData()
        myDialog = MyDialog(requireContext())

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.dashboardData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonKmPoin?.hide()
                    skeletonKmPoin = Skeleton.bind(layoutKmPoint)
                        .load(R.layout.skeleton_home_box_content)
                        .show()
                }
                is UiState.Success -> {
                    skeletonKmPoin?.hide()
                    if (it.data.status){
                        txtKmPoin.text = it.data.data.user_kmpoin.toString()
                        val workConfigs = it.data.data.work_config
                        val isWFH =
                            workConfigs.find { config -> config.key == ModeKerjaActivity.WORK_MODE }?.value == ModeKerjaActivity.WFH
                        val isSdmWFH =
                            isWFH && workConfigs.find { it.key == ModeKerjaActivity.WFH_USER_SCOPE }?.value?.contains(
                                "sdm",
                                true
                            ) ?: false
                        if (isSdmWFH) layoutWfhMode.visible() else layoutWfhMode.gone()

                        holidays.clear()
                        holidays.addAll(it.data.data.holidays)
                        if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                            setHolidayView()
                        }
                    }
                }
                is UiState.Error -> {
                    skeletonKmPoin?.hide()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.presenceCheckState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status){
                        onPresenceCheck(it.data)
                    } else {
                        createAlertError(activity!!, getString(R.string.label_gagal), getString(R.string.message_error_occured))
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.checkoutState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status){
                        createAlertSuccess(activity, it.data.message)
                    } else {
                        createAlertError(activity!!, getString(R.string.label_gagal), getString(R.string.message_error_occured))
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        vm.jadwalShalatData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    hideSkeletonTime()
//                    hideSkeletonMenu()
                    showSkeletonTime()
//                    if (!swipeRefresh.isRefreshing){
//                        showSkeletonMenu()
//                    }
                }
                is UiState.Success -> {
                    hideSkeletonTime()
//                    hideSkeletonMenu()
                    if (it.data.status.toLowerCase().equals(getString(R.string.ok), true)) {
                        val data = it.data.jadwal.data
                        val dzuhur = data?.dzuhur
                        val ashr = data?.ashar
                        setCountdown(dzuhur, ashr)
                    }
                }
                is UiState.Error -> {
                    hideSkeletonTime()
//                    hideSkeletonMenu()
                }
            }
        })

        vm.coworkUserData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonLabelCoworking = Skeleton.bind(textCoworkingSpace)
                        .load(R.layout.skeleton_item)
                        .show()

                    skeletonCoworking = Skeleton.bind(rvCoworkingSpace)
                        .adapter(groupAdapter)
                        .load(R.layout.skeleton_list_coworking_space)
                        .show()
                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    skeletonLabelCoworking?.hide()
                    skeletonCoworking?.hide()
                    if (it.data.status) {
                        it.data.data.forEach {
                            groupAdapter.add(CoworkingSpaceItem(it) { coworking, hasCheckin ->
                                if (hasCheckin) {
                                    vm.checkOutCoworkingSpace(coworking.cowork_presence.last().id)
                                } else {
                                    if (coworking.available_slot > 0) {
                                        if (coworking.cowork_presence.size < 2) {
                                            val intent = Intent(
                                                requireContext(),
                                                CheckinCoworkingActivity::class.java
                                            ).apply {
                                                putExtra("coworking", coworking)
                                            }
                                            startActivityForResult(intent, 112)
//                                        vm.checkInCoworkingSpace(coworking.id)
                                        } else if (coworking.cowork_presence.size >= 2) {
                                            createAlertError(
                                                activity!!,
                                                "Gagal",
                                                "Kamu hanya bisa check in coworking space sebanyak 2 kali"
                                            )
                                        }
                                    }

                                }
                            })
                        }
                    }

                }
                is UiState.Error -> {
                    skeletonLabelCoworking?.hide()
                    skeletonCoworking?.hide()
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
                    if (it.data.status) {
                        vm.getCoworkUserData(user.id)
                    } else {
                        createAlertError(activity!!, "Failed", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    com.github.ajalt.timberkt.Timber.e(it.throwable)
                }
            }
        })

        vm.redeemPoin.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()

                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        showDialogSuccess(activity!!, message = it.data.message)
                        vm.getDashboardInfo(user.id)
                    } else {
                        createAlertError(activity!!, "Failed", it.data.message)
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })


        vm.getJadwalShalat()
        vm.getDashboardInfo(user.id)
        vm.getCoworkUserData(user.id)
        textView24.text = getTodayDateTimeDay()

        if (user.position_name.toLowerCase() != "customer service" && user.position_name.toLowerCase() != "advertiser") btnLaporanLayout.invis()

    }

    private fun setHolidayView() {
        skeletonNextTime?.hide()
        skeletonStatusWaktu?.hide()
        skeletonContdown?.hide()
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            txtNextTime.text = "Hari Minggu"
        } else {
            if (holidays.isNotEmpty()) {
                txtNextTime.invis()
                layoutHoliday.visible()
                txtHolidayName.text = holidays[0].eventName
                val dateStart: LocalDate = LocalDate.parse(holidays[0].startDate)
                val dateEnd: LocalDate = LocalDate.parse(holidays[0].endDate)

                txtHolidayDate.text = if (holidays[0].startDate == holidays[0].endDate)
                    localDateFormatter(dateStart)
                else
                    "${localDateFormatter(dateStart, "dd MMM yyyy")} s.d ${
                        localDateFormatter(
                            dateEnd,
                            "dd MMM yyyy"
                        )
                    }"
            }
        }
        labelWaktu.text = "Hari Libur"
        txtCountdown.invis()
        txtStatusWaktu.invis()

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()

        setupGreetings()


        btnCheckIn.setOnClickListener {
            isCheckinButtonClicked = true
            vm.presenceCheck(user.id)
        }

        btnCheckOut.setOnClickListener {
            isCheckinButtonClicked = false
            vm.presenceCheck(user.id)
        }

        btnFormIzin.setOnClickListener {
            startActivityForResult(Intent(requireContext(),
                FormIzinActivity::class.java).putExtra(USER_KEY, user),
                REQ_FORM_IZIN)
        }

        btnGagalAbsen.setOnClickListener {
            activity?.startActivity<ReportAbsensiActivity>()
        }

        btnProductKnowledge.setOnClickListener {
            activity?.startActivity<ProductKnowledgeActivity>(NO_PARTNER_KEY to user.no_partners!![0].toInt())
        }

        btnLaporan.setOnClickListener {
            if (user.position_name.toLowerCase() == "customer service"){
                activity?.startActivity<SdmLaporanActivity>()
            } else {
                activity?.startActivity<ListLaporanAdvertiserActivity>()
            }

        }


        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            txtNextTime.text = ""
            txtCountdown.text = ""
            txtStatusWaktu.text = ""
            layoutHoliday.invis()
            user = vm.getUserData()
            vm.getJadwalShalat()
            vm.getCoworkUserData(user.id)
            vm.getDashboardInfo(user.id)
            setupGreetings()
        }

        btnScanQr.setOnClickListener {

            val intent = Intent(requireContext(), ScanQrActivity::class.java)
            startActivityForResult(intent, REQ_SCAN_QR)
        }

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
                    (activity as HomeActivity).showDialogNotYetCheckout()
                } else {
                    // office name contain rumah, can direct selfie
                    if (presenceCheck.office_assigned.office_name.toLowerCase()
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
                if (presenceCheck.office_assigned.office_name.toLowerCase()
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
        val (greeting, header) = (activity as HomeActivity).setGreeting()
        txtHello.text = greeting
        header_waktu.setImageResource(header)
        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )

        txtRoleName.text = user.position_name
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeSdmFragment()
    }

    private fun setCountdown(time_zuhur: String?, time_ashar: String?) {

        if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//            setHolidayView()
        } else {
            labelWaktu.text = "WAKTU"

            val (statusWaktu, differenceTime, nextTime) = (activity as HomeActivity).getCountdownTime(
                time_zuhur,
                time_ashar
            )

            txtStatusWaktu.visible()
            txtNextTime.visible()
            txtCountdown.visible()

            txtStatusWaktu.text = statusWaktu
            txtNextTime.text = nextTime

            if (differenceTime != 0.toLong()) {
                countDownTimer(differenceTime)
            } else {
                txtCountdown.text = "-"
            }
        }
    }

    private fun countDownTimer(ms: Long) {
        try {
            countDownTimer = object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    if (txtCountdown != null) {
                        val hour = (millisUntilFinished / 1000) / (60 * 60) % 24
                        val minute = (millisUntilFinished / 1000) / 60 % 60
                        val second = (millisUntilFinished / 1000) % 60
                        try {
                            txtCountdown.text = String.format(
                                FORMAT,
                                hour,
                                minute,
                                second
                            )
                        }catch (e: Exception){ }

                    }
                }

                override fun onFinish() {
                    txtCountdown.text = "Waktu Tiba!"
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

    fun initRv() {
        rvCoworkingSpace.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }
    }

    private fun showSkeletonMenu() {
        skeletonDate = Skeleton.bind(textView24)
            .load(R.layout.skeleton_item)
            .show()

        skeletonMenu1 = Skeleton.bind(layoutMenu1)
            .load(R.layout.skeleton_home_menu)
            .show()

    }

    fun hideSkeletonMenu() {
        skeletonDate?.hide()
        skeletonMenu1?.hide()
    }

    private fun showSkeletonTime() {
        skeletonNextTime = Skeleton.bind(txtNextTime)
            .load(R.layout.skeleton_item_big)
            .show()
        skeletonContdown = Skeleton.bind(txtCountdown)
            .load(R.layout.skeleton_item)
            .show()
        skeletonStatusWaktu = Skeleton.bind(txtStatusWaktu)
            .load(R.layout.skeleton_item)
            .show()
    }

    private fun hideSkeletonTime() {
        skeletonNextTime?.hide()
        skeletonContdown?.hide()
        skeletonStatusWaktu?.hide()
    }


}
