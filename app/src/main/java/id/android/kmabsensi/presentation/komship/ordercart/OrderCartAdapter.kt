package id.android.kmabsensi.presentation.komship.ordercart

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.databinding.ItemRowOrderCartBinding
import id.android.kmabsensi.utils.URL_SHOPPING_EMPTY
import id.android.kmabsensi.utils.convertRupiah
import id.android.kmabsensi.utils.loadImageFromUrl
import kotlinx.android.synthetic.main.item_row_order_cart.view.*

class OrderCartAdapter(
    val context : Context,
    val listener : onAdapterListener
    ): RecyclerView.Adapter<OrderCartAdapter.VHolder>() {

    private var list : MutableList<CartItem> = ArrayList()
    private var qty = 0
    private var maxQty = 1
    inner class VHolder(val binding : ItemRowOrderCartBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CartItem) {
            binding.apply {
                tvAvailableProduct.setTextColor(ContextCompat.getColor(context, R.color.cl_black))
                imgProduct.loadImageFromUrl(data.productImage?: URL_SHOPPING_EMPTY)
                tvAvailable.text = "${data.stock} pcs"
                maxQty = data.stock!!
                tvAvailableProduct.text = data.variantName
                tvNameProduct.text = data.productName
                tvPrice.text = convertRupiah((data.productPrice ?: 0).toDouble())
                cbOrder.isChecked = false
                tvTotal.text = data.qty.toString()
            }
        }
    }

    private fun validateQty(type : Int): Boolean{
        return  if (type == 0) (qty > 1) else (qty < maxQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder = VHolder(
        ItemRowOrderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
        holder.itemView.apply {

            btn_min.setOnClickListener {
                qty = holder.itemView.tv_total.text.toString().toInt()
                if (validateQty(0)) qty -= 1
                holder.itemView.tv_total.text = "$qty"
                listener.onUpdateQty(data, qty)
            }
            btn_plus.setOnClickListener {
                qty = holder.itemView.tv_total.text.toString().toInt()
                if (validateQty(1)) qty += 1
                holder.itemView.tv_total.text = "$qty"
                listener.onUpdateQty(data, qty)
            }
            cb_order.setOnCheckedChangeListener { _, isChecked ->
                    listener.onChecked(position, isChecked, data)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    interface onAdapterListener{
        fun onChecked(position: Int, isChecked : Boolean, product : CartItem)
        fun onUpdateQty(product: CartItem, qty: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData : List<CartItem>){
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }
}