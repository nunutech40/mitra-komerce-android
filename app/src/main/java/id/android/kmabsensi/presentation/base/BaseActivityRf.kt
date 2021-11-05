package id.android.kmabsensi.presentation.base

import android.Manifest
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.wildma.idcardcamera.camera.IDCardCamera
import com.wildma.idcardcamera.utils.PermissionUtils
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.isVisib
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseActivityRf<B: ViewBinding>(val bindingFactory: (LayoutInflater) -> B)
    : AppCompatActivity()  {

    val binding : B by lazy {
        bindingFactory(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Tell the window that we want to handle/fit any system windows

            window.setDecorFitsSystemWindows(false)

            val controller = binding.root.windowInsetsController

            // Hide the keyboard (IME)
            controller?.hide(WindowInsets.Type.ime())

            // Sticky Immersive is now ...
            controller?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // When we want to hide the system bars
            controller?.hide(WindowInsets.Type.systemBars())

            /*val flag = WindowInsets.Type.statusBars()
            WindowInsets.Type.navigationBars()
            WindowInsets.Type.captionBar()
            window?.insetsController?.hide(flag)*/
        } else {
            //noinspection
            @Suppress("DEPRECATION")
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            //or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            //or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    // Hide the nav bar and status bar
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            //or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun checkPermissionCamera(): Boolean{
        return PermissionUtils.checkPermissionFirst(
            this, IDCardCamera.PERMISSION_CODE_FIRST,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
    }

    fun setupToolbar(
        title: String,
        isBackable: Boolean = false,
        isFilter: Boolean = false,
        isCart: Boolean = false,
        isSearch: Boolean = false,
        isDelete: Boolean = false,
    ) {
        val tvDelete = binding.root.findViewById<AppCompatTextView>(R.id.tv_delete)
        val tvTitle = binding.root.findViewById<AppCompatTextView>(R.id.txtTitle)
        val btnBack = binding.root.findViewById<AppCompatImageView>(R.id.btnBack)
        val btnFilter = binding.root.findViewById<AppCompatImageView>(R.id.btn_filter)
        val btnCart = binding.root.findViewById<FrameLayout>(R.id.btn_cart)
        val search = binding.root.findViewById<AppCompatImageView>(R.id.btn_search)
        val etSearch = binding.root.findViewById<AppCompatEditText>(R.id.et_search)
        tvTitle.text = title

        if (isBackable) btnBack.visible() else btnBack.gone()
        if (isFilter) btnFilter.visible() else btnFilter.gone()
        if (isCart) btnCart.visible() else btnCart.gone()
        if (isSearch) search.visible()
        else {
            search.gone()
            etSearch.gone()
        }
        if (isDelete) tvDelete.visible() else tvDelete.gone()

        btnBack.setOnClickListener {
            onBackPressed()
        }

        search.setOnClickListener {
           if (etSearch.visibility == View.VISIBLE){
               etSearch.gone()
               tvTitle.visible()
           }else{
               etSearch.visible()
               tvTitle.gone()
           }
        }
    }


}