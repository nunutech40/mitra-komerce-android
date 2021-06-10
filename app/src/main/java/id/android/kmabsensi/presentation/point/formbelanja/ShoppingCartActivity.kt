package id.android.kmabsensi.presentation.point.formbelanja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityShoppingCartBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.point.penarikandetail.WithdrawalDetailActivity
import id.android.kmabsensi.presentation.point.formbelanja.adapter.FormBelanjaItem
import id.android.kmabsensi.presentation.point.formbelanjadetail.ShoppingDetailsActivity
import id.android.kmabsensi.presentation.point.penarikan.WithdrawalListActivity
import id.android.kmabsensi.utils.visible

class ShoppingCartActivity : BaseActivity() {
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var dataBelanja: ArrayList<FormBelanjaModel> = arrayListOf()
    private var groupDataBelanja: ArrayList<FormBelanjaMainModel> = arrayListOf()
    private val binding by lazy { ActivityShoppingCartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupData()
        setupView()
        setupListener()
        initRv()
    }
    private fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPenarikan.apply {
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }

    private fun setupData() {
        for (i in 1..3){
            dataBelanja.add(
                FormBelanjaModel(
                "name $i",
                "https://media.glamour.com/photos/5a425fd3b6bcee68da9f86f8/master/pass/best-face-oil.png",
                i,
                "$i",
                "DISETEJUI", "10-10-2010")
            )
        }
        for (i in 1..3){
            dataBelanja.add(FormBelanjaModel(
                "name $i",
                "https://media.glamour.com/photos/5a425fd3b6bcee68da9f86f8/master/pass/best-face-oil.png",
                i,
                "$i",
                "DISETEJUI", "12-10-2010"))
        }
        var date = ""
        dataBelanja.forEach {
            var type = 0
            if (!date.equals(it.date)) {
                type = WithdrawalListActivity.TYPE_HEADER
                date = it.date!!
            }else {
                type = WithdrawalListActivity.TYPE_WITHDRAWAL
            }
            groupDataBelanja.add(
                FormBelanjaMainModel(
                    type,
                    it
                )
            )
        }
        groupAdapter.clear()
        groupDataBelanja.forEach {
            groupAdapter.add(FormBelanjaItem(this, it){
                var type = 0
                if (it.data.status!!.toLowerCase().equals("disetujui")) type = 1 else type = 0
                startActivity(
                    Intent(this, ShoppingDetailsActivity::class.java))
            })
        }
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        binding.toolbar.btnSearch.visible()
        binding.toolbar.txtTitle.text = getString(R.string.text_form_belanja)
    }
}