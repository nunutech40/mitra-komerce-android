package id.android.kmabsensi.presentation.komship.selectproduct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.android.kmabsensi.databinding.ActivitySelectProductBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf

class SelectProductActivity : BaseActivityRf<ActivitySelectProductBinding>(
    ActivitySelectProductBinding::inflate
){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}