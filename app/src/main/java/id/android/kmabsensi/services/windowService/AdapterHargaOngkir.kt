package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komboard.CostResults
import id.android.kmabsensi.data.remote.response.komboard.OngkirResults
import id.android.kmabsensi.databinding.ItemHargaOngkirBinding
import kotlinx.android.synthetic.main.item_harga_ongkir.view.*

class AdapterHargaOngkir (
        val listener: onHargaOngkirListener
        ): RecyclerView.Adapter<AdapterHargaOngkir.VHHarga>(){
    private  var list: MutableList<CostResults> = ArrayList()
    class VHHarga(
        val binding: ItemHargaOngkirBinding
    ):RecyclerView.ViewHolder(binding.root)

    interface onHargaOngkirListener{
        fun onClick(costData: CostResults)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHHarga = VHHarga(
        ItemHargaOngkirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHHarga, position: Int) {
        val data = list[position]
        holder.itemView.apply {
            nama_expedisi.text = data.serviceCost
            jenis_expedisi.text = data.descriptionCost
            harga_pengiriman.text = "Rp. ${data.cost!![0].valueCost.toString()}"
            prediksi_tiba.text = "${data.cost!![0].etdCost.toString()} Hari"
            setOnClickListener {
                if (check_harga.isChecked){
                    check_harga.isChecked = false
                    Log.d("onunpicking", "onBindViewHolder: unpicking...")
                }else{
                    check_harga.isChecked = true
                    listener.onClick(data)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<CostResults>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
