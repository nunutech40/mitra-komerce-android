package id.android.kmabsensi.presentation.komship.detaildataorder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komship.ProductDetailOrderItem
import id.android.kmabsensi.databinding.ItemRowProductDetailBinding
import id.android.kmabsensi.utils.convertRupiah
import id.android.kmabsensi.utils.loadImageFromUrl

class DetailOrderAdapter(
    private val context: Context
) : RecyclerView.Adapter<DetailOrderAdapter.VHolder>() {

    private var list: MutableList<ProductDetailOrderItem> = ArrayList()

    inner class VHolder(val binding: ItemRowProductDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductDetailOrderItem) {
            binding.apply {
                imgProduct.loadImageFromUrl(data.productImage?:"https://png.pngtree.com/png-vector/20190703/ourlarge/pngtree-shopping-bag-icon-in-trendy-style-isolated-background-png-image_1536177.jpg")
                tvNameProduct.text = data.productName
                tvVariant.text = data.variantName
                tvPrice.text = convertRupiah(data.price?.toDouble()!!)
                tvDetailProduct.text = "${data.qty} pcs (${data.weight}gr)"
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

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<ProductDetailOrderItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}