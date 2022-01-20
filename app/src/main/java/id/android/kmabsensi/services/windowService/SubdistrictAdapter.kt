package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komboard.CityResults
import id.android.kmabsensi.data.remote.response.komboard.SubDistrictResults
import id.android.kmabsensi.databinding.ItemAddressBinding
import kotlinx.android.synthetic.main.item_address.view.*

class SubdistrictAdapter(
    val listener: onDistrictListener
): RecyclerView.Adapter<SubdistrictAdapter.VHDistrict>() {
    private var list: MutableList<SubDistrictResults> = ArrayList()

    interface onDistrictListener{
        fun onClick(dataDistrict: SubDistrictResults)
    }

    class VHDistrict(
        val binding: ItemAddressBinding
    ):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHDistrict = VHDistrict(
        ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHDistrict, position: Int) {
        val data = list[position]
        holder.itemView.apply {
            tv_nameAddress.text = data.subdistrict_name
            setOnClickListener {
                listener.onClick(data)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<SubDistrictResults>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredCourseList: ArrayList<SubDistrictResults>) {
        this.list = filteredCourseList;
        notifyDataSetChanged();
    }
}