package id.android.kmabsensi.presentation.ubahprofile

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.DataBank
import id.android.kmabsensi.databinding.ItemRowOneTextBinding

class SelectBankAdapter(
    val context: Context,
    val listener : onAdapterListener
): RecyclerView.Adapter<SelectBankAdapter.VHolder>(), Filterable{

    var list = ArrayList<DataBank>()
    var listFiltered = ArrayList<DataBank>()

    init {
        listFiltered = list
    }

    interface onAdapterListener{
        fun onCLick(data : DataBank)
    }

    inner class VHolder(
        val binding: ItemRowOneTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataBank) {
            binding.apply {
                tvText.text = data.name
            }
            itemView.setOnClickListener {
               listener.onCLick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder  = VHolder(
        ItemRowOneTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val data = listFiltered[position]
        holder.bind(data)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData : List<DataBank>){
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
                    val filtered = ArrayList<DataBank>()
                    for (bank in list) {
                        if (bank.name?.lowercase()!!.contains(charSearch.lowercase())) {
                            Log.d("performFiltering", "$bank")
                            filtered.add(bank)
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
                listFiltered = results?.values as ArrayList<DataBank>
                notifyDataSetChanged()
            }

        }
    }
}