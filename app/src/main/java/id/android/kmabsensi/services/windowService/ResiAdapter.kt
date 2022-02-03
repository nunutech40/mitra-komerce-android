package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komboard.DataResultResi
import id.android.kmabsensi.databinding.ItemResiBinding
import kotlinx.android.synthetic.main.item_resi.view.*

class ResiAdapter(
    val listener : onAdapterListener
): RecyclerView.Adapter<ResiAdapter.ResiVH>() {
    private var list : MutableList<DataResultResi> = ArrayList()
    class ResiVH(
        val binding: ItemResiBinding
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResiVH = ResiVH(
        ItemResiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    @SuppressLint("LogNotTimber")
    override fun onBindViewHolder(holder: ResiVH, position: Int) {
        val data = list[position]
        holder.itemView.apply{
            tv_namaPenggunaResi.text = data.customerName
            tv_tanggalResi.text = data.orderDate
            setOnClickListener {
                listener.onClick(data)
            }
        }
    }
    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<DataResultResi>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    interface onAdapterListener{
        fun onClick(dataResi: DataResultResi)
    }
}