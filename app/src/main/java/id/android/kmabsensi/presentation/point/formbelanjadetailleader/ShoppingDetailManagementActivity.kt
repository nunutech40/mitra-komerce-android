package id.android.kmabsensi.presentation.point.formbelanjadetailleader

import android.os.Bundle
import id.android.kmabsensi.databinding.ActivityShoppingDetailManagementBinding
import id.android.kmabsensi.presentation.base.BaseActivity

class ShoppingDetailManagementActivity : BaseActivity() {
    private val binding by lazy { ActivityShoppingDetailManagementBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}