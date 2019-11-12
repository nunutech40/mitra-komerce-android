package id.android.kmabsensi.presentation.admin


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.jabatan.ManajemenJabatanActivity
import id.android.kmabsensi.presentation.kantor.KelolaKantorActivity
import id.android.kmabsensi.presentation.permission.manajemenizin.ManajemenIzinActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class HomeAdminFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private val FORMAT = "%02d:%02d:%02d"
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
                    txtTotalUser.text = "/ ${it.data.data.total_user}"
                    txtNotPresent.text = "${it.data.data.total_not_present} orang belum hadir"
                }
                is UiState.Error -> {
                    progressBar.gone()
                    e { it.throwable.message.toString() }
                }
            }
        })

        vm.jadwalShalatData.observe(viewLifecycleOwner, Observer {
            when(it){
                is UiState.Loading -> {}
                is UiState.Success -> {
                    val data = it.data.jadwal.data
                    val dzuhur = data.dzuhur
                    val ashr = data.ashar
                }
                is UiState.Error -> {}
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
        setGreeting()
//        countDownTimer(7200000)
        txtRoleName.text = getRoleName(user.role_id).capitalize()

        btnKelolaDataKantor.setOnClickListener {
            activity?.startActivity<KelolaKantorActivity>()
        }

        btnKelolaDataSdm.setOnClickListener {
            activity?.startActivity<KelolaDataSdmActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnManajemenIzin.setOnClickListener {
            activity?.startActivity<ManajemenIzinActivity>(IS_MANAGEMENT_KEY to false)
        }

        btnManajemenJabatan.setOnClickListener {
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
        vm.getJadwalShalat()

    }

    private fun getPrayerTime(){
        val today = SimpleDate(GregorianCalendar())

        val location = Location(-6.1744, 106.8294, 7.0, 0)
        val azan = Azan(location, Method.EGYPT_SURVEY)
        val prayerTimes = azan.getPrayerTimes(today)
        val imsaak = azan.getImsaak(today)
        activity?.toast("asd")
        d { "get prayer time" }
        d { prayerTimes.assr().toString() }
        Log.i("asasasasas", "${today.day} ${today.month} ${today.year}")
//        Log.i("asasasasas", imsaak.toString())
        Log.i("asasasasas", prayerTimes.fajr().toString())
        Log.i("asasasasas", prayerTimes.shuruq().toString())
        Log.i("asasasasas", prayerTimes.thuhr().toString())
        Log.i("asasasasas", prayerTimes.assr().toString())
        Log.i("asasasasas", prayerTimes.maghrib().toString())
        Log.i("asasasasas", prayerTimes.ishaa().toString())

    }


    private fun countDownTimer(ms: Long) {
        try {
            object : CountDownTimer(ms, 1000) {

                override fun onTick(millisUntilFinished: Long) {

                    txtHello.text = String.format(
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

                override fun onFinish() {
                    txtHello.text = "Waktu Tiba!"
                }

            }.start()
        } catch (e: Exception) {

        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun setGreeting() {
        var greeting = ""
        val morning = Calendar.getInstance()
        val noon = Calendar.getInstance()
        val afterNoon = Calendar.getInstance()
        val evening = Calendar.getInstance()

        morning.set(Calendar.HOUR_OF_DAY, 11)
        noon.set(Calendar.HOUR_OF_DAY, 15)
        afterNoon.set(Calendar.HOUR_OF_DAY, 18)
        evening.set(Calendar.HOUR_OF_DAY, 24)

        val now = Calendar.getInstance()
        if (now.before(morning)) {
            greeting = "Selamat Pagi, ${user.full_name}"
        } else if (now.before(noon)) {
            greeting = "Selamat Siang, ${user.full_name}"
        } else if (now.before(afterNoon)) {
            greeting = "Selamat Sore, ${user.full_name}"
        } else if (now.before(evening)) {
            greeting = "Selamat Malam, ${user.full_name}"
        }

        txtHello.text = greeting

    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeAdminFragment()
    }


}
