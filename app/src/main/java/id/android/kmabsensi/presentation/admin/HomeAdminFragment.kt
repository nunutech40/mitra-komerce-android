package id.android.kmabsensi.presentation.admin


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import id.android.kmabsensi.data.remote.response.Dashboard
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.coworking.ListCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.invoice.InvoiceActivity
import id.android.kmabsensi.presentation.invoice.report.InvoiceReportActivity
import id.android.kmabsensi.presentation.jabatan.ManajemenJabatanActivity
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.partner.PartnerActivity
import id.android.kmabsensi.presentation.partner.grafik.GrafikPartnerActivity
import id.android.kmabsensi.presentation.partner.kategori.KategoriPartnerActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.dashboard_section_partner.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * A simple [Fragment] subclass.
 */
class HomeAdminFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
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
                    val data = it.data.jadwal.data
                    val dzuhur = data.dzuhur
                    val ashr = data.ashar
                    setCountdown(dzuhur, ashr)
                }
                is UiState.Error -> {
                    skeletonNextTime?.hide()
                    skeletonStatusWaktu?.hide()
                    skeletonContdown?.hide()
                }
            }
        })

        getPrayerTime()
        getDashboardData()

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
                view?.findViewById<TextView>(R.id.txtJumlahCssr)?.text = model?.total_cssr.toString()
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

//        imgProfile.loadCircleImage(
//            user.photo_profile_url
//                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
//        )

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


        sectionPartner.setOnClickListener {
            activity?.startActivity<GrafikPartnerActivity>(DASHBOARD_DATA_KEY to dashboard)
        }



        swipeRefresh.setOnRefreshListener {
            txtPresent.text = ""
            txtTotalUser.text = ""

            txtNextTime.text = ""
            txtCountdown.text = ""
            txtStatusWaktu.text = ""
            getPrayerTime()
            getDashboardData()
            setupGreetings()
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

    private fun setCountdown(time_zuhur: String, time_ashar: String) {

        val (statusWaktu, differenceTime, nextTime) = (activity as HomeActivity).getCountdownTime(
            time_zuhur,
            time_ashar
        )

        txtStatusWaktu.text = statusWaktu
        txtNextTime.text = nextTime

        if (differenceTime != 0.toLong()) {
            countDownTimer(differenceTime)
        } else {
            txtCountdown.text = "-"
        }

    }

    fun getDashboardData() {
        vm.getDashboardInfo(user.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeAdminFragment()
    }

    fun countDownTimer(ms: Long) {
        try {
            countDownTimer = object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    if (txtCountdown != null) {

                        val hour = (millisUntilFinished / 1000) / (60 * 60) % 24
                        val minute = (millisUntilFinished / 1000) / 60 % 60
                        val second = (millisUntilFinished / 1000) % 60

                        txtCountdown.text = String.format(
                            FORMAT,
                            hour,
                            minute,
                            second
                        )
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

    fun hideSkeletonMenu() {
        skeletonLabelMenu?.hide()
        skeletonMenu1?.hide()
        skeletonMenu2?.hide()
        skeletonMenu3?.hide()
    }
}
