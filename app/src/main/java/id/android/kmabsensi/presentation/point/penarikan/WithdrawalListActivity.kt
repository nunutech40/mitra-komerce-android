package id.android.kmabsensi.presentation.point.penarikan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityWithdrawalListBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.point.detailpenarikan.WithdrawalDetailActivity
import id.android.kmabsensi.presentation.point.penarikan.adapter.PenarikanItem
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.toast

class WithdrawalListActivity : BaseActivity() {
    companion object{
        val TYPE_HEADER = 1
        val TYPE_WITHDRAWAL = 0
    }
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var dataPenarikan: ArrayList<PenarikanModel> = arrayListOf()
    private var groupDataPenarikan: ArrayList<PenarikanMainModel> = arrayListOf()

    private val binding by lazy { ActivityWithdrawalListBinding.inflate(layoutInflater) }
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
        for (i in 1..6){
            dataPenarikan.add(PenarikanModel(
                "name $i",
                "https://media.glamour.com/photos/5a425fd3b6bcee68da9f86f8/master/pass/best-face-oil.png",
                i,
                "$i",
                "SELESAI", "10-10-2010"))
            dataPenarikan.add(PenarikanModel(
                    "name $i",
                    "https://media.glamour.com/photos/5a425fd3b6bcee68da9f86f8/master/pass/best-face-oil.png",
                    i,
                    "$i",
                    "DIAJUKAN", "10-10-2010"))
        }
        for (i in 1..6){
            dataPenarikan.add(PenarikanModel(
                "name $i",
                "https://media.glamour.com/photos/5a425fd3b6bcee68da9f86f8/master/pass/best-face-oil.png",
                i,
                "$i",
                "DIAJUKAN", "12-10-2010"))
            dataPenarikan.add(PenarikanModel(
                    "name $i",
                    "https://media.glamour.com/photos/5a425fd3b6bcee68da9f86f8/master/pass/best-face-oil.png",
                    i,
                    "$i",
                    "SELESAI", "12-10-2010"))
        }
        var date = ""
        dataPenarikan.forEach {
            var type = 0
            if (!date.equals(it.date)) {
                type = TYPE_HEADER
                date = it.date!!
            }else {
                type = TYPE_WITHDRAWAL
            }
            groupDataPenarikan.add(PenarikanMainModel(type, it))
        }
        groupAdapter.clear()
        groupDataPenarikan.forEach {
            groupAdapter.add(PenarikanItem(this, it){
                var type = 0
                if (it.data.status!!.toLowerCase().equals("diajukan")) type = 1 else type = 0

                startActivity(Intent(this, WithdrawalDetailActivity::class.java)
                        .putExtra("typePenarikan", type))
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
        binding.toolbar.txtTitle.text = getString(R.string.text_penarikan_poin)
    }
}