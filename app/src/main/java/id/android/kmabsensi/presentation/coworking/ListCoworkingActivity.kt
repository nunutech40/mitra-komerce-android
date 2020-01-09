package id.android.kmabsensi.presentation.coworking

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_list_coworking.*
import org.jetbrains.anko.startActivity

class ListCoworkingActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_coworking)

        setToolbarTitle("Co-Working")
        myDialog = MyDialog(this)

        btnAddCoworking.setOnClickListener {
            startActivity<CoworkingActivity>()
        }

        initRv()

        groupAdapter.add(CoworkingItem())
        groupAdapter.add(CoworkingItem())

    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvCoworkingSpace.apply {
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }
}
