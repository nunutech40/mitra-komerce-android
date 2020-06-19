package id.android.kmabsensi.presentation.partner.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.ListPartnerResponse
import id.android.kmabsensi.presentation.partner.PartnerActivity
import id.android.kmabsensi.presentation.partner.PartnerItem
import id.android.kmabsensi.presentation.partner.detail.DetailPartnerActivity
import id.android.kmabsensi.utils.PARTNER_DATA_KEY
import id.android.kmabsensi.utils.PARTNER_RESPONSE_KEY
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_search_partner.*
import org.jetbrains.anko.startActivityForResult

class SearchPartnerActivity : AppCompatActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var data: ListPartnerResponse

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_partner)

        data = intent.getParcelableExtra(PARTNER_RESPONSE_KEY)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cari Data Partner"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRv()

        /* Show keyboard */
        searchView.requestFocus()
        val imm: InputMethodManager? =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)

        searchView.addTextChangedListener {
            /* Ketika query kosong, maka tampil view not found */
            if (searchView.text.isNullOrEmpty()) {
                layout_empty.visibility = View.VISIBLE
                rvPartner.visibility = View.GONE
                return@addTextChangedListener
            }

            layout_empty.gone()
            rvPartner.visible()
            handleOnTextChange(searchView.text.toString())
        }
    }


    fun initRv() {
        rvPartner.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.divider
                    ), true
                )
            )
            adapter = groupAdapter
        }
    }

    private fun handleOnTextChange(keyword: String) {
        mHandler.removeCallbacksAndMessages(null)
        if (keyword.isNotEmpty()) {
            mHandler.postDelayed({
                search(keyword)
            }, 500)
        }
    }

    private fun search(keyword: String) {
        groupAdapter.clear()

        val filter = data.partners.filter {
            it.fullName.toLowerCase().contains(keyword.toLowerCase()) || it.noPartner == keyword
        }

        filter.forEach { partner ->
            groupAdapter.add(PartnerItem(partner) {
                startActivityForResult<DetailPartnerActivity>(
                    PartnerActivity.CRUD_PARTNER_RC,
                    PARTNER_DATA_KEY to it
                )
            })
        }

    }

    override fun onStop() {
        /* hide keyboard when leave */
        val imm: InputMethodManager? =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(searchView.windowToken, 0)
        super.onStop()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PartnerActivity.CRUD_PARTNER_RC && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra("message")
            val intent = Intent()
            intent.putExtra("message", message)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}