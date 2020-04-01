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
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.e
import iammert.com.expandablelib.ExpandableLayout
import iammert.com.expandablelib.Section
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Dashboard
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.coworking.ListCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.jabatan.ManajemenJabatanActivity
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.partner.PartnerActivity
import id.android.kmabsensi.presentation.partner.kategori.KategoriPartnerActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.*
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
                    progressBar.visible()
                }
                is UiState.Success -> {
                    progressBar.gone()
                    if (it.data.status){
                        txtPresent.text = it.data.data.total_present.toString()
                        txtTotalUser.text = " /${it.data.data.total_user}"

                        if (!isSectionAdded) expandableLayout.addSection(getSectionDashboard(it.data.data)) else {
                            expandableLayout.sections[0].parent = it.data.data.total_not_present.toString()
                            expandableLayout.sections[0].children.clear()
                            expandableLayout.sections[0].children.add(it.data.data)
                        }
                    }
               }
                is UiState.Error -> {
                    progressBar.gone()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.jadwalShalatData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    val data = it.data.jadwal.data
                    val dzuhur = data.dzuhur
                    val ashr = data.ashar
                    setCountdown(dzuhur, ashr)
                }
                is UiState.Error -> {
                }
            }
        })

        getPrayerTime()
        getDashboardData()

        textView24.text = getTodayDateTimeDay()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandableLayout.setRenderer(object: ExpandableLayout.Renderer<String, Dashboard> {
            override fun renderChild(
                view: View?,
                model: Dashboard?,
                parentPosition: Int,
                childPosition: Int
            ) {
                view?.findViewById<TextView>(R.id.txtJumlahCssr)?.setText(model?.total_cssr.toString())
                view?.findViewById<TextView>(R.id.txtJumlahCuti)?.setText(model?.total_holiday.toString())
                view?.findViewById<TextView>(R.id.txtJumlahSakit)?.setText(model?.total_sick.toString())
                view?.findViewById<TextView>(R.id.txtJumlahIzin)?.setText(model?.total_permission.toString())
                view?.findViewById<TextView>(R.id.txtJumlahBelumHadir)?.setText(model?.total_not_yet_present.toString())
                view?.findViewById<TextView>(R.id.txtJumlahGagalAbsen)?.setText(model?.total_failed_present.toString())
            }

            override fun renderParent(
                view: View?,
                model: String?,
                isExpanded: Boolean,
                parentPosition: Int
            ) {
                view?.findViewById<ImageView>(R.id.arrow)?.setBackgroundResource(if (isExpanded) R.drawable.ic_keyboard_arrow_up else R.drawable.ic_keyboard_arrow_down)
                view?.findViewById<TextView>(R.id.txtJumlahTidakHadir)?.setText(model)
            }
        })
        expandableLayout.setExpandListener { parentIndex: Int, parent: String, view: View? ->
            view?.findViewById<ImageView>(R.id.arrow)?.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
        }

        expandableLayout.setCollapseListener  { parentIndex: Int, parent: String, view: View? ->
            view?.findViewById<ImageView>(R.id.arrow)?.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
        }



        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )

        setupGreetings()

        txtRoleName.text = getRoleName(user.role_id).capitalize()

        btnKelolaKantor.setOnClickListener {
            activity?.startActivity<KelolaKantorActivity>()
        }

        btnKelolaSdm.setOnClickListener {
            activity?.startActivity<KelolaDataSdmActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnKelolaIzin.setOnClickListener {
            activity?.startActivity<ManajemenIzinActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnKelolaJabatan.setOnClickListener {
            activity?.startActivity<ManajemenJabatanActivity>()
        }

        btnKelolaCoworking.setOnClickListener {
            activity?.startActivity<ListCoworkingActivity>()
        }

        btnDataPartner.setOnClickListener {
            activity?.startActivity<PartnerActivity>()
        }

        btnPartnerCategory.setOnClickListener {
            activity?.startActivity<KategoriPartnerActivity>()
        }
        
        

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            txtPresent.text = ""
            txtTotalUser.text = ""
//            txtNotPresent.text = ""

            txtNextTime.text = ""
            txtCountdown.text = ""
            txtStatusWaktu.text = ""
            getPrayerTime()
            getDashboardData()
            setupGreetings()
        }

    }


    private fun getSectionDashboard(dashboard: Dashboard) : Section<String, Dashboard> {
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


}
