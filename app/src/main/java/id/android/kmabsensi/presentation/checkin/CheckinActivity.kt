package id.android.kmabsensi.presentation.checkin

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.github.ajalt.timberkt.Timber.e
import com.google.gson.Gson
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.OfficeAssigned
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.ActivityCheckinBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.camera.CameraActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.presentation.splashabsen.SplashAbsenActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_checkin.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CheckinActivity : BaseActivity() {
    private val vm: CheckinViewModel by inject()
    private val pref: PreferencesHelper by inject()

    private lateinit var data: OfficeAssigned
    private var presenceId: Int = 0

    private val cal = Calendar.getInstance()

    private var compressedImage: File? = null

    private lateinit var myDialog: MyDialog

    var onTimeLevel = 0

    private var presenceTime = ""

    private val binding by lazy { ActivityCheckinBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        presenceId = intent.getIntExtra(PRESENCE_ID_KEY, 0)

        myDialog = MyDialog(this)

        data = intent.getParcelableExtra(DATA_OFFICE_KEY) ?: OfficeAssigned()

        setupView()
        setupObserver()
        setupListener()

    }

    private fun setupListener() {
        binding.btnSelesai.setOnClickListener {
            compressedImage?.let {
                val timeFormat = SimpleDateFormat("kk:mm")
                presenceTime = timeFormat.format(cal.time).toString()
                if (presenceId == 0) vm.checkIn(it, getCheckinOntimeLevel() ) else vm.checkOut(presenceId, it)
            } ?: run {
                createAlertError(this, "Gagal", "Ambil foto terlebih dahulu", 3000)
            }
        }

        binding.btnAmbilUlang.setOnClickListener {
            startActivityForResult<CameraActivity>(125)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupObserver() {
        vm.checkInResponse.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    startActivity(
                        intentFor<SplashAbsenActivity>(
                            "isCheckin" to (presenceId == 0),
                            "isCheckout" to (presenceId != 0),
                            "message" to it.data.message,
                            "ontimeLevel" to onTimeLevel,
                            "presenceTime" to presenceTime
                        ).clearTask().newTask()
                    )

                    if (!PreferencesHelper(this).getBoolean(IS_SAVE_PHOTO)) {
                        compressedImage?.delete()
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

    }

    private fun setupView() {

        val user =
            Gson().fromJson<User>(pref.getString(PreferencesHelper.PROFILE_KEY), User::class.java)

        txtKantor.text = data.office_name
        txtName.text = user.full_name
        txtPositionName.text = user.position_name

//        Auto move to camera
        startActivityForResult<CameraActivity>(125)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 125 && resultCode == Activity.RESULT_OK){
            val file = data?.getSerializableExtra("image")
            compressedImage = file as File
            file?.let {
                Glide.with(this)
                    .load(it)
                    .signature(ObjectKey(System.currentTimeMillis().toString()))
                    .into(picture)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getCheckinOntimeLevel(): Int{
        val currentTime = Calendar.getInstance()
        val now : Date = currentTime.time

        // for check ontime level 1
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY,8)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 1)
        val jam8 : Date = cal.time

        // for check ontime level 2
        val cal2 = Calendar.getInstance()
        cal2.set(Calendar.HOUR_OF_DAY,8)
        cal2.set(Calendar.MINUTE, 0)
        cal2.set(Calendar.SECOND, 0)
        val jamTepatWaktuForBetween : Date = cal2.time

        // for check ontime level 2
        val cal3 = Calendar.getInstance()
        cal3.set(Calendar.HOUR_OF_DAY,8)
        cal3.set(Calendar.MINUTE, 10)
        cal3.set(Calendar.SECOND, 1)
        val jam8TelatForBetween : Date = cal3.time

        // for check ontime level 3
        val cal4 = Calendar.getInstance()
        cal4.set(Calendar.HOUR_OF_DAY,8)
        cal4.set(Calendar.MINUTE, 10)
        cal4.set(Calendar.SECOND, 0)
        val jam8Telat : Date = cal4.time

        if (now.before(jam8)){
            onTimeLevel = 1
            return 1
        } else if (now.after(jamTepatWaktuForBetween) && now.before(jam8TelatForBetween)) {
            onTimeLevel = 2
            return 2
        } else if (now.after(jam8Telat)){
            onTimeLevel = 3
            return 3
        }
        return 0
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivity::class.java).clearTask().newTask())
        finishAffinity()
    }

}
