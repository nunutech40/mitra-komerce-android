package id.android.kmabsensi.presentation.home

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.hadilq.liveevent.LiveEvent
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.repository.*
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.presentation.sdm.home.MenuModels
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    private val preferencesHelper: PreferencesHelper,
    private val dashboardRepository: DashboardRepository,
    private val presenceRepository: PresenceRepository,
    private val jadwalShalatRepository: JadwalShalatRepository,
    private val coworkingSpaceRepository: CoworkingSpaceRepository,
    private val kmPoinRepository: KmPoinRepository,
    val authRepository: AuthRepository,
    val userRepository: UserRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {


    val jadwalShalatData = MutableLiveData<UiState<JadwalShalatResponse>>()
    val dashboardData = MutableLiveData<UiState<DashboardResponse>>()
    val userdData = MutableLiveData<UiState<UserResponse>>()
    val presenceCheckResponse = LiveEvent<UiState<PresenceCheckResponse>>()
    val checkoutResponse = LiveEvent<UiState<CheckinResponse>>()

    val presenceCheckState: LiveData<UiState<PresenceCheckResponse>> = presenceCheckResponse
    val checkoutState: LiveData<UiState<CheckinResponse>> = checkoutResponse

    val checkInCoworkingSpace = LiveEvent<UiState<BaseResponse>>()

    val coworkUserData = LiveEvent<UiState<UserCoworkDataResponse>>()

    val logoutState = MutableLiveData<UiState<BaseResponse>>()

    val redeemPoin = LiveEvent<UiState<BaseResponse>>()


    fun getDashboardInfo(userId: Int) {
        try {
            dashboardData.value = UiState.Loading()
            compositeDisposable.add(
                dashboardRepository.getDashboardInfo(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        dashboardData.value = UiState.Success(it)
                    }, {
                        dashboardData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            dashboardData.value = UiState.Error(e)
        }
    }

    fun presenceCheck(userId: Int) {
        try {
            presenceCheckResponse.value = UiState.Loading()
            compositeDisposable.add(
                presenceRepository.presenceCheck(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        presenceCheckResponse.value = UiState.Success(it)
                    }, {
                        presenceCheckResponse.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            presenceCheckResponse.value = UiState.Error(e)
        }
    }

    fun getJadwalShalat() {
        try {
            jadwalShalatData.value = UiState.Loading()
            compositeDisposable.add(
                jadwalShalatRepository.getJadwalShalat()
                    .with(schedulerProvider)
                    .subscribe({
                        jadwalShalatData.value = UiState.Success(it)
                    }, {
                        jadwalShalatData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            jadwalShalatData.value = UiState.Error(e)
        }
    }

    fun getCoworkUserData(userId: Int) {
        try {
            coworkUserData.value = UiState.Loading()
            compositeDisposable.add(
                coworkingSpaceRepository.getCoworkUserData(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        coworkUserData.value = UiState.Success(it)
                    }, {
                        coworkUserData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            coworkUserData.value = UiState.Error(e)
        }
    }

    fun checkInCoworkingSpace(coworkId: Int) {
        try {
            checkInCoworkingSpace.value = UiState.Loading()
            compositeDisposable.add(
                coworkingSpaceRepository.checkInCoworkingSpace(coworkId)
                    .with(schedulerProvider)
                    .subscribe({
                        checkInCoworkingSpace.value = UiState.Success(it)
                    }, {
                        checkInCoworkingSpace.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            checkInCoworkingSpace.value = UiState.Error(e)
        }
    }

    fun checkOutCoworkingSpace(coworkPresenceId: Int) {
        try {
            checkInCoworkingSpace.value = UiState.Loading()
            compositeDisposable.add(
                coworkingSpaceRepository.checkOutCoworkingSpace(coworkPresenceId)
                    .with(schedulerProvider)
                    .subscribe({
                        checkInCoworkingSpace.value = UiState.Success(it)
                    }, {
                        checkInCoworkingSpace.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            checkInCoworkingSpace.value = UiState.Error(e)
        }
    }

    fun logout() {
        try {
            logoutState.value = UiState.Loading()
            compositeDisposable.add(
                authRepository.logout(preferencesHelper.getString(PreferencesHelper.FCM_TOKEN))
                    .with(schedulerProvider)
                    .subscribe({
                        logoutState.value = UiState.Success(it)
                    }, {
                        logoutState.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            logoutState.value = UiState.Error(e)
        }
    }

    fun getProfileUserData(userId: Int) {
        try {
            userdData.value = UiState.Loading()
            compositeDisposable.add(
                userRepository.getProfileUser(userId)
                    .with(schedulerProvider)
                    .subscribe({
                        preferencesHelper.saveString(PreferencesHelper.PROFILE_KEY, Gson().toJson(it.data[0]))
                        userdData.value = UiState.Success(it)
                    }, {
                        userdData.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            userdData.value = UiState.Error(e)
        }
    }

    fun getUserData(): User {
        val userDara = preferencesHelper.getString(PreferencesHelper.PROFILE_KEY)
        return Gson().fromJson(
            userDara,
            User::class.java
        )
    }

    fun redeemPoin(userId: Int, totalRequestRedeemPoin: Int) {
        try {
            redeemPoin.value = UiState.Loading()
            compositeDisposable.add(
                kmPoinRepository.redeemPoin(userId, totalRequestRedeemPoin)
                    .with(schedulerProvider)
                    .subscribe({
                        redeemPoin.value = UiState.Success(it)
                    }, {
                        redeemPoin.value = UiState.Error(it)
                    })
            )
        } catch (e: Exception) {
            redeemPoin.value = UiState.Error(e)
        }
    }

    fun clearPref() {
        preferencesHelper.clear()
    }

    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }

    fun menuHome( role : Int): List<MenuModels>{
        val list : MutableList<MenuModels> = arrayListOf()
        val imgMenu = arrayListOf(
            R.drawable.ic_data_kantor_rf,
            R.drawable.ic_co_working_space_rf,
            R.drawable.ic_checkin_rf,
            R.drawable.ic_logout_rf,
            R.drawable.ic_data_talent_rf,
            R.drawable.ic_form_izin_rf, // 5
            R.drawable.ic_data_izin_rf,
            R.drawable.ic_invoice_rf,
            R.drawable.ic_invoice_report_rf,
            R.drawable.ic_document_text_rf,
            R.drawable.ic_time_square, // 10
            R.drawable.ic_partner_rf,
            R.drawable.ic_role_rf,
            R.drawable.ic_product_knowlage_rf,
            R.drawable.ic_orderku_rf,
            R.drawable.ic_talent_rf, // 15
            R.drawable.ic_orderku_rf
        )

        val nameMenu = arrayListOf(
            "Data Kantor",
            "Co-Working",
            "Datang",
            "Pulang",
            "Data Talent",
            "Form Izin", // 5
            "Data Izin",
            "Invoice",
            "Invoice Report",
            "Evaluasi Saya",
            "Shift", // 10
            "Partner",
            "Role",
            "Pengetahuan Produk",
            "Orderku",
            "Talent", // 15
            "Form Belanja"
        )

        /**
        role menu
        1 -> management
        2 -> management lead
        3 -> management growth
        4 -> talent
        5 -> admin
         */
        for (idx in 0..imgMenu.size-1){
            if (role == 1){
                if (idx == 0 || idx == 1 || idx == 10 || idx == 11 || idx == 12 || idx == 13 || idx == 14 || idx == 15 || idx == 16) continue
                list.add(MenuModels(nameMenu[idx], imgMenu[idx]))
            }else if (role == 2){
                if (idx == 0 || idx == 1 || idx == 11 || idx == 12 || idx == 13 || idx == 15 ) continue
                list.add(MenuModels(nameMenu[idx], imgMenu[idx]))
            } else if (role == 3){
                if (idx == 0 || idx == 1 || idx == 12 || idx == 13 || idx == 14 || idx == 15 || idx == 16) continue
                list.add(MenuModels(nameMenu[idx], imgMenu[idx]))
            }else if (role  == 4){
                if (idx == 0 || idx == 1 || idx == 4 || idx == 7 || idx == 8 || idx == 9 || idx == 10 || idx == 11 || idx == 12 || idx == 15 || idx == 16) continue
                list.add(MenuModels(nameMenu[idx], imgMenu[idx]))
            } else if (role == 5){
                if (idx == 2 || idx == 3 || idx == 4 || idx == 5 || idx == 6 || idx == 7 || idx == 8 || idx == 9 || idx == 10 || idx == 13 || idx == 14 || idx == 16) continue
                list.add(MenuModels(nameMenu[idx], imgMenu[idx]))
            }
        }
        return list
    }

    fun getCountdownTime(time_zuhur: String?, time_ashar: String?): Pair<String, Long> {

        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        var nextTime = ""

        val time_datang = "08:00:00"
        val time_istirahat = "12:00:00"
        val time_istirhat_selesai = "13:00:00"
        val time_pulang = "16:00:00"

        val datang = Calendar.getInstance()
        val istirahat = Calendar.getInstance()
        val selesai_istirahat = Calendar.getInstance()
        val ashar = Calendar.getInstance()
        val pulang = Calendar.getInstance()

        datang.set(Calendar.HOUR_OF_DAY, 8)
        istirahat.set(Calendar.HOUR_OF_DAY, 12)
        selesai_istirahat.set(Calendar.HOUR_OF_DAY, 13)
        pulang.set(Calendar.HOUR_OF_DAY, 17)

        time_ashar?.let {
            if (it.isNotEmpty()) {
                ashar.set(Calendar.HOUR_OF_DAY, time_ashar.split(":")[0].toInt())
                ashar.set(Calendar.MINUTE, time_ashar.split(":")[1].toInt())
            }
        }
        val now = Calendar.getInstance()

        val currentTime = simpleDateFormat.parse(simpleDateFormat.format(now.time))

        var statusWaktu = "-"
        var endTime: Date? = null

        when {
            now.before(datang) -> {
                statusWaktu = "Menuju Waktu Datang"
                nextTime = "08:00"
                endTime = simpleDateFormat.parse(time_datang)
            }
            now.before(istirahat) -> {
                statusWaktu = "Menuju Waktu Istirahat"
                nextTime = "12:00"
                endTime = simpleDateFormat.parse(time_istirahat)
            }
            now.before(selesai_istirahat) -> {
                statusWaktu = "Menuju Waktu \nSelesai Istirahat"
                nextTime = "13:00"
                endTime = simpleDateFormat.parse(time_istirhat_selesai)
            }
            now.before(ashar) -> {
                statusWaktu = "Menuju Waktu Ashar"
                nextTime = time_ashar ?: "-"
                endTime = simpleDateFormat.parse("$time_ashar:00")
            }
            now.before(pulang) -> {
                statusWaktu = "Menuju Waktu Pulang"
                nextTime = "16:00"
                endTime = simpleDateFormat.parse(time_pulang)
            }
            else -> statusWaktu = "Waktu Pulang"
        }

        var differenceTime: Long = 0

        if (endTime != null) {
            differenceTime = endTime.time - currentTime.time
        }

        return Pair(statusWaktu, differenceTime)
    }

    @SuppressLint("SimpleDateFormat")
    fun setGreeting(): String {
        val user = getUserData()
        var greeting = ""
        val morning = Calendar.getInstance()
        val noon = Calendar.getInstance()
        val afterNoon = Calendar.getInstance()
        val evening = Calendar.getInstance()

        morning.set(Calendar.HOUR_OF_DAY, 11)
        noon.set(Calendar.HOUR_OF_DAY, 15)
        afterNoon.set(Calendar.HOUR_OF_DAY, 18)
        evening.set(Calendar.HOUR_OF_DAY, 24)

        var name = ""

        if (!user.full_name.isNullOrEmpty()) {
//            name = user.full_name.split(" ")[0].lowercase().capitalizeWords()
            name = user.full_name.split(" ")[0]
        }

//        val now = Calendar.getInstance()
//        if (now.before(morning)) {
//            greeting = "Selamat Pagi, $name"
//        } else if (now.before(noon)) {
//            greeting = "Selamat Siang, $name"
//        } else if (now.before(afterNoon)) {
//            greeting = "Selamat Sore, $name"
//        } else if (now.before(evening)) {
//            greeting = "Selamat Malam, $name"
//        }
        greeting = "Hi, $name"

        return greeting
    }

    private var skeletonProfile : SkeletonScreen? = null

    fun showSkeletonProfile(view : AppCompatTextView){
        skeletonProfile = Skeleton.bind(view)
        .load(R.layout.skeleton_list_partner)
        .show()
    }
    fun hideSkeletonProfile(){
        skeletonProfile?.hide()
    }
}