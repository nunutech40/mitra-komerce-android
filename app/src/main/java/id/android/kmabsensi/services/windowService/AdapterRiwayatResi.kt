package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komboard.DataBulkResi
import id.android.kmabsensi.databinding.ItemRiwayatResiBinding
import kotlinx.android.synthetic.main.item_riwayat_resi.view.*

class AdapterRiwayatResi (
    val listener: onBulkResiListener
        ): RecyclerView.Adapter<AdapterRiwayatResi.VHRiwayatResi>(){
    private  var list: MutableList<DataBulkResi> = ArrayList()
    class VHRiwayatResi(
        val binding: ItemRiwayatResiBinding
    ):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHRiwayatResi = VHRiwayatResi(
        ItemRiwayatResiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VHRiwayatResi, position: Int) {
        val data = list[position]
        holder.itemView.apply {
            tv_tanggal_riwayat_resi.text = data.cnote_date
            tv_resi_riwayat_resi.text = "${data.airway_bil} (${data.customer})"
            tv_keterangan_riwayat_resi.text = data.last_status
            setOnClickListener {
                listener.onClick(data)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<DataBulkResi>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    interface onBulkResiListener{
        fun onClick(dataBulkResi: DataBulkResi)
    }
}