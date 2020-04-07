package id.android.kmabsensi.presentation.partner.grafik.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.android.kmabsensi.R

/**
 * A simple [Fragment] subclass.
 */
class GrafikByCategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grafik_by_category, container, false)
    }

}
