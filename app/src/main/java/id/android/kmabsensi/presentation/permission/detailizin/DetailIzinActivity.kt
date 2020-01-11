package id.android.kmabsensi.presentation.permission.detailizin

import android.app.Activity
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.d
import com.stfalcon.imageviewer.StfalconImageViewer
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Permission
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.permission.PermissionViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_detail_izin.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat

class DetailIzinActivity : BaseActivity() {

    private val vm: PermissionViewModel by inject()

    var permission: Permission? = null
    var isFromManajemenIzin = false

    var isApprove = false
    var isStatusChanged = false

    //permission type = 1 -> izin, 2 -> sakit, 3 -> cuti

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_izin)

//        setSupportActionBar(toolbar)
//        supportActionBar?.title = "Pengajuan Izin"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setToolbarTitle("Pengajuan Izin")

        permission = intent.getParcelableExtra(PERMISSION_DATA_KEY)
        isFromManajemenIzin = intent.getBooleanExtra(IS_FROM_MANAJEMEN_IZI, false)



        permission?.let {
            it.user?.let { user ->
//                imgKaryawan.loadCircleImage(it.user.photo_profile_url.toString())
                d { it.user.photo_profile_url.toString() }

                txtNamaPemohon.text = "${it.user.full_name}"
//                txtDivisiKaryawan.text = ":   ${it.user.division_name}"
                txtRole.text = "${it.user.position_name}"
//                txtKantor.text = ":   ${it.user.office_name}"
            }

//            val namaAtasan = it.management?.full_name ?: "-"
//            txtNamaAtasan.text = ":   $namaAtasan"
            txtAlasanTidakHadir.text = when (it.permission_type) {
                1 -> "Izin"
                2 -> "Sakit"
                else -> "Cuti"
            }

            txtKeterangan.text = "${it.explanation}"

            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateFrom = dateFormat.parse(it.date_from)
            val dateTo = dateFormat.parse(it.date_to)
//            txtDate.text = getDateStringFormatted(date)

            txtDateFrom.text = getDateStringFormatted(dateFrom)
            txtDateTo.text = getDateStringFormatted(dateTo)

            when(it.status){
                0 -> {
                    txtStatus.text = "REQUESTED"
                    txtStatus.setBackgroundResource(R.drawable.bg_status_requested)
                }
                2 -> {
                    txtStatus.text = "DISETUJUI"
                    txtStatus.setBackgroundResource(R.drawable.bg_status_approved)
                }
                3 -> {
                    txtStatus.text = "DITOLAK"
                    txtStatus.setBackgroundResource(R.drawable.bg_status_rejected)
                }
            }

//            it.created_at?.let {
//                txtTanggalPengajuan.text = ":   $it"
//            } ?: kotlin.run {
//                txtTanggalPengajuan.text = ":   -"
//            }

//            button.setOnClickListener { view ->
//                StfalconImageViewer.Builder<String>(
//                    this,
//                    listOf(it.attachment_img_url)
//                ) { view, image ->
//                    Glide.with(this)
//                        .load(image).into(view)
//                }.show()
//            }

            if (isFromManajemenIzin) {
                if (it.status == 0) layoutAction.visible() else layoutAction.gone()
            } else {
                layoutAction.gone()
            }



        }


        btnSetuju.setOnClickListener {
            isApprove = true
            confirmDialog("Apakah anda yakin menyetujui pengajuan izin ini?")
        }

        btnTolak.setOnClickListener {
            isApprove = false
            confirmDialog("Apakah anda yakin menolak pengajuan izin ini?")
        }

        vm.approvePermissionResponse.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    isStatusChanged = true
                    createAlertSuccess(this, it.data.message)
                    layoutAction.gone()
                    txtStatus.text = when(isApprove){
                        true -> "Disetujui"
                        else -> "Ditolak"
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })


    }

    private fun confirmDialog(message: String){
        MaterialDialog(this).show {
            cornerRadius(10f)
            title(text = "Konfirmasi")
            message(text = message)
            positiveButton(text = "Ya") {
                it.dismiss()
                permission?.let {
                    if (isApprove){
                        vm.approvePermission(it.id, 2)
                    } else {
                        vm.approvePermission(it.id, 3)
                    }
                }

            }
            negativeButton(text = "Tidak") {
                it.dismiss()
            }
        }
    }

    override fun onBackPressed() {
        if (isStatusChanged){
            val intent = Intent()
            intent.putExtra("isStatusChanged", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            super.onBackPressed()
        }

    }
}
