package id.android.kmabsensi.presentation.komship.ordercart

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komship.DataProductItem
import id.android.kmabsensi.databinding.ItemRowOrderCartBinding
import id.android.kmabsensi.utils.loadImageFromUrl
import kotlinx.android.synthetic.main.item_row_order_cart.view.*

class OrderCartAdapter(
    val context : Context,
    val listener : onAdapterListener
    ): RecyclerView.Adapter<OrderCartAdapter.VHolder>() {

    private var list : MutableList<DataProductItem> = ArrayList()

    inner class VHolder(val binding : ItemRowOrderCartBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, data: DataProductItem) {
            binding.apply {
                imgProduct.loadImageFromUrl(data.productImages!!)
                tvNameProduct.text = data.productName
                tvAvailableProduct.text = "Tersedia: ${data.stock}"
                tvPrice.text = "Rp${data.price}"
                cbOrder.setOnCheckedChangeListener { buttonView, isChecked ->
                    listener.onChecked(position, isChecked, data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder = VHolder(
        ItemRowOrderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val data = list[position]
        holder.bind(position, data)
        holder.itemView.btn_minus.setOnClickListener {
            list.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = list.size

    interface onAdapterListener{
        fun onChecked(position: Int, isChecked : Boolean, product : DataProductItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData : List<DataProductItem>){
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }
}