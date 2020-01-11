package id.android.kmabsensi.presentation.management


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.e
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.presentation.permission.PermissionActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_management.*
import kotlinx.android.synthetic.main.fragment_home_management.btnCheckIn
import kotlinx.android.synthetic.main.fragment_home_management.btnCheckOut
import kotlinx.android.synthetic.main.fragment_home_management.btnFormIzin
import kotlinx.android.synthetic.main.fragment_home_management.btnKelolaIzin
import kotlinx.android.synthetic.main.fragment_home_management.btnKelolaSdm
import kotlinx.android.synthetic.main.fragment_home_management.imgProfile
import kotlinx.android.synthetic.main.fragment_home_management.progressBar
import kotlinx.android.synthetic.main.fragment_home_management.swipeRefresh
import kotlinx.android.synthetic.main.fragment_home_management.txtHello
import kotlinx.android.synthetic.main.fragment_home_management.txtNotPresent
import kotlinx.android.synthetic.main.fragment_home_management.txtPresent
import kotlinx.android.synthetic.main.fragment_home_management.txtRoleName
import kotlinx.android.synthetic.main.fragment_home_management.txtTotalUser
import kotlinx.android.synthetic.main.layout_second.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
                is UiState.Loading -> progressBar.visible()
                is UiState.Success -> {
                    progressBar.gone()
                    txtPresent.text = it.data.data.total_present.toString()
                    txtTotalUser.text = "/ ${it.data.data.total_user}"
                    txtNotPresent.text = "${it.data.data.total_not_present}"
                }
                is UiState.Error -> {
                    progressBar.gone()
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
                            context?.startActivity<CekJangkauanActivity>(
                                DATA_OFFICE_KEY to it.data.office_assigned,
                                PRESENCE_ID_KEY to it.data.presence_id
                            )
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

        vm.coworkUserData.observe(viewLifecycleOwner, Observer {
            when(it){
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    it.data.data.forEach {
                        groupAdapter.add(CoworkingSpaceItem(it){ coworking, hasCheckin ->
                            activity?.toast("$hasCheckin")
                            if (hasCheckin){
                                vm.checkOutCoworkingSpace(coworking.cowork_presence.last().id)
                            } else {
                                if (coworking.available_slot > 0){
                                    if (coworking.cowork_presence.size < 2){
                                        vm.checkInCoworkingSpace(coworking.id)
                                    } else if (coworking.cowork_presence.size >= 2){
                                        createAlertError(activity!!, "Gagal", "Anda hanya bisa check in coworking space sebanyak 2 kali")
                                    }
                                }

                            }
                        })
                    }
                }
                is UiState.Error -> {

                }
            }

        })

        vm.checkInCoworkingSpace.observe(viewLifecycleOwner, Observer {
            when(it){
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status){
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

        vm.userdData.observe(viewLifecycleOwner, Observer {
            when(it){
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    txtKmPoin.text = it.data.data[0].kmpoin.toString()
                }
                is UiState.Error -> {

                }
            }
        })

        vm.getJadwalShalat()
        vm.getProfileUserData(user.id)
        getDashboardData()
        vm.getCoworkUserData(user.id)
        textView24.text = getTodayDateTimeDay()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandable.setOnExpandListener {
            if (it) {
                context?.toast("expanded")
            } else {
                context?.toast("collapse")
            }
        }
        expandable.parentLayout.setOnClickListener {
            if (expandable.isExpanded) {
                expandable.collapse()
            } else {
                expandable.expand()
            }
        }


        setupGreetings()

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            txtPresent.text = ""
            txtTotalUser.text = ""
            txtNotPresent.text = ""

            txtNextTime.text = ""
            txtCountdown.text = ""
            txtStatusWaktu.text = ""
            vm.getJadwalShalat()
            vm.getCoworkUserData(user.id)
            vm.getProfileUserData(user.id)
            getDashboardData()
            setupGreetings()
        }

        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )

        txtRoleName.text = getRoleName(user.role_id).capitalize()

        btnKelolaSdm.setOnClickListener {
            context?.startActivity<KelolaDataSdmActivity>(
                IS_MANAGEMENT_KEY to true,
                USER_ID_KEY to user.id
            )
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
            activity?.startActivity<ManajemenIzinActivity>(
                IS_MANAGEMENT_KEY to true,
                USER_ID_KEY to user.id
            )
        }

    }

    fun getDashboardData(){
        vm.getDashboardInfo(user.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeManagementFragment()
    }

    private fun setCountdown(time_zuhur: String, time_ashar: String) {

        val (statusWaktu, differenceTime, nextTime) = (activity as HomeActivity).getCountdownTime(time_zuhur, time_ashar)

        txtStatusWaktu.text = statusWaktu
        txtNextTime.text = nextTime

        if (differenceTime != 0.toLong()){
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

    fun initRv(){
        rvCoworkingSpace.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }


}
