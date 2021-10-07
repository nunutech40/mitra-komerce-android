package id.android.kmabsensi.presentation.komship.selectdestination

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komship.DestinationItem
import id.android.kmabsensi.databinding.ItemRowOneTextBinding
import kotlinx.android.synthetic.main.item_row_one_text.view.*

class DestinationAdapter(
    val listener: onAdapterListener
): RecyclerView.Adapter<DestinationAdapter.VHolder>() {

    private var list : MutableList<DestinationItem> = ArrayList()

    class VHolder(
        val binding : ItemRowOneTextBinding
        ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder = VHolder(
        ItemRowOneTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val data = list[position]
        holder.itemView.apply {
            tv_text.text = data.label
            setOnClickListener {
                listener.onCLick(data)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<DestinationItem>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    interface onAdapterListener{
        fun onCLick(data: DestinationItem)
    }
}