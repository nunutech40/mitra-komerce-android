package id.android.kmabsensi.presentation.sdm.home


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.coworking.CheckinCoworkingActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.management.CoworkingSpaceItem
import id.android.kmabsensi.presentation.permission.PermissionActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_sdm.*
import kotlinx.android.synthetic.main.fragment_home_sdm.header_waktu
import kotlinx.android.synthetic.main.fragment_home_sdm.imgProfile
import kotlinx.android.synthetic.main.fragment_home_sdm.txtCountdown
import kotlinx.android.synthetic.main.fragment_home_sdm.txtHello
import kotlinx.android.synthetic.main.fragment_home_sdm.txtRoleName
import kotlinx.android.synthetic.main.fragment_home_sdm.txtStatusWaktu
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeSdmFragment : Fragment() {

    private val vm: HomeViewModel by inject()
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
        val view = inflater.inflate(R.layout.fragment_home_sdm, container, false)

        user = vm.getUserData()
        myDialog = MyDialog(context!!)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.presenceCheckState.observe(viewLifecycleOwner, Observer {
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
                            val now : Date = currentTime.time

                            val cal = Calendar.getInstance()
                            cal.set(Calendar.HOUR_OF_DAY,16)
                            cal.set(Calendar.MINUTE,30)
                            val jamPulang : Date = cal.time

                            if (now.before(jamPulang)){
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

        vm.checkoutState.observe(viewLifecycleOwner, Observer {
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
                                        val intent = Intent(context, CheckinCoworkingActivity::class.java).apply {
                                            putExtra("coworking", coworking)
                                        }
                                        startActivityForResult( intent, 112)
//                                        vm.checkInCoworkingSpace(coworking.id)
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
                    com.github.ajalt.timberkt.Timber.e(it.throwable)
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
        vm.getCoworkUserData(user.id)
        textView24.text = getTodayDateTimeDay()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 112 && resultCode == Activity.RESULT_OK){
            vm.getCoworkUserData(user.id)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        setupGreetings()

        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )
        txtRoleName.text = getRoleName(user.role_id).capitalize()

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

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            txtNextTime.text = ""
            txtCountdown.text = ""
            txtStatusWaktu.text = ""
            vm.getJadwalShalat()
            vm.getProfileUserData(user.id)
            setupGreetings()
        }

    }

    private fun setupGreetings() {
        val (greeting, header) = (activity as HomeActivity).setGreeting()
        txtHello.text = greeting
        header_waktu.setImageResource(header)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeSdmFragment()
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

    private fun countDownTimer(ms: Long) {
        try {
            countDownTimer = object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    d { millisUntilFinished.toString() }
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
