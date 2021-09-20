package id.android.kmabsensi.presentation.komship.detailorder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.databinding.ItemRowProductDetailBinding
import id.android.kmabsensi.utils.loadImageFromUrl

class ProductDetailAdapter(
    val context : Context,
    val listener : onadapterListener
) : RecyclerView.Adapter<ProductDetailAdapter.VHolder>() {
    private var list : MutableList<DetailProductSementara> = ArrayList()
    inner class VHolder(
        val binding : ItemRowProductDetailBinding
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DetailProductSementara) {
            binding.apply {
                tvNameProduct.text = data.nameProduct
                tvPrice.text = data.price
                tvVariant.text = data.variant
                tvDetailProduct.text = data.detailProduct
                imgProduct.loadImageFromUrl(data.img)
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
        fun onClick(data : DetailProductSementara)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData : List<DetailProductSementara>){
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }
}

data class DetailProductSementara(
    val img : String,
    val nameProduct : String,
    val variant : String,
    val price : String,
    val detailProduct : String,
)