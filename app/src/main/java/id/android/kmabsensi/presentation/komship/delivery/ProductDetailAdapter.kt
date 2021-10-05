package id.android.kmabsensi.presentation.komship.delivery

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komship.CartItem
import id.android.kmabsensi.databinding.ItemRowProductDetailBinding
import id.android.kmabsensi.utils.convertRupiah
import id.android.kmabsensi.utils.loadImageFromUrl

class ProductDetailAdapter(
    val context : Context,
    val listener : onadapterListener
) : RecyclerView.Adapter<ProductDetailAdapter.VHolder>() {
    private var list : MutableList<CartItem> = ArrayList()
    inner class VHolder(
        val binding : ItemRowProductDetailBinding
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CartItem) {
            binding.apply {
                tvNameProduct.text = data.productName
                tvPrice.text = convertRupiah(data.productPrice?.toDouble()!!)
                tvVariant.text = data.variantName
                tvDetailProduct.text = "${data.qty} Barang (x gram)"
                // TODO : Dummy image
//                imgProduct.loadImageFromUrl(data.productImage?.get(0) ?: "https://png.pngtree.com/png-vector/20191031/ourlarge/pngtree-t-shirt-and-hanger-vector-illustration-isolated-on-white-background-clothes-png-image_1928833.jpg")
                imgProduct.loadImageFromUrl("https://png.pngtree.com/png-vector/20191031/ourlarge/pngtree-t-shirt-and-hanger-vector-illustration-isolated-on-white-background-clothes-png-image_1928833.jpg")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder = VHolder(
        ItemRowProductDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    interface onadapterListener{
        fun onClick(data : CartItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData : List<CartItem>){
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }
}