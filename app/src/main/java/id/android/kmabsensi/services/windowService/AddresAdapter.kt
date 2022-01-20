package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komboard.ProvinceResults
import id.android.kmabsensi.databinding.ItemAddressBinding
import kotlinx.android.synthetic.main.item_address.view.*

class AddresAdapter(
    val listener: onAddressAdapterLintener
): RecyclerView.Adapter<AddresAdapter.VHAddres>() {

    private var list: MutableList<ProvinceResults> = ArrayList()

    class VHAddres(
        val binding: ItemAddressBinding
        ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHAddres = VHAddres(
        ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHAddres, position: Int) {
        val data = list[position]
        holder.itemView.apply {
            tv_nameAddress.text = data.province_name
            setOnClickListener {
                listener.onClick(data)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<ProvinceResults>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun filterList(filteredCourseList: ArrayList<ProvinceResults>) {
        this.list = filteredCourseList;
        notifyDataSetChanged();
    }
    interface onAddressAdapterLintener{
        fun onClick(dataAddress: ProvinceResults)
    }
}