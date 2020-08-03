package id.android.kmabsensi.presentation.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.admin.EvaluasiMenuActivity
import id.android.kmabsensi.presentation.invoice.InvoiceActivity
import id.android.kmabsensi.presentation.invoice.report.InvoiceReportActivity
import id.android.kmabsensi.presentation.partner.PartnerActivity
import id.android.kmabsensi.presentation.partner.administratif.AdministratifActivity
import id.android.kmabsensi.presentation.partner.kategori.KategoriPartnerActivity
import id.android.kmabsensi.presentation.partner.partneroff.PartnerOffActivity
import kotlinx.android.synthetic.main.fragment_partner_menu.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.btnBack
import org.jetbrains.anko.startActivity

class ManagementPartnerMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_management_partner_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            (parentFragment as HomeManagementFragment).hideGroupMenu()
        }

        btnInvoice.setOnClickListener {
            activity?.startActivity<InvoiceActivity>()
        }

        btnInvoiceReport.setOnClickListener {
            activity?.startActivity<InvoiceReportActivity>()
        }

        btnPartnerCategory.setOnClickListener {
            activity?.startActivity<KategoriPartnerActivity>()
        }

        btnDataPartner.setOnClickListener {
            activity?.startActivity<PartnerActivity>()
        }

        btnEvaluasi.setOnClickListener {
            activity?.startActivity<EvaluasiMenuActivity>()
        }

        btnAdministratif.setOnClickListener {
            activity?.startActivity<AdministratifActivity>()
        }

        btnPartnerOff.setOnClickListener {
            activity?.startActivity<PartnerOffActivity>()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ManagementPartnerMenuFragment().apply {

            }
    }
}