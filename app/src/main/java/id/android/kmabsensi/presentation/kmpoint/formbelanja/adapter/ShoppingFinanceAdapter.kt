package id.android.kmabsensi.presentation.kmpoint.formbelanja.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse.Data.DataListShopping
import id.android.kmabsensi.databinding.ItemRowPenarikanPoinBinding
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingRequestModel
import id.android.kmabsensi.presentation.kmpoint.penarikan.TYPE_HEADER
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_penarikan_poin.view.*

class ShoppingFinanceAdapter(
        val context: Context,
        val listener: onAdapterListener
) : PagedListAdapter<ShoppingRequestModel, ShoppingFinanceAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ShoppingRequestModel>(){
            override fun areItemsTheSame(oldItem: ShoppingRequestModel, newItem: ShoppingRequestModel): Boolean = oldItem.data.id == oldItem.data.id
            override fun areContentsTheSame(oldItem: ShoppingRequestModel, newItem: ShoppingRequestModel): Boolean = oldItem == oldItem
        }
    }

    inner class ViewHolder(private val binding : ItemRowPenarikanPoinBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(dataList: ShoppingRequestModel?) {
            with(binding){
                if (dataList?.type == TYPE_HEADER){
                    txDate
                    txDate.visible()
                    txDate.text = dataList.data.createdAt!!.split(" ")[0]
                }else{
                    txDate.gone()
                }
                dataList?.data.apply {
                    txNoTransaksi.text = "No. "+this?.transactionNo
                    txUsername.text = this?.partner?.user?.fullName ?: "-"
                    txNoPartner.text = "No. Partner: ${this?.partner?.noPartner}"
                    txTotalPoin.text = "${this?.total} Poin"
                    var status = this?.status
                    txStatus.apply {
                        text = when(status){
                            "requested" -> "Diajukan"
                            "completed" -> "Selesai"
                            "rejected" -> "Ditolak"
                            "canceled" -> "Dibatalkan"
                            "approved" -> "Disetujui"
                            else -> "-"
                        }
                        setTextColor(
                                when(status){
                                    "requested" -> resources.getColor(R.color.cl_yellow)
                                    "approved" -> resources.getColor(R.color.cl_green)
                                    "rejected" -> resources.getColor(R.color.cl_orange)
                                    "canceled" -> resources.getColor(R.color.cl_orange)
                                    "completed" -> resources.getColor(R.color.cl_blue)
                                    else -> resources.getColor(R.color.cl_yellow)
                                }
                        )
                        setBackgroundColor(
                                when(status){
                                    "requested" -> resources.getColor(R.color.cl_semi_yellow)
                                    "approved" -> resources.getColor(R.color.cl_semi_green)
                                    "rejected" -> resources.getColor(R.color.cl_semi_orange)
                                    "canceled" -> resources.getColor(R.color.cl_semi_orange)
                                    "completed" -> resources.getColor(R.color.cl_semi_blue)
                                    else -> resources.getColor(R.color.cl_semi_yellow)
                                }
                        )
                    }
                    itemView.setOnClickListener {
                        listener.onClickde(dataList!!.data)
                    }
                }
            }
        }
    }

    interface onAdapterListener{
        fun onClickde(data : DataListShopping)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            ItemRowPenarikanPoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataList = getItem(position)
        if (dataList != null) holder.bind(dataList)
    }

}