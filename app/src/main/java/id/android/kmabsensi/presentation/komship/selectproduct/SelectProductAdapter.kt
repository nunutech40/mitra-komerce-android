package id.android.kmabsensi.presentation.komship.selectproduct

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komship.ProductKomItem
import id.android.kmabsensi.databinding.ItemRowProductBinding
import id.android.kmabsensi.utils.loadImageFromUrl

class SelectProductAdapter(
    val context: Context,
    val listener : onAdapterListener
): RecyclerView.Adapter<SelectProductAdapter.VHolder>(), Filterable{

    var list = ArrayList<ProductKomItem>()
    var listFiltered = ArrayList<ProductKomItem>()

    init {
        listFiltered = list
    }

    interface onAdapterListener{
        fun onCLick(data : ProductKomItem)
    }

    inner class VHolder(
        val binding: ItemRowProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductKomItem) {
            binding.apply {
                ivProduct.loadImageFromUrl(if (data.productImage?.size != 0 ) data.productImage?.get(0)!! else "https://www.kindpng.com/picc/m/600-6008515_shopping-transparent-design-png-shopping-bag-icon-png.png")
                tvProductName.text = data.productName
            }
            itemView.setOnClickListener {
               listener.onCLick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder  = VHolder(
        ItemRowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val data = listFiltered[position]
        holder.bind(data)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData : ArrayList<ProductKomItem>){
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listFiltered.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listFiltered = list
                } else {
                    val filtered = ArrayList<ProductKomItem>()
                    for (product in list) {
                        if (product.productName?.lowercase()!!.contains(charSearch.lowercase())) {
                            Log.d("performFiltering", "$product")
                            filtered.add(product)
                        }
                    }
                    listFiltered = filtered
                }
                val filteredResult = FilterResults()
                filteredResult.values = listFiltered
                return filteredResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listFiltered = results?.values as ArrayList<ProductKomItem>
                notifyDataSetChanged()
            }

        }
    }
}