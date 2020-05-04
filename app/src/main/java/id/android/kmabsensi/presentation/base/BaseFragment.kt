package id.android.kmabsensi.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.ui.MyDialog

abstract class BaseFragment : Fragment(){

    private lateinit var myDialog: MyDialog

    @LayoutRes
    abstract fun getLayoutResId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getLayoutResId(), container, false)
        myDialog = MyDialog(requireContext())
        return view
    }

    fun showLoadingDialog() {
        myDialog.show()
    }

    fun hideLoadingDialog() {
        myDialog.dismiss()
    }

}