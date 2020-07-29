package id.android.kmabsensi.presentation.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.activity_cari_data_sdm.*
import kotlinx.android.synthetic.main.activity_partner_picker.*
import kotlinx.android.synthetic.main.edittext_search.view.*
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseSearchActivity : BaseActivity() {

    abstract fun search(keyword: String)
    abstract fun restoreData()

    val searchView: View by lazy {
        LayoutInflater.from(this).inflate(R.layout.edittext_search, null)
    }

    val toolbarContent: FrameLayout by lazy {
        findViewById<FrameLayout>(R.id.toolbar_content)
    }

    val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchView.et_search.addTextChangedListener {
            /* Ketika query kosong, maka tampil view not found */
            if (searchView.et_search.text.isNullOrEmpty()) {
                restoreData()
                return@addTextChangedListener
            }

            handleOnTextChange(searchView.et_search.text.toString())
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

    fun setupSearchToolbar(title: String) {
        setupToolbar(title, isSearchVisible = true)

        btnSearch.setOnClickListener {
            isSearchMode = true
            /* add search view from edittext_search.xml */
            toolbarContent.visibility = View.VISIBLE
            toolbarContent.addView(searchView)

            /* Show keyboard */
            searchView.et_search.requestFocus()
            val imm: InputMethodManager? = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.showSoftInput(searchView.et_search, InputMethodManager.SHOW_IMPLICIT)
        }

    }

    override fun onBackPressed() {
        if (isSearchMode){
            isSearchMode = false
            restoreData()
            toolbarContent.visibility = View.GONE
            toolbarContent.removeView(searchView)
        } else {
            super.onBackPressed()
        }
    }

}