package id.android.kmabsensi.presentation.komship.detaildataorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityDetailDataOrderBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import org.jetbrains.anko.toast

class DetailDataOrderActivity : BaseActivityRf<ActivityDetailDataOrderBinding>(
    ActivityDetailDataOrderBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Data Order", isBackable = true)
        setupListener()
    }

    private fun setupListener(){
        binding.apply {
            btnToTheTop.setOnClickListener {
                toast("Scroll to the top")
                nestedScrollView.scrollTo(0, 0)
            }
        }
    }
}