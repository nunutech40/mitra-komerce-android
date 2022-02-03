package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.android.kmabsensi.data.remote.response.komboard.CostResults
import id.android.kmabsensi.data.remote.response.komboard.OngkirResults
import id.android.kmabsensi.databinding.ItemHargaOngkirBinding
import id.android.kmabsensi.services.windowService.LayoutCekongkir.Companion.daftarHarga
import id.android.kmabsensi.services.windowService.LayoutCekongkir.Companion.hashHarga
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
            check_harga.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
//                    check_harga.isChecked = false
                    listener.onClick(data)
                    hashHarga.put("${nama_expedisi.text}", "${nama_expedisi.text} ${harga_pengiriman.text} | ${prediksi_tiba.text}")
                    Log.d("onPicking", "updated $hashHarga")
                }else{
//                    check_harga.isChecked = true
                    hashHarga.remove("${nama_expedisi.text}")
                    Log.d("onunpicking", "onBindViewHolder: updated $hashHarga")
                }
            }
            setOnClickListener {
                if (check_harga.isChecked){
                    check_harga.isChecked = false
                    hashHarga.remove("${nama_expedisi.text}")
                    Log.d("onunpicking", "onBindViewHolder: updated $hashHarga")
                }else{
                    check_harga.isChecked = true
                    listener.onClick(data)
                    hashHarga.put("${nama_expedisi.text}", "${nama_expedisi.text} ${harga_pengiriman.text} | ${prediksi_tiba.text}")
                    Log.d("onPicking", "updated $hashHarga")
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
