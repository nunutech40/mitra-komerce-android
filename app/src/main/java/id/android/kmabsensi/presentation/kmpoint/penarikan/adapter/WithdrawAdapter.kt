package id.android.kmabsensi.presentation.kmpoint.penarikan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse.Data.DataListShopping
import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse
import id.android.kmabsensi.databinding.ItemRowPenarikanPoinBinding
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingRequestModel
import id.android.kmabsensi.presentation.kmpoint.penarikan.TYPE_HEADER
import id.android.kmabsensi.presentation.kmpoint.penarikan.WithdrawMainModel
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_penarikan_poin.view.*

class WithdrawAdapter(
        val context: Context,
        val listener: onAdapterListener
) : PagedListAdapter<WithdrawMainModel, WithdrawAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WithdrawMainModel>(){
            override fun areItemsTheSame(oldItem: WithdrawMainModel, newItem: WithdrawMainModel): Boolean = oldItem.data.id == oldItem.data.id
            override fun areContentsTheSame(oldItem: WithdrawMainModel, newItem: WithdrawMainModel): Boolean = oldItem == oldItem
        }
    }

    inner class ViewHolder(private val binding : ItemRowPenarikanPoinBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(dataList: WithdrawMainModel?) {
            with(binding) {
                if (dataList!!.type == TYPE_HEADER){
                    txDate.visible()
                    txDate.text = dataList.data.createdAt.split(" ")[0]
                }else{
                    txDate.gone()
                }
                dataList.data.apply {
                    binding.txNoTransaksi.gone()
                    txUsername.text = this.user.fullName
                    txNoPartner.text = "No. Partner: -"
                    txTotalPoin.text = "${this.nominal} Poin"
                    txStatus.text = "${this.status}"
                    var status = this.status
                    txStatus.apply {
                        text = getTextStatus(status)
                        setTextColor(resources.getColor(getStatusTextColor(status)))
                        setBackgroundColor(resources.getColor(getStatusBackgroundColor(status)))
                    }
                    itemView.setOnClickListener {
                        listener.onClickde(dataList.data)
                    }
                }
            }
        }
    }

    interface onAdapterListener{
        fun onClickde(data : GetWithdrawResponse.DataWithDraw.DataDetailWithDraw)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            ItemRowPenarikanPoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataList = getItem(position)
        if (dataList != null) holder.bind(dataList)
    }

    fun getTextStatus(status : String) : String{
        return when (status) {
            "requested" -> "Diajukan"
            "completed" -> "Selesai"
            "rejected" -> "Ditolak"
            "canceled" -> "Dibatalkan"
            "approved" -> "Disetujui"
            else -> "-"
        }
    }

    fun getStatusTextColor(status : String) : Int{
        return  when (status) {
            "requested" -> R.color.cl_yellow
            "approved" -> R.color.cl_green
            "completed" -> R.color.cl_blue
            "rejected" -> R.color.cl_orange
            "canceled" -> R.color.cl_orange
            else -> R.color.cl_yellow
        }
    }

    fun getStatusBackgroundColor(status : String) : Int{
        return  when (status) {
            "requested" -> R.color.cl_semi_yellow
            "approved" -> R.color.cl_semi_green
            "completed" -> R.color.cl_semi_blue
            "rejected" -> R.color.cl_semi_orange
            "canceled" -> R.color.cl_semi_orange
            else -> R.color.cl_semi_yellow
        }
    }

}