package id.android.kmabsensi.presentation.riwayat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.fragment_riwayat.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class RiwayatFragment : Fragment() {

    private val vm: RiwayatViewModel by inject()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

    }

    fun initRv(){
        val linearLayoutManager = LinearLayoutManager(context)
        rvRiwayat.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RiwayatFragment()
    }


}
