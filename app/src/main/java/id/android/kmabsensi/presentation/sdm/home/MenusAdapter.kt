package id.android.kmabsensi.presentation.sdm.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.databinding.ItemRowMenuBinding

class MenusAdapter(
    val context: Context,
    val listener : onAdapterListener) : RecyclerView.Adapter<MenusAdapter.VHolder>() {

    private val list : MutableList<MenuModels> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder = VHolder(
        ItemRowMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    interface onAdapterListener{
        fun onClick(data : MenuModels)
    }

    fun setData(newList : List<MenuModels>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class VHolder(val binding: ItemRowMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MenuModels) {
            binding.apply {
                imgMenu.setImageResource(data.img)
                tvMenuName.text = data.name
                itemView.setOnClickListener {
                    listener.onClick(data)
                }
            }
        }
    }
}

