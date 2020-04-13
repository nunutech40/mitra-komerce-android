package id.android.kmabsensi.presentation.management


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.e
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import iammert.com.expandablelib.ExpandableLayout
import iammert.com.expandablelib.Section
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Dashboard
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.checkin.ReportAbsensiActivity
import id.android.kmabsensi.presentation.coworking.CheckinCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.permission.PermissionActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_management.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeManagementFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    private lateinit var user: User

    private lateinit var myDialog: MyDialog

    var isCheckin = false

    private val FORMAT = "(- %02d:%02d:%02d )"
    private var countDownTimer: CountDownTimer? = null

    //for expandable layout
    val section = Section<String, Dashboard>()
    var isSectionAdded = false

    private var skeletonNextTime: SkeletonScreen? = null
    private var skeletonContdown: SkeletonScreen? = null
    private var skeletonStatusWaktu: SkeletonScreen? = null
    private var skeletonKmPoin: SkeletonScreen? = null
    private var skeletonDate: SkeletonScreen? = null
    private var skeletonDataHadir: SkeletonScreen? = null
    private var skeletonDataBelumHadir: SkeletonScreen? = null
    private var skeletonPartnerLabel: SkeletonScreen? = null
    private var skeletonPartner: SkeletonScreen? = null
    private var skeletonLabelMenu: SkeletonScreen? = null
    private var skeletonMenu1: SkeletonScreen? = null
    private var skeletonMenu2: SkeletonScreen? = null
    private var skeletonCoworking: SkeletonScreen? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_management, container, false)

        user = vm.getUserData()
        myDialog = MyDialog(context!!)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRv()

        vm.dashboardData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> showSkeletonDashboardContent()
                is UiState.Success -> {
                    hideSkeletonDashboardContent()
                    txtKmPoin.text = it.data.data.user_kmpoin.toString()
                    txtPresent.text = it.data.data.total_present.toString()
                    txtTotalUser.text = "/ ${it.data.data.total_user}"

                    if (!isSectionAdded) expandableLayout.addSection(getSectionDashboard(it.data.data)) else {
                        expandableLayout.sections[0].parent = it.data.data.total_not_present.toString()
                        expandableLayout.sections[0].children.clear()
                        expandableLayout.sections[0].children.add(it.data.data)
                    }

                }
                is UiState.Error -> {
                    hideSkeletonDashboardContent()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.presenceCheckResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.checkdeIn) {
                        if (isCheckin) {
                            MaterialDialog(context!!).show {
                                cornerRadius(16f)
                                title(text = "Check-In")
                                message(text = "Anda sudah check-in hari ini")
                                positiveButton(text = "OK") {
                                    it.dismiss()
                                }
                            }
                        } else {
                            //checkout
                            // cek jam pulang terlebih dahulu
                            val currentTime = Calendar.getInstance()
                            val now: Date = currentTime.time

                            val cal = Calendar.getInstance()
                            cal.set(Calendar.HOUR_OF_DAY, 16)
                            cal.set(Calendar.MINUTE, 30)
                            val jamPulang: Date = cal.time

                            if (now.before(jamPulang)) {
                                (activity as HomeActivity).showDialogNotYetCheckout()
                            } else {
                                context?.startActivity<CekJangkauanActivity>(
                                    DATA_OFFICE_KEY to it.data.office_assigned,
                                    PRESENCE_ID_KEY to it.data.presence_id
                                )
                            }
                        }

                    } else {
                        if (isCheckin) {
                            //checkin
                            context?.startActivity<CekJangkauanActivity>(DATA_OFFICE_KEY to it.data.office_assigned)
                        } else {
                            val dialog = MaterialDialog(context!!).show {
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
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.checkoutResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    createAlertSuccess(activity, it.data.message)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                }
            }
        })

        vm.jadwalShalatData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonNextTime = Skeleton.bind(txtNextTime)
                        .load(R.layout.skeleton_item_big)
                        .show()
                    skeletonContdown = Skeleton.bind(txtCountdown)
                        .load(R.layout.skeleton_item)
                        .show()
                    skeletonStatusWaktu = Skeleton.bind(txtStatusWaktu)
                        .load(R.layout.skeleton_item)
                        .show()
                    if (!swipeRefresh.isRefreshing){
                        showSkeletonMenu()
                    }
                }
                is UiState.Success -> {
                    skeletonNextTime?.hide()
                    skeletonStatusWaktu?.hide()
                    skeletonContdown?.hide()
                    hideSkeletonMenu()
                    val data = it.data.jadwal.data
                    val dzuhur = data.dzuhur
                    val ashr = data.ashar
                    setCountdown(dzuhur, ashr)
                }
                is UiState.Error -> {
                    skeletonNextTime?.hide()
                    skeletonStatusWaktu?.hide()
                    skeletonContdown?.hide()
                    hideSkeletonMenu()
                }
            }
        })

        vm.coworkUserData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                    skeletonCoworking = Skeleton.bind(rvCoworkingSpace)
                        .adapter(groupAdapter)
                        .load(R.layout.skeleton_list_coworking_space)
                        .show()
                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    skeletonCoworking?.hide()
                    it.data.data.forEach {
                        groupAdapter.add(CoworkingSpaceItem(it) { coworking, hasCheckin ->
                            if (hasCheckin) {
                                vm.checkOutCoworkingSpace(coworking.cowork_presence.last().id)
                            } else {
                                if (coworking.available_slot > 0) {
                                    if (coworking.cowork_presence.size < 2) {
                                        val intent = Intent(context, CheckinCoworkingActivity::class.java).apply {
                                            putExtra("coworking", coworking)
                                        }
                                        startActivityForResult( intent, 112)
//                                        vm.checkInCoworkingSpace(coworking.id)
                                    } else if (coworking.cowork_presence.size >= 2) {
                                        createAlertError(
                                            activity!!,
                                            "Gagal",
                                            "Anda hanya bisa check in coworking space sebanyak 2 kali"
                                        )
                                    }
                                }

                            }
                        })
                    }

                }
                is UiState.Error -> {
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
                    Timber.e(it.throwable)
                }
            }
        })


        vm.getJadwalShalat()
        vm.getCoworkUserData(user.id)
        getDashboardData()
        textView24.text = getTodayDateTimeDay()

    }

    private fun getSectionDashboard(dashboard: Dashboard): Section<String, Dashboard> {
        section.parent = dashboard.total_not_present.toString()
        section.children.add(dashboard)
        isSectionAdded = true
        return section
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 112 && resultCode == Activity.RESULT_OK){
            vm.getCoworkUserData(user.id)
        }
        super.onActivityResult(requestCode, resultCode, data)
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
                view?.findViewById<TextView>(R.id.txtJumlahCssr)
                    ?.setText(model?.total_cssr.toString())
                view?.findViewById<TextView>(R.id.txtJumlahCuti)
                    ?.setText(model?.total_holiday.toString())
                view?.findViewById<TextView>(R.id.txtJumlahSakit)
                    ?.setText(model?.total_sick.toString())
                view?.findViewById<TextView>(R.id.txtJumlahIzin)
                    ?.setText(model?.total_permission.toString())
                view?.findViewById<TextView>(R.id.txtJumlahBelumHadir)
                    ?.setText(model?.total_not_yet_present.toString())
                view?.findViewById<TextView>(R.id.txtJumlahGagalAbsen)
                    ?.setText(model?.total_failed_present.toString())
            }

            override fun renderParent(
                view: View?,
                model: String?,
                isExpanded: Boolean,
                parentPosition: Int
            ) {
                view?.findViewById<ImageView>(R.id.arrow)
                    ?.setBackgroundResource(if (isExpanded) R.drawable.ic_keyboard_arrow_up else R.drawable.ic_keyboard_arrow_down)
                view?.findViewById<TextView>(R.id.txtJumlahTidakHadir)?.setText(model)
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

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            txtPresent.text = ""
            txtTotalUser.text = ""

            txtNextTime.text = ""
            txtCountdown.text = ""
            txtStatusWaktu.text = ""
            vm.getJadwalShalat()
            vm.getCoworkUserData(user.id)
            getDashboardData()
            setupGreetings()
        }

        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )

        txtRoleName.text = getRoleName(user.role_id).capitalize()

        btnKelolaSdm.setOnClickListener {
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

        btnCheckIn.setOnClickListener {
            isCheckin = true
            vm.presenceCheck(user.id)
        }

        btnCheckOut.setOnClickListener {
            isCheckin = false
            vm.presenceCheck(user.id)
        }

        btnFormIzin.setOnClickListener {
            context?.startActivity<PermissionActivity>(USER_KEY to user)
        }

        btnKelolaIzin.setOnClickListener {

            if (user.position_id == 3 || user.position_id == 4 || user.position_id == 5) {
                activity?.startActivity<ManajemenIzinActivity>(IS_MANAGEMENT_KEY to false)
            } else {
                activity?.startActivity<ManajemenIzinActivity>(
                    IS_MANAGEMENT_KEY to true,
                    USER_ID_KEY to user.id
                )
            }


        }

        btnGagalAbsen.setOnClickListener {
           activity?.startActivity<ReportAbsensiActivity>()
        }

    }

    fun getDashboardData() {
        vm.getDashboardInfo(user.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeManagementFragment()
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

    private fun setupGreetings() {
        val (greeting, header) = (activity as HomeActivity).setGreeting()
        txtHello.text = greeting
        header_waktu.setImageResource(header)
    }

    private fun countDownTimer(ms: Long) {
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

    fun initRv() {
        rvCoworkingSpace.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun showSkeletonDashboardContent(){
        skeletonKmPoin = Skeleton.bind(layoutKmPoint)
            .load(R.layout.skeleton_home_box_content)
            .show()

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

    private fun hideSkeletonDashboardContent(){
        skeletonDate?.hide()
        skeletonDataHadir?.hide()
        skeletonDataBelumHadir?.hide()
        skeletonPartnerLabel?.hide()
        skeletonPartner?.hide()
        skeletonKmPoin?.hide()
    }

    private fun showSkeletonMenu(){
        skeletonLabelMenu = Skeleton.bind(textView26)
            .load(R.layout.skeleton_item)
            .show()

        skeletonMenu1 = Skeleton.bind(layoutMenu1)
            .load(R.layout.skeleton_home_menu)
            .show()

        skeletonMenu2 = Skeleton.bind(layoutMenu2)
            .load(R.layout.skeleton_home_menu)
            .show()
    }

    private fun hideSkeletonMenu(){
        skeletonLabelMenu?.hide()
        skeletonMenu1?.hide()
        skeletonMenu2?.hide()
    }
}
