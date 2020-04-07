package id.android.kmabsensi.presentation.invoice

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
import kotlinx.android.synthetic.main.fragment_invoice_active.*


class InvoiceActiveFragment : Fragment() {

    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice_active, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        for (i in 1..3){
            groupAdapter.add(ActiveInvoiceItem())
        }

    }

    private fun initRv(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        rvInvoiceActive.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

}
