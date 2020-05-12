package id.android.kmabsensi.presentation.base

import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.ui.MyDialog
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.toolbar.*


abstract class BaseActivity : AppCompatActivity() {

    private lateinit var myDialog: MyDialog

    private var skeletonScreen: SkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDialog = MyDialog(this)

        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun showDialog() {
        myDialog.show()
    }

    fun hideDialog() {
        myDialog.dismiss()
    }

    fun showSkeleton(
        view: View, @LayoutRes layoutRes: Int,
        rvAdapter: RecyclerView.Adapter<GroupieViewHolder>? = null
    ) {
        if (view is RecyclerView){
            skeletonScreen = Skeleton.bind(view)
                .adapter(rvAdapter)
                .color(R.color.shimmer_color)
                .load(layoutRes)
                .show()
        }
    }

    fun hideSkeleton(){
        skeletonScreen?.hide()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun disableAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                window.decorView.importantForAutofill =
                    View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            } catch (e: Exception) {
                Crashlytics.log(e.message)
            }
        }
    }

    fun setupToolbar(
        title: String,
        isWhiteBackground: Boolean = false,
        isFilterVisible: Boolean = false,
        isSearchVisible: Boolean = false
    ) {
        txtTitle.text = title
        btnBack.setOnClickListener {
            onBackPressed()
        }
        if (isWhiteBackground) {
            toolbar_container.layoutParams.height = resources.getDimensionPixelSize(R.dimen._100dp)
            toolbar_container.setBackgroundResource(R.drawable.bg_toolbar_white)
            txtTitle.setTextColor(Color.BLACK)
            btnBack.setColorFilter(
                ContextCompat.getColor(this, R.color._7A7B7C),
                PorterDuff.Mode.SRC_IN
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        if (isFilterVisible) btnFilter.visible()
        if (isSearchVisible) btnSearch.visible()
    }

}