package id.android.kmabsensi.presentation.komship.leads

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komship.LeadsItem
import id.android.kmabsensi.databinding.ItemRowLeadsBinding
import id.android.kmabsensi.utils.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LeadsOrderAdapter(
    val context: Context,
    val listener: onAdapterListener
) : RecyclerView.Adapter<LeadsOrderAdapter.VHolder>(), Filterable {

    var list: MutableList<LeadsItem> = ArrayList()
    var listFiltered: MutableList<LeadsItem> = ArrayList()

    init {
        listFiltered = list
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<LeadsItem>) {
        list.clear()
        Log.d("data Leads Adapter", "setData: $newList")
        list.addAll(newList)
        Log.d("List Data Lead", "setData: $newList")
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        listFiltered.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    interface onAdapterListener {
        fun onCLick(data: LeadsItem)
    }

    inner class VHolder(
        val binding: ItemRowLeadsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LeadsItem) {

            val dateLeads = data.date_leads
            val dateFormat1 = SimpleDateFormat(DATE_FORMAT)
            val dateFormat2 = SimpleDateFormat(DATE_FORMAT3, LOCALE)
            val date = dateFormat2.format(dateFormat1.parse(dateLeads))

            val dateTimeToday = dateFormat1.format(Date())

            if (!dateTimeToday.equals(dateLeads)){
                binding.ivDeleteLeads.gone()
            }

            binding.apply {
                tvTime.text = data.leads_time
                tvDate.text = date
                ivDeleteLeads.setOnClickListener { listener.onCLick(data) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder = VHolder(
        ItemRowLeadsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: LeadsOrderAdapter.VHolder, position: Int) {
        val data = listFiltered[position]
        holder.binding.tvLeadsTitle.text = "Leads ${position+1}"
        holder.bind(data)
    }

    override fun getItemCount(): Int = listFiltered.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charSearch = p0.toString()
                if (charSearch.isEmpty()) {
                    listFiltered = list
                } else {
                    val filtered = ArrayList<LeadsItem>()
                    for (leads in list) {
                        if (leads.date_leads?.lowercase()!!.contains(charSearch.lowercase())) {
                            filtered.add(leads)

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
                listFiltered = results?.values as ArrayList<LeadsItem>
                notifyDataSetChanged()
            }
        }
    }
}