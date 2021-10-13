package id.android.kmabsensi.presentation.komship.dataorder

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.komship.OrderItem
import id.android.kmabsensi.databinding.ItemRowDataOrderBinding
import id.android.kmabsensi.utils.convertDate
import id.android.kmabsensi.utils.convertRupiah
import kotlinx.android.synthetic.main.item_row_data_order.view.*
import org.jetbrains.anko.toast

class DataOrderAdapter(
    val context: Context,
    val listener : onAdapterListener
    ) : RecyclerView.Adapter<DataOrderAdapter.VHolder>(), Filterable {

    var list : MutableList<OrderItem> = ArrayList()
    var listFiltered : MutableList<OrderItem> = ArrayList()

    init {
        listFiltered = list
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<OrderItem>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    interface onAdapterListener{
        fun onCLick(data : OrderItem)
    }

    inner class VHolder(
        val binding: ItemRowDataOrderBinding
        ): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrderItem) {
            binding.apply {
                var totalItem = 0
                data.product?.forEach {
                    totalItem += it.qty!!
                }
                val type = if (data.isKomship == 1) "Komship" else "Non Komship"
                tvUsername.text = data.customerName
                tvDateType.text = "${convertDate(data.orderDate!!)} - $type"
                tvNameProduct.text = "${data.product?.get(0)?.productName}"
                tvTotalPrice.text = convertRupiah(data.grandTotal!!.toDouble())
                tvResi.text = "Resi: ${data.airwayBill}"
                tvTotalProduct.text = "$totalItem pcs (${data.product?.size} produk)"
                tvStatus.setStatusView(data.orderStatus!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder = VHolder(
        ItemRowDataOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: DataOrderAdapter.VHolder, position: Int) {
        val data = listFiltered[position]
        holder.bind(data)
        holder.itemView.tv_resi.setOnClickListener {
            val stringBuilder = StringBuilder().append(data.airwayBill?:"Data Resi kosong")

            val clipboard: ClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("resi", stringBuilder.toString())
            clipboard.setPrimaryClip(clip)
            context.toast("Copied!")
        }

        holder.itemView.setOnClickListener {
            listener.onCLick(data)
        }
    }

    override fun getItemCount(): Int = listFiltered.size

    private fun AppCompatTextView.setStatusView(status : String){
        this.text = status
        when(status.lowercase()){
            "diajukan" -> {
                this.setTextColor(ContextCompat.getColor(context, R.color.cl_yellow))
                this.setBackgroundColor(ContextCompat.getColor(context, R.color.cl_semi_yellow))
            }
            "dikirim" -> {
                this.setTextColor(ContextCompat.getColor(context, R.color.cl_blue))
                this.setBackgroundColor(ContextCompat.getColor(context, R.color.cl_semi_blue))
            }
            "diterima" -> {
                this.setTextColor(ContextCompat.getColor(context, R.color.cl_green))
                this.setBackgroundColor(ContextCompat.getColor(context, R.color.cl_semi_green))
            }
            "batal" -> {
                this.setTextColor(ContextCompat.getColor(context, R.color.cl_orange))
                this.setBackgroundColor(ContextCompat.getColor(context, R.color.cl_semi_orange))
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charSearch = p0.toString()
                if (charSearch.isEmpty()){
                    listFiltered = list
                }else {
                    val filtered = ArrayList<OrderItem>()
                    for (order in list) {
                        if (order.customerName?.lowercase()!!.contains(charSearch.lowercase())) {
                            filtered.add(order)
                        }
                    }
                    listFiltered = filtered
                }
                val filteredResult = FilterResults()
                filteredResult.values = listFiltered
                return filteredResult
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                listFiltered = results?.values as ArrayList<OrderItem>
                notifyDataSetChanged()
            }

        }
    }
}