package id.android.kmabsensi.presentation.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.FragmentPartnerMenuBinding
import id.android.kmabsensi.presentation.invoice.InvoiceActivity
import id.android.kmabsensi.presentation.invoice.report.InvoiceReportActivity
import id.android.kmabsensi.presentation.partner.PartnerActivity
import id.android.kmabsensi.presentation.partner.administratif.AdministratifActivity
import id.android.kmabsensi.presentation.partner.device.PartnerDeviceActivity
import id.android.kmabsensi.presentation.partner.kategori.KategoriPartnerActivity
import id.android.kmabsensi.presentation.partner.partneroff.PartnerOffActivity
import id.android.kmabsensi.presentation.point.formbelanja.ShoppingCartActivity
import id.android.kmabsensi.presentation.point.penarikan.WithdrawalListActivity
import id.android.kmabsensi.presentation.sdm.device.DataDeviceActivity
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
    private lateinit var binding: FragmentPartnerMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPartnerMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            (parentFragment as HomeAdminFragment).hideGroupMenu()
        }

        binding.btnInvoice.setOnClickListener {
            activity?.startActivity<InvoiceActivity>()
        }

        binding.btnInvoiceReport.setOnClickListener {
            activity?.startActivity<InvoiceReportActivity>()
        }

        binding.btnPartnerCategory.setOnClickListener {
            activity?.startActivity<KategoriPartnerActivity>()
        }

        binding.btnDataPartner.setOnClickListener {
            activity?.startActivity<PartnerActivity>()
        }

        binding.btnEvaluasi.setOnClickListener {
            activity?.startActivity<EvaluasiMenuActivity>()
        }

        binding.btnAdministratif.setOnClickListener {
            activity?.startActivity<AdministratifActivity>()
        }

        binding.btnPartnerOff.setOnClickListener {
            activity?.startActivity<PartnerOffActivity>()
        }

        binding.btnKmPoin.setOnClickListener {
            activity?.startActivity<WithdrawalListActivity>()
        }

        binding.btnShopingChart.setOnClickListener {
            activity?.startActivity<ShoppingCartActivity>()
        }
    }

}