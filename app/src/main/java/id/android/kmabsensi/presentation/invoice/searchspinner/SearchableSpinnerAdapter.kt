package id.android.kmabsensi.presentation.invoice.searchspinner

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.databinding.ItemRowUserSpinnerBinding
import timber.log.Timber

class SearchableSpinnerAdapter (
    val listUser: ArrayList<UserSpinner>,
    val listener: onAdapterListener
        ): RecyclerView.Adapter<SearchableSpinnerAdapter.ViewHolder>(), Filterable{

    private var userFilter = ArrayList<UserSpinner>()

    init {
        userFilter = listUser
    }

    interface onAdapterListener{
        fun onCLicked(user: UserSpinner)
    }

    inner class ViewHolder(
        private val binding: ItemRowUserSpinnerBinding
        ): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserSpinner) {
            if (user.typeRole == 0){
                binding.tvName.setText("${user.id} - ${user.username}")
            }else{
                binding.tvName.setText("${user.username}")
            }
            itemView.setOnClickListener { listener.onCLicked(user) }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchableSpinnerAdapter.ViewHolder = ViewHolder(
        ItemRowUserSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SearchableSpinnerAdapter.ViewHolder, position: Int) {
        val user = userFilter[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = userFilter.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                Timber.d("charSearch: $charSearch")
                if (charSearch.isEmpty()) {
                    userFilter = listUser
                } else {
                    val citiesFiltered = ArrayList<UserSpinner>()
                    for (user in listUser) {
                        if (user.username.toLowerCase().contains(charSearch.toLowerCase())) {
                            citiesFiltered.add(user)
                        }
                    }
                    userFilter = citiesFiltered
                }
                val citiesFilteredResult = FilterResults()
                citiesFilteredResult.values = userFilter
                return citiesFilteredResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userFilter = results?.values as ArrayList<UserSpinner>
                notifyDataSetChanged()
            }

        }
    }
}