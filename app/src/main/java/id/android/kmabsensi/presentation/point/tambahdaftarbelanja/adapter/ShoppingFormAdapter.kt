package id.android.kmabsensi.presentation.point.tambahdaftarbelanja.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.databinding.ItemRowFormBelanjaBinding
import id.android.kmabsensi.presentation.point.tambahdaftarbelanja.ToolsModel
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.invis
import id.android.kmabsensi.utils.visible

class ShoppingFormAdapter(
        val listForm : ArrayList<ToolsModel>,
        val listener : onAdapterListener
        ): RecyclerView.Adapter<ShoppingFormAdapter.viewHolder>(){

    class viewHolder(val binding: ItemRowFormBelanjaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = viewHolder(
        ItemRowFormBelanjaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val data  = listForm[position]
        holder.apply {
            if (position == 0) binding.btnRemove.invis() else binding.btnRemove.visible()
//            binding.etNameTools.setText("")
//            binding.txPriceForecasts.setText("")
            binding.btnRemove.setOnClickListener {
                listener.OnDeleteForm(data, position)
                binding.etNameTools.setText("")
                binding.txPriceForecasts.setText("")
            }
        }
    }

    override fun getItemCount() = listForm.size

    interface onAdapterListener{
        fun OnDeleteForm(dataForm : ToolsModel, position: Int)
    }

    fun setData(data : List<ToolsModel>){
        listForm.clear()
        listForm.addAll(data)
        notifyDataSetChanged()
    }
}