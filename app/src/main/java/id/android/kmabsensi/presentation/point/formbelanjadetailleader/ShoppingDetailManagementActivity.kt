package id.android.kmabsensi.presentation.point.formbelanjadetailleader

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityShoppingDetailManagementBinding
import id.android.kmabsensi.presentation.base.BaseActivity

class ShoppingDetailManagementActivity : BaseActivity() {
    private val binding by lazy { ActivityShoppingDetailManagementBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListener()
    }

    private fun setupListener() {
        binding.btnBatalkan.setOnClickListener {
            // check in not yet
            val dialog = MaterialDialog(this).show {
                cornerRadius(5f)
                customView(
                    R.layout.dialog_batal_pengajuan_belanja,
                    scrollable = false,
                    horizontalPadding = false,
                    noVerticalPadding = true
                )
            }
            val customView = dialog.getCustomView()
            val btnYa = customView.findViewById<Button>(R.id.btn_ya)
            val btnTidak = customView.findViewById<Button>(R.id.btn_tidak)
            btnYa.setOnClickListener {
                dialog.dismiss()
                onBackPressed()
            }
            btnTidak.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}