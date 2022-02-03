package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komboard.CityResults
import id.android.kmabsensi.data.remote.response.komboard.ProvinceResults
import id.android.kmabsensi.databinding.ItemAddressBinding
import kotlinx.android.synthetic.main.item_address.view.*

class CityAdapter(
    val listener: onCityAdapterListener
): RecyclerView.Adapter<CityAdapter.VHCity>() {
    private var list: MutableList<CityResults> = ArrayList()

    class VHCity(
        val binding: ItemAddressBinding
    ): RecyclerView.ViewHolder(binding.root)

    interface onCityAdapterListener{
        fun onClick(dataCity: CityResults)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHCity = VHCity(
        ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHCity, position: Int) {
        val data = list[position]
        holder.itemView.apply {
            tv_nameAddress.text = data.city_name
            setOnClickListener {
                listener.onClick(data)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<CityResults>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredCourseList: ArrayList<CityResults>) {
        this.list.clear()
        this.list = filteredCourseList
        notifyDataSetChanged()
    }
}
