package id.android.kmabsensi.presentation.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.myevaluation.LeaderEvaluationActivity
import kotlinx.android.synthetic.main.fragment_evaluasi_menu.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.btnBack
import org.jetbrains.anko.startActivity

class EvaluasiMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_evaluasi_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            (parentFragment as HomeAdminFragment).hideGroupMenu()
        }

        btnEvaluasiLeader.setOnClickListener {
            activity?.startActivity<LeaderEvaluationActivity>()
        }

        btnEvaluasiKolaborasi.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }
}