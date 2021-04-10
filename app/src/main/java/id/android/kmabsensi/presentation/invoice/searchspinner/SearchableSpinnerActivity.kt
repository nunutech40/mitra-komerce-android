package id.android.kmabsensi.presentation.invoice.searchspinner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.databinding.ActivitySearchableSpinnerBinding
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.fragment_invoice_active.*

class SearchableSpinnerActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySearchableSpinnerBinding.inflate(layoutInflater) }
    private lateinit var searchAdapter: SearchableSpinnerAdapter
    private val listUser = ArrayList<UserSpinner>()
    private lateinit var sharedPref: PreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sharedPref = PreferencesHelper(this)
        listUser.addAll(intent.getParcelableArrayListExtra<UserSpinner>("listUserSpinner") as ArrayList<UserSpinner>)
        if (listUser.size == 0) layout_empty.visible() else layout_empty.gone()
        setupRecyclerview()
        setupListener()
    }

    private fun setupListener() {
        binding.editSearch.doAfterTextChanged {
            searchAdapter.filter.filter(it.toString())
        }
    }

    private fun setupRecyclerview() {
        searchAdapter = SearchableSpinnerAdapter(
                listUser,
                object : SearchableSpinnerAdapter.onAdapterListener{
            override fun onCLicked(user: UserSpinner) {
                if (user.type.equals(getString(R.string.text_leader))){
                    sharedPref.saveString(filterLeaderName, user.username)
                    sharedPref.saveString(filterLeaderId, user.id.toString())
                }else{
                    sharedPref.saveString(filterPartnerName, user.username)
                    sharedPref.saveString(filterPartnerId, user.id.toString())
                }
                this@SearchableSpinnerActivity.finish()
            }
        })
        with(binding.listUser){
            setHasFixedSize(true)
            adapter = searchAdapter
        }
    }
}