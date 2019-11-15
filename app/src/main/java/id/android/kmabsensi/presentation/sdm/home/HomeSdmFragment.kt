package id.android.kmabsensi.presentation.sdm.home


import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e

import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.checkin.CekJangkauanActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.permission.PermissionActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_home_admin.*
import kotlinx.android.synthetic.main.fragment_home_sdm.*
import kotlinx.android.synthetic.main.fragment_home_sdm.header_waktu
import kotlinx.android.synthetic.main.fragment_home_sdm.imgProfile
import kotlinx.android.synthetic.main.fragment_home_sdm.txtCountdown
import kotlinx.android.synthetic.main.fragment_home_sdm.txtHello
import kotlinx.android.synthetic.main.fragment_home_sdm.txtRoleName
import kotlinx.android.synthetic.main.fragment_home_sdm.txtStatusWaktu
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class HomeSdmFragment : Fragment() {

    private val vm: HomeViewModel by inject()

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

        vm.getJadwalShalat()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupGreetings()

        imgProfile.loadCircleImage(
            user.photo_profile_url
                ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg"
        )
        txtHello.text = "Hello, ${user.full_name}"
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

        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val simpleDateFormat2 = SimpleDateFormat("HH:mm:ss")

        val time_istirahat = "12:00"
        val time_istirhat_selesai = "13:00"
        val time_pulang = "16:30"

        val istirahat = Calendar.getInstance()
        val dzuhur = Calendar.getInstance()
        val selesai_istirahat = Calendar.getInstance()
        val ashar = Calendar.getInstance()
        val pulang = Calendar.getInstance()

        istirahat.set(Calendar.HOUR_OF_DAY, 12)
        selesai_istirahat.set(Calendar.HOUR_OF_DAY, 13)
        pulang.set(Calendar.HOUR_OF_DAY, 17)

        dzuhur.set(Calendar.HOUR_OF_DAY, time_zuhur.split(":")[0].toInt())
        dzuhur.set(Calendar.MINUTE, time_zuhur.split(":")[1].toInt())

        ashar.set(Calendar.HOUR_OF_DAY, time_ashar.split(":")[1].toInt())
        ashar.set(Calendar.MINUTE, time_ashar.split(":")[1].toInt())

        val now = Calendar.getInstance()


        val currentTime = simpleDateFormat.parse(simpleDateFormat2.format(now.time))

        com.github.ajalt.timberkt.d { simpleDateFormat2.format(now.time) }

        var statusWaktu = ""
        var endTime: Date? = null

        if (now.before(dzuhur)) {
            statusWaktu = "Menuju Waktu Dzuhur"
            endTime = simpleDateFormat.parse(time_zuhur)
        } else if (now.before(istirahat)) {
            statusWaktu = "Menuju Waktu Istirahat"
            endTime = simpleDateFormat.parse(time_istirahat)
        } else if (now.before(selesai_istirahat)) {
            statusWaktu = "Menuju Waktu Selesai Istirahat"
            endTime = simpleDateFormat.parse(time_istirhat_selesai)
        } else if (now.before(time_ashar)) {
            statusWaktu = "Menuju Waktu Ashar"
            endTime = simpleDateFormat.parse(time_ashar)
        } else if (now.before(pulang)) {
            statusWaktu = "Menuju Waktu Pulang"
            endTime = simpleDateFormat.parse(time_pulang)
        } else {
            statusWaktu = "Waktu Pulang"
        }

        txtStatusWaktu.text = statusWaktu
        if (endTime != null) {
            val difference: Long = endTime.time - currentTime.time
            countDownTimer(difference)
        } else {
            txtCountdown.text = "-"
        }
    }

    fun countDownTimer(ms: Long) {
        try {
            countDownTimer = object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    d { millisUntilFinished.toString() }
                    if (txtCountdown != null) {
                        txtCountdown.text = String.format(
                            FORMAT,
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                            ),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                            )
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
