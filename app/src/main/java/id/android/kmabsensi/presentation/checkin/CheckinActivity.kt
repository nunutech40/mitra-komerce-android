package id.android.kmabsensi.presentation.checkin

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.github.ajalt.timberkt.Timber.e
import com.google.gson.Gson
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.OfficeAssigned
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_checkin.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CheckinActivity : BaseActivity() {

    private val vm: CheckinViewModel by inject()
    private val pref: PreferencesHelper by inject()

    private lateinit var data: OfficeAssigned
    private var presenceId: Int = 0

    private val cal = Calendar.getInstance()

    var imagePath: String = ""

    private var actualImage: File? = null
    private var compressedImage: File? = null

    private val disposables = CompositeDisposable()

    private lateinit var myDialog: MyDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)

        presenceId = intent.getIntExtra(PRESENCE_ID_KEY, 0)

        myDialog = MyDialog(this)

        data = intent.getParcelableExtra(DATA_OFFICE_KEY)

        setupView()

        vm.checkInResponse.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    startActivity(
                        intentFor<HomeActivity>(
                            "hasCheckin" to true,
                            "message" to it.data.message
                        ).clearTask().newTask()
                    )
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        btnCheckIn.setOnClickListener {
            compressedImage?.let {
                if (presenceId == 0) vm.checkIn(it) else vm.checkOut(presenceId, it)
            } ?: run {
                createAlertError(this, "Gagal", "Ambil foto terlebih dahulu", 3000)
            }
        }

    }

    fun setupView() {

        txtTitle.text = if (presenceId == 0) "Check in" else "Check Out"
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val user =
            Gson().fromJson<User>(pref.getString(PreferencesHelper.PROFILE_KEY), User::class.java)

        val timeFormat = SimpleDateFormat("kk:mm")
        txtJam.text = timeFormat.format(cal.time)
        txtKantor.text = data.office_name

        txtName.text = user.full_name
        txtPartner.text = user.division_name

        if (presenceId != 0) {
            txtType.text = "Pulang"
            btnCheckIn.text = "Check out"
        }

        container.setOnClickListener {
            ImagePicker.cameraOnly().start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)

            imagePath = image.path

            actualImage = File(imagePath)

            actualImage?.let {
                compress(it)
                txtKetukLayar.gone()
                layoutBorderCamera.gone()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun compress(file: File) {
        disposables.add(Compressor(this)
            .setQuality(75)
            .setCompressFormat(Bitmap.CompressFormat.WEBP)
            .setDestinationDirectoryPath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).absolutePath
            )
            .compressToFileAsFlowable(file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                compressedImage = it

                Glide.with(this)
                    .load(compressedImage)
                    .into(picture)

            }) { e { it.message.toString() } })
    }
}
