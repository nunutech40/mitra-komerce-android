package id.android.kmabsensi.presentation.checkin

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.github.ajalt.timberkt.Timber.e
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.OfficeAssigned
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_checkin.*
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

    private lateinit var data: OfficeAssigned

    private val cal = Calendar.getInstance()

    var imagePath: String = ""

    private var actualImage: File? = null
    private var compressedImage: File? = null

    private val disposables = CompositeDisposable()

    private lateinit var myDialog: MyDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Check in"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDialog = MyDialog(this)

        data = intent.getParcelableExtra(DATA_OFFICE_KEY)

        val timeFormat = SimpleDateFormat("kk:mm")


        txtJam.text = timeFormat.format(cal.time)
        txtNamaKantor.text = data.office_name

        imgCheckin.setOnClickListener {
            ImagePicker.cameraOnly().start(this)
        }

        vm.checkInResponse.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    startActivity(intentFor<HomeActivity>("hasCheckin" to true).clearTask().newTask())
//                    toast(it.data.message)
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    e { it.throwable.message.toString() }
                }
            }
        })

        btnCheckIn.setOnClickListener {
            compressedImage?.let {
                vm.checkIn(it)
            } ?: run {
                createAlertError(this, "Gagal", "Ambil foto terlebih dahulu", 3000)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val image = ImagePicker.getFirstImageOrNull(data)

            imagePath = image.path

            actualImage = File(imagePath)

            actualImage?.let {
                compress(it)
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
                Environment.DIRECTORY_PICTURES).absolutePath
            )
            .compressToFileAsFlowable(file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                compressedImage = it

                Glide.with(this)
                    .load(compressedImage)
                    .into(imgCheckin)

            }) { e { it.message.toString() }})
    }
}
