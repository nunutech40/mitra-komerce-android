package id.android.kmabsensi.presentation.admin


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.e
import com.github.ajalt.timberkt.d
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.jabatan.ManajemenJabatanActivity
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class HomeAdminFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private val FORMAT = "(- %02d:%02d:%02d )"
    private lateinit var user: User

    private var countDownTimer: CountDownTimer? = null

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
                    txtPresent.text = it.data.data.total_present.toString()
                    txtTotalUser.text = " /${it.data.data.total_user}"
                    txtNotPresent.text = "${it.data.data.total_not_present}"
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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getDashboardInfo(user.id)

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

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            txtPresent.text = ""
            txtTotalUser.text = ""
            txtNotPresent.text = ""
            vm.getDashboardInfo(user.id)

        }

        getPrayerTime()

        val now = Calendar.getInstance()
        val a = now.get(Calendar.AM_PM)
        if (a == Calendar.AM) txtAmPm.text = "AM" else txtAmPm.text = "PM"


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

        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val simpleDateFormat2 = SimpleDateFormat("HH:mm:ss")

        val time_istirahat = "12:00"
        val time_istirhat_selesai = "13:00"
        val time_pulang = "16:30:22"

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

        d { simpleDateFormat2.format(now.time) }

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
            endTime = simpleDateFormat2.parse(time_pulang)
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

    companion object {
        @JvmStatic
        fun newInstance() = HomeAdminFragment()
    }

    fun countDownTimer(ms: Long) {
        try {
            countDownTimer = object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    d { millisUntilFinished.toString() }
                    if (txtCountdown != null) {

                        val hour = (millisUntilFinished / 1000) / (60 * 60) % 24
                        val minute = (millisUntilFinished / 1000) / 60
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
