package id.android.kmabsensi.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible

abstract class BaseFragmentRf <B : ViewBinding>(
    private val bindingFactory : (LayoutInflater, ViewGroup?, Boolean) -> B
) : Fragment(){

    private var _binding : B? = null
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingFactory.invoke(inflater, container, false)

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupToolbar(
        title: String,
        isBackable: Boolean = false,
        isBgWhite: Boolean = false,
    ) {
        val toolbar = binding?.root?.findViewById<Toolbar>(R.id.toolbar)
        val tvTitle = binding?.root?.findViewById<AppCompatTextView>(R.id.txtTitle)
        val btnBack = binding?.root?.findViewById<AppCompatImageView>(R.id.btnBack)
        val btnFilter = binding?.root?.findViewById<AppCompatImageView>(R.id.btn_filter)
        if (isBgWhite) toolbar?.setBackgroundColor(resources.getColor(R.color.white))
        tvTitle?.text = title
        if (isBackable) btnBack?.visible() else btnBack?.gone()
        btnFilter?.gone()

    }

}