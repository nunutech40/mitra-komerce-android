package id.android.kmabsensi.presentation.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.invoice.InvoiceActivity
import id.android.kmabsensi.presentation.invoice.report.InvoiceReportActivity
import id.android.kmabsensi.presentation.partner.PartnerActivity
import id.android.kmabsensi.presentation.partner.administratif.AdministratifActivity
import id.android.kmabsensi.presentation.partner.device.PartnerDeviceActivity
import id.android.kmabsensi.presentation.partner.kategori.KategoriPartnerActivity
import kotlinx.android.synthetic.main.fragment_partner_menu.*
import kotlinx.android.synthetic.main.fragment_partner_menu.btnDataPartner
import kotlinx.android.synthetic.main.fragment_partner_menu.btnEvaluasi
import kotlinx.android.synthetic.main.fragment_partner_menu.btnInvoice
import kotlinx.android.synthetic.main.fragment_partner_menu.btnInvoiceReport
import kotlinx.android.synthetic.main.fragment_partner_menu.btnPartnerCategory
import kotlinx.android.synthetic.main.toolbar.btnBack
import org.jetbrains.anko.startActivity

/*
 * sementara untuk role admin
 */
class PartnerMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            (parentFragment as HomeAdminFragment).hideGroupMenu()
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

        btnDevice.setOnClickListener {
            activity?.startActivity<PartnerDeviceActivity>()
        }

        btnEvaluasi.setOnClickListener {
            activity?.startActivity<EvaluasiMenuActivity>()
        }

        btnAdministratif.setOnClickListener {
            activity?.startActivity<AdministratifActivity>()
        }

        btnPartnerOff.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

}