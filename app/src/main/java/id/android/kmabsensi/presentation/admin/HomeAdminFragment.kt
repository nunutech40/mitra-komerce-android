package id.android.kmabsensi.presentation.admin


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.e
import com.github.ajalt.timberkt.d
import iammert.com.expandablelib.ExpandableLayout
import iammert.com.expandablelib.Section
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.Dashboard
import id.android.kmabsensi.data.remote.response.Holiday
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.coworking.ListCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.partner.grafik.GrafikPartnerActivity
import id.android.kmabsensi.presentation.role.RoleActivity
import id.android.kmabsensi.presentation.sdm.modekerja.ModeKerjaActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.dashboard_section_partner.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import kotlinx.android.synthetic.main.fragment_home_admin.header_waktu
import kotlinx.android.synthetic.main.fragment_home_admin.labelWaktu
import kotlinx.android.synthetic.main.fragment_home_admin.layoutHoliday
import kotlinx.android.synthetic.main.fragment_home_admin.layoutMenu1
import kotlinx.android.synthetic.main.fragment_home_admin.layoutMenu2
import kotlinx.android.synthetic.main.fragment_home_admin.swipeRefresh
import kotlinx.android.synthetic.main.fragment_home_admin.textView24
import kotlinx.android.synthetic.main.fragment_home_admin.txtCountdown
import kotlinx.android.synthetic.main.fragment_home_admin.txtHello
import kotlinx.android.synthetic.main.fragment_home_admin.txtHolidayDate
import kotlinx.android.synthetic.main.fragment_home_admin.txtHolidayName
import kotlinx.android.synthetic.main.fragment_home_admin.txtNextTime
import kotlinx.android.synthetic.main.fragment_home_admin.txtRoleName
import kotlinx.android.synthetic.main.fragment_home_admin.txtStatusWaktu
import kotlinx.android.synthetic.main.fragment_home_sdm.*
import kotlinx.android.synthetic.main.item_row_hari_libur.view.*
import kotlinx.android.synthetic.main.layout_wfh_mode.*
import org.jetbrains.anko.startActivity
import org.joda.time.LocalDate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class HomeAdminFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private val prefHelper: PreferencesHelper by inject()

    private val FORMAT = "(- %02d:%02d:%02d )"
    private lateinit var user: User

    private var countDownTimer: CountDownTimer? = null

    //for expandable layout
    val section = Section<String, Dashboard>()
    var isSectionAdded = false
    private var dashboard: Dashboard? = null

    private var skeletonNextTime: SkeletonScreen? = null
    private var skeletonContdown: SkeletonScreen? = null
    private var skeletonStatusWaktu: SkeletonScreen? = null
    private var skeletonDate: SkeletonScreen? = null
    private var skeletonDataHadir: SkeletonScreen? = null
    private var skeletonDataBelumHadir: SkeletonScreen? = null
    private var skeletonPartnerLabel: SkeletonScreen? = null
    private var skeletonPartner: SkeletonScreen? = null
    private var skeletonLabelMenu: SkeletonScreen? = null
    private var skeletonMenu1: SkeletonScreen? = null
    private var skeletonMenu2: SkeletonScreen? = null
    private var skeletonMenu3: SkeletonScreen? = null

    private val cal = Calendar.getInstance()
    private val holidays = mutableListOf<Holiday>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home_admin, container, false)

        user = vm.getUserData()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.dashboardData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    hideSkeletonDashboardContent()
                    hideSkeletonMenu()

                    showSkeletonDashboardContent()
                    if (!swipeRefresh.isRefreshing) {
                        showSkeletonMenu()
                    }
                }
                is UiState.Success -> {
                    dashboard = it.data.data
                    hideSkeletonDashboardContent()
                    hideSkeletonMenu()
                    swipeRefresh.isRefreshing = false
                    if (it.data.status) {
                        txtPresent.text = it.data.data.total_present.toString()
                        txtTotalUser.text = " /${it.data.data.total_user}"
                        textTotalPartner.text = it.data.data.total_partner.toString()

                        if (!isSectionAdded){
                            expandableLayout.addSection(getSectionDashboard(it.data.data))
                        } else {
                            expandableLayout.sections[0].parent = it.data.data.total_not_present.toString()
                            expandableLayout.sections[0].children.clear()
                            expandableLayout.sections[0].children.add(it.data.data)
                            expandableLayout.notifyParentChanged(0)
                        }
                    }

                    val isWFH = it.data.data.work_config.find { config -> config.key == ModeKerjaActivity.WORK_MODE }?.value == ModeKerjaActivity.WFH
//                    val workModeScope = it.data.data.work_config.find { config -> config.key == ModeKerjaActivity.WFH_USER_SCOPE }?.value
//
//                    prefHelper.saveBoolean(PreferencesHelper.WORK_MODE_IS_WFH, isWFH)
//                    prefHelper.saveString(PreferencesHelper.WORK_MODE_SCOPE, workModeScope.toString())
                    setWorkModeUI(isWFH)

                    holidays.clear()
                    holidays.addAll(it.data.data.holidays)

                    if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                        setHolidayView()
                    }
                }
                is UiState.Error -> {
                    hideSkeletonDashboardContent()
                    hideSkeletonMenu()
                    swipeRefresh.isRefreshing = false
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.jadwalShalatData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonNextTime?.hide()
                    skeletonStatusWaktu?.hide()
                    skeletonContdown?.hide()

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
                is UiState.Success -> {
                    skeletonNextTime?.hide()
                    skeletonStatusWaktu?.hide()
                    skeletonContdown?.hide()
                    if (it.data.status.toLowerCase().equals(getString(R.string.ok), true)) {
                        val data = it.data.jadwal.data
                        val dzuhur = data?.dzuhur
                        val ashr = data?.ashar
                        setCountdown(dzuhur, ashr)
                    }
                }
                is UiState.Error -> {
                    skeletonNextTime?.hide()
                    skeletonStatusWaktu?.hide()
                    skeletonContdown?.hide()
                }
            }
        })

        getPrayerTime()
//        getDashboardData()

        textView24.text = getTodayDateTimeDay()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandableLayout.setRenderer(object : ExpandableLayout.Renderer<String, Dashboard> {
            override fun renderChild(
                view: View?,
                model: Dashboard?,
                parentPosition: Int,
                childPosition: Int
            ) {
//                view?.findViewById<TextView>(R.id.txtJumlahCssr)?.text = model?.total_cssr.toString()
                view?.findViewById<TextView>(R.id.txtJumlahCuti)?.text = model?.total_holiday.toString()
                view?.findViewById<TextView>(R.id.txtJumlahSakit)?.text = model?.total_sick.toString()
                view?.findViewById<TextView>(R.id.txtJumlahIzin)?.text = model?.total_permission.toString()
                view?.findViewById<TextView>(R.id.txtJumlahBelumHadir)?.text = model?.total_not_yet_present.toString()
                view?.findViewById<TextView>(R.id.txtJumlahGagalAbsen)?.text = model?.total_failed_present.toString()
            }

            override fun renderParent(
                view: View?,
                model: String?,
                isExpanded: Boolean,
                parentPosition: Int
            ) {
                d { "render parent" }
                view?.findViewById<ImageView>(R.id.arrow)
                    ?.setBackgroundResource(if (isExpanded) R.drawable.ic_keyboard_arrow_up else R.drawable.ic_keyboard_arrow_down)
                view?.findViewById<TextView>(R.id.txtJumlahTidakHadir)?.text = model
            }
        })
        expandableLayout.setExpandListener { parentIndex: Int, parent: String, view: View? ->
            view?.findViewById<ImageView>(R.id.arrow)
                ?.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
        }

        expandableLayout.setCollapseListener { parentIndex: Int, parent: String, view: View? ->
            view?.findViewById<ImageView>(R.id.arrow)
                ?.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
        }


        setupGreetings()

        txtRoleName.text = getRoleName(user.role_id).capitalize()

        btnKelolaKantor.setOnClickListener {
            activity?.startActivity<KelolaKantorActivity>()
        }

        btnKelolaSdm.setOnClickListener {
//            activity?.startActivity<KelolaDataSdmActivity>(IS_MANAGEMENT_KEY to false)
            showGroupMenu(0)
        }



        btnKelolaCoworking.setOnClickListener {
            activity?.startActivity<ListCoworkingActivity>()
        }

        btnDataPartner.setOnClickListener {
//            activity?.startActivity<PartnerActivity>()
            showGroupMenu(1)
        }

        btnRole.setOnClickListener {
            activity?.startActivity<RoleActivity>()
        }


        sectionPartner.setOnClickListener {
            activity?.startActivity<GrafikPartnerActivity>(DASHBOARD_DATA_KEY to dashboard)
        }



        swipeRefresh.setOnRefreshListener {
            txtPresent.text = ""
            txtTotalUser.text = ""
            txtNextTime.text = ""
            txtCountdown.text = ""
            txtStatusWaktu.text = ""
            layoutHoliday.invis()
            layoutWfhMode.gone()
            getPrayerTime()
            getDashboardData()
            setupGreetings()
        }

    }

    private fun setWorkModeUI(isWFH: Boolean){
        if (isWFH){
            dataHadir.gone()
            expandableLayout.gone()
            layoutWfhMode.visible()
        } else {
            dataHadir.visible()
            expandableLayout.visible()
            layoutWfhMode.gone()
        }
    }

    /*
     * 0 - SDM
     * 1 - Partner
     * 2 - Evaluasi
     */
    private fun showGroupMenu(menu: Int) {
        swipeRefresh.gone()
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
        swipeRefresh.visible()
        childFragmentManager.popBackStack()
        (activity as HomeActivity).isOpenGroupMenu = false
    }


    private fun getSectionDashboard(dashboard: Dashboard): Section<String, Dashboard> {
        section.parent = dashboard.total_not_present.toString()
        section.children.add(dashboard)
        isSectionAdded = true
        return section
    }

    private fun getPrayerTime() {
        vm.getJadwalShalat()
    }

    private fun setupGreetings() {
        val (greeting, header) = (activity as HomeActivity).setGreeting()
        txtHello.text = greeting
        header_waktu.setImageResource(header)
    }

    private fun setHolidayView(){
        skeletonNextTime?.hide()
        skeletonStatusWaktu?.hide()
        skeletonContdown?.hide()
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            txtNextTime.text = "Hari Minggu"
        } else {
            if (holidays.isNotEmpty()){
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
        dataHadir.gone()
        expandableLayout.gone()
    }

    private fun setCountdown(time_zuhur: String?, time_ashar: String?) {
        if (holidays.isNotEmpty() || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
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

    private fun getDashboardData() {
        vm.getDashboardInfo(user.id)
    }

    override fun onResume() {
        super.onResume()
        getDashboardData()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeAdminFragment()
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
                        }catch (e: Exception){}
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

    private fun showSkeletonDashboardContent() {
        skeletonDate = Skeleton.bind(textView24)
            .load(R.layout.skeleton_item)
            .show()

        skeletonDataHadir = Skeleton.bind(dataHadir)
            .load(R.layout.skeleton_home_data_hadir)
            .show()

        skeletonDataBelumHadir = Skeleton.bind(expandableLayout)
            .load(R.layout.skeleton_home_box_content)
            .show()

        skeletonPartnerLabel = Skeleton.bind(labelPartner)
            .load(R.layout.skeleton_item)
            .show()

        skeletonPartner = Skeleton.bind(sectionPartner)
            .load(R.layout.skeleton_home_box_content)
            .show()
    }

    private fun hideSkeletonDashboardContent() {
        skeletonDate?.hide()
        skeletonDataHadir?.hide()
        skeletonDataBelumHadir?.hide()
        skeletonPartnerLabel?.hide()
        skeletonPartner?.hide()
    }

    private fun showSkeletonMenu() {
        skeletonLabelMenu = Skeleton.bind(textView26)
            .load(R.layout.skeleton_item)
            .show()

        skeletonMenu1 = Skeleton.bind(layoutMenu1)
            .load(R.layout.skeleton_home_menu)
            .show()

        skeletonMenu2 = Skeleton.bind(layoutMenu2)
            .load(R.layout.skeleton_home_menu)
            .show()

        skeletonMenu3 = Skeleton.bind(layoutMenu3)
            .load(R.layout.skeleton_home_menu)
            .show()
    }

    private fun hideSkeletonMenu() {
        skeletonLabelMenu?.hide()
        skeletonMenu1?.hide()
        skeletonMenu2?.hide()
        skeletonMenu3?.hide()
    }
}
