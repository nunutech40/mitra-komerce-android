package id.android.kmabsensi.presentation.admin


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.e
import iammert.com.expandablelib.Section
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.Dashboard
import id.android.kmabsensi.data.remote.response.Holiday
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserCoworkingSpace
import id.android.kmabsensi.databinding.FragmentHomeAdminBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.coworking.CheckinCoworkingActivity
import id.android.kmabsensi.presentation.coworking.ListCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.partner.grafik.GrafikPartnerActivity
import id.android.kmabsensi.presentation.role.RoleActivity
import id.android.kmabsensi.presentation.scanqr.ScanQrActivity
import id.android.kmabsensi.presentation.sdm.home.MenuModels
import id.android.kmabsensi.presentation.sdm.home.MenusAdapter
import id.android.kmabsensi.presentation.sdm.modekerja.ModeKerjaActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.dashboard_section_partner.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import kotlinx.android.synthetic.main.fragment_home_sdm.*
import kotlinx.android.synthetic.main.item_row_hari_libur.view.*
import kotlinx.android.synthetic.main.layout_wfh_mode.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.joda.time.LocalDate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class HomeAdminFragment : BaseFragmentRf<FragmentHomeAdminBinding>(
    FragmentHomeAdminBinding::inflate
) {

    private val vm: HomeViewModel by sharedViewModel()
    private val prefHelper: PreferencesHelper by inject()

    private val FORMAT = "(- %02d:%02d:%02d )"
    private lateinit var user: User

    private var countDownTimer: CountDownTimer? = null

    private lateinit var myDialog: MyDialog
    private val REQ_SCAN_QR = 123
    //for expandable layout
    val section = Section<String, Dashboard>()
    var isSectionAdded = false
    private var dashboard: Dashboard? = null

    private val cal = Calendar.getInstance()
    private val holidays = mutableListOf<Holiday>()
    private lateinit var menusAdapter: MenusAdapter

    private lateinit var dataUserCoworking : UserCoworkingSpace

    private val  isShopping by lazy {
        activity?.intent?.getBooleanExtra("isShopping", false)
    }

    private var sklUsername : SkeletonScreen? = null
    private var sklPoin : SkeletonScreen? = null
    private var sklTimer : SkeletonScreen? = null
    private var sklChair : SkeletonScreen? = null
    private var sklPhoto : SkeletonScreen? = null
    private var sklQtyPartner : SkeletonScreen? = null
    private var sklQtyPresence : SkeletonScreen? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = vm.getUserData()
        if (isShopping!!) showGroupMenu(1)
        vm.getJadwalShalat()

        setupGreetings()
        setupView()
        setupObserver()
        setupListener()
        setupListMenus()

    }

    private fun setupObserver(){
        vm.dashboardData.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {
                    showSkeleton()
                }
                is UiState.Success -> {
                    hideSkeleton()
                    dashboard = it.data.data
                    binding?.swipeRefresh?.isRefreshing = false
                    if (it.data.status) {
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
                    }

                    val isWFH = it.data.data.work_config.find { config -> config.key == ModeKerjaActivity.WORK_MODE }?.value == ModeKerjaActivity.WFH
                    val workModeScope = it.data.data.work_config.find { config -> config.key == ModeKerjaActivity.WFH_USER_SCOPE }?.value
                    prefHelper.saveBoolean(PreferencesHelper.WORK_MODE_IS_WFH, isWFH)
                    prefHelper.saveString(PreferencesHelper.WORK_MODE_SCOPE, workModeScope.toString())
                    setWorkModeUI(isWFH)

                    holidays.clear()
                    holidays.addAll(it.data.data.holidays)

                    if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                        setHolidayView()
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                    binding?.swipeRefresh?.isRefreshing = false
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.jadwalShalatData.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    sklTimer?.hide()
                    if (it.data.status.lowercase().equals(getString(R.string.ok), true)) {
                        val data = it.data.jadwal.data
                        val dzuhur = data?.dzuhur
                        val ashr = data?.ashar
                        setCountdown(dzuhur, ashr)
                    }
                }
                is UiState.Error -> {
                    sklTimer?.hide()
                }
            }
        })

        vm.coworkUserData.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    sklChair?.hide()
                    if (it.data.status) {
                        it.data.data[0].apply {
                            dataUserCoworking = this
                            binding?.apply {
                                if (status.equals("2")) {
                                    btnCheckinCoWorking.alpha = 0.3f
                                    btnCheckinCoWorking.isEnabled = false
                                    tvChair.text = "Tutup"
                                    tvCofee.gone()
                                }else {
                                    btnCheckinCoWorking.alpha = 1f
                                    btnCheckinCoWorking.isEnabled = true
                                    tvCofee.visible()
                                    tvChair.text = "$available_slot Kursi"
                                }
                                if (cowork_presence.isNotEmpty()){
                                    if (cowork_presence.last().checkout_date_time.isNullOrEmpty()){
                                        tvCoworkingPresence.text = "Check Out"
                                    }else{
                                        tvCoworkingPresence.text = "Check In"
                                    }
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    sklChair?.hide()
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

    private fun setupListMenus(){
        menusAdapter = MenusAdapter(requireActivity(), object : MenusAdapter.onAdapterListener{
            override fun onClick(data: MenuModels) {
                when(data.name){
                    "Data Kantor" ->{
                        activity?.startActivity<KelolaKantorActivity>()
                    }
                    "Co-Working" ->{
                        activity?.startActivity<ListCoworkingActivity>()
                    }
                    "Talent" ->{
        //            activity?.startActivity<KelolaDataSdmActivity>(IS_MANAGEMENT_KEY to false)
                        showGroupMenu(0)
                    }
                    "Partner" ->{
        //            activity?.startActivity<PartnerActivity>()
                        showGroupMenu(1)
                    }
                    "Role" ->{
                        activity?.startActivity<RoleActivity>()
                    }
                }
            }
        })
        binding?.rvMenu?.apply {
            adapter = menusAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }
        menusAdapter.setData(vm.menuHome(5))
    }

    private fun setupView(){
        myDialog = MyDialog(requireContext())
        vm.getDashboardInfo(user.id)
        vm.getCoworkUserData(user.id)
        binding?.apply {
            imgProfile?.loadCircleImage(
                user.photo_profile_url
                    ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
            )
            tvPosition.text = getRoleName(user.role_id).capitalizeWords()
        }
    }

    private fun setupListener(){
        binding?.apply {
            btnDetailPartner.setOnClickListener {
                activity?.startActivity<GrafikPartnerActivity>(DASHBOARD_DATA_KEY to dashboard)
            }

            btnQrCode.setOnClickListener {
                requireActivity().startActivityForResult<ScanQrActivity>(REQ_SCAN_QR)
            }

            btnCheckinCoWorking.setOnClickListener {
                validateCoworking()
            }

            swipeRefresh.setOnRefreshListener {
//                resetText()
//                layoutHoliday.invis()
//                layoutWfhMode.gone()
                vm.getDashboardInfo(user.id)
                vm.getCoworkUserData(user.id)
                vm.getJadwalShalat()
                setupGreetings()
            }

            btnDataPresence.setOnClickListener {
                val params = llDdDataPresence.layoutParams
                params.height = if (llDdDataPresence.height == 0) ViewGroup.LayoutParams.WRAP_CONTENT else 0
                imgArrowDd.rotation = if (llDdDataPresence.height == 0) 180f else 0f
                llDdDataPresence.layoutParams = params
            }
        }
    }

    private fun validateCoworking() {
        dataUserCoworking.apply {
            if (cowork_presence.isNotEmpty() && cowork_presence.last().checkout_date_time?.isEmpty()?:true){
                vm.checkOutCoworkingSpace(this.cowork_presence.last().id)
                vm.getCoworkUserData(user.id)
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

    private fun setWorkModeUI(isWFH: Boolean){
        if (isWFH){
            binding?.cvWfh?.visible()
        } else {
            binding?.cvWfh?.gone()
        }
    }

    /*
     * 0 - SDM
     * 1 - Partner
     * 2 - Evaluasi
     */
    private fun showGroupMenu(menu: Int) {
        binding?.swipeRefresh?.gone()
        containerHome.visible()
        val fragment = when (menu) {
            0 -> SdmMenuFragment()
            else -> PartnerMenuFragment()
        }
        childFragmentManager.beginTransaction().apply {
            replace(R.id.containerHome, fragment).commit()
        }
        (activity as HomeActivity).isOpenGroupMenu = true
    }

    fun hideGroupMenu() {
        containerHome.gone()
        binding?.swipeRefresh?.visible()
        childFragmentManager.popBackStack()
        (activity as HomeActivity).isOpenGroupMenu = false
    }


    private fun getSectionDashboard(dashboard: Dashboard): Section<String, Dashboard> {
        section.parent = dashboard.total_not_present.toString()
        section.children.add(dashboard)
        isSectionAdded = true
        return section
    }


    private fun setupGreetings() {
        val greeting = vm.setGreeting()
        binding?.apply {
            tvUsername.text = greeting
            if (user.role_id ==1){
                imgProfile.setImageResource(R.drawable.logo)
            }
        }
    }

    private fun setHolidayView(){
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            binding?.tvCountDown?.text = "Hari Minggu"
        } else {
            binding?.apply {
                tvCountDown.invis()
                tvStatusWaktu.invis()
                if (holidays.isNotEmpty()){
                    tvCountDown.text = holidays[0].eventName
                    val dateStart: LocalDate = LocalDate.parse(holidays[0].startDate)
                    val dateEnd: LocalDate = LocalDate.parse(holidays[0].endDate)

                    tvCountDown.text = if (holidays[0].startDate == holidays[0].endDate)
                        localDateFormatter(dateStart)
                    else
                        "${localDateFormatter(dateStart, "dd MMM yyyy")} s.d ${
                            localDateFormatter(
                                dateEnd,
                                "dd MMM yyyy"
                            )
                        }"
                    tvCountDown.text = "Hari Libur"
                }
            }
        }
    }

    private fun setCountdown(time_zuhur: String?, time_ashar: String?) {
        if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
        } else {
//            labelWaktu.text = "WAKTU"
            val (statusWaktu, differenceTime) = vm.getCountdownTime(
                time_zuhur,
                time_ashar
            )
            binding?.apply{
                tvStatusWaktu.visible()
                tvCountDown.visible()
                tvStatusWaktu.text = statusWaktu

                if (differenceTime != 0.toLong()) {
                    countDownTimer(differenceTime)
                } else {
                    tvCountDown.text = "-"
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        vm.getDashboardInfo(user.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeAdminFragment()
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
                        }catch (e: Exception){}
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_SCAN_QR && resultCode == Activity.RESULT_OK) {
            val redeemPoin = data?.getIntExtra(getString(R.string.qrdata), 0)
            redeemPoin?.let {
                vm.redeemPoin(user.id, it)
            }
        }
        if (requestCode == 112 && resultCode == Activity.RESULT_OK) {
            vm.getCoworkUserData(user.id)
        }
    }

    private fun showSkeleton() {
        if (sklUsername == null){
            binding?.apply {
                sklUsername = Skeleton.bind(tvUsername).load(R.layout.skeleton_item).show()
                sklPoin = Skeleton.bind(tvPoint).load(R.layout.skeleton_item).show()
                sklChair = Skeleton.bind(tvChair).load(R.layout.skeleton_item).show()
                sklTimer = Skeleton.bind(tvCountDown).load(R.layout.skeleton_item).show()
                sklPhoto = Skeleton.bind(imgProfile).load(R.layout.skeleton_hm_profile).show()
                sklQtyPartner = Skeleton.bind(tvPartnerTotal).load(R.layout.skeleton_hm_profile).show()
                sklPhoto = Skeleton.bind(imgProfile).load(R.layout.skeleton_hm_profile).show()
            }
        }else{
            sklUsername?.show()
            sklPoin?.show()
            sklChair?.show()
            sklTimer?.show()
            sklPhoto?.show()
            sklQtyPresence?.show()
            sklQtyPartner?.show()
        }
    }

    private fun hideSkeleton(){
        sklUsername?.hide()
        sklPoin?.hide()
        sklPhoto?.hide()
        sklQtyPresence?.hide()
        sklQtyPartner?.hide()
    }
}

