package id.android.kmabsensi.presentation.kantor.report.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.stfalcon.imageviewer.StfalconImageViewer
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Presence
import id.android.kmabsensi.databinding.ItemRowReportAbsensiBinding
import id.android.kmabsensi.utils.capitalizeWords
import id.android.kmabsensi.utils.getDateStringFormatted
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_report_absensi.view.*
import java.text.SimpleDateFormat

class PresentasiAdapter(
        private val context: Context
): PagedListAdapter<Presence, PresentasiAdapter.ViewHolder>(DIFF_CALLBACK) {
    private var klik = false
    inner class ViewHolder(val binding: ItemRowReportAbsensiBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(dataPresentasi: Presence) {
            with(binding){
                itemView.setOnClickListener {
                    if (!klik){
                        cardViewCheckin.visible()
                        cardViewCheckout.visible()
                        llCheckIn.visible()
                        linearLayout3.visible()
                        klik = true
                    }else{
                        cardViewCheckin.gone()
                        cardViewCheckout.gone()
                        llCheckIn.gone()
                        linearLayout3.gone()
                        klik = false
                    }
                }
                txtCheckIn.text = "${dataPresentasi.check_in_datetime.split(" ")[1].substring(0, 5)}"
                Glide.with(context)
                        .load(dataPresentasi.checkIn_photo_url)
                        .fitCenter()
                        .centerCrop()
                    .override(50, 50)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(R.drawable.ic_fail_load_image)
                        .placeholder(R.drawable.ic_loading_image)
                        .into(btnLihatFotoDatang)
                btnLihatFotoDatang.setOnClickListener {
                    StfalconImageViewer.Builder<String>(
                            itemView.context,
                            listOf(dataPresentasi.checkIn_photo_url)
                    ) { view, image ->
                        Glide.with(itemView.context)
                                .load(image).into(view)
                    }.show()
                }

                dataPresentasi.checkout_date_time?.let {
                    itemView.txtCheckOut.visible()
                    itemView.btnLihatFotoPulang.isEnabled = true
                    itemView.txtCheckOut.text = "${it.split(" ")[1].substring(0, 5)}"

                    Glide.with(itemView)
                            .load(dataPresentasi.checkOut_photo_url)
                            .error(R.drawable.ic_fail_load_image)
                            .fitCenter()
                        .override(50, 50)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_loading_image)
                            .into(btnLihatFotoPulang)

                    itemView.btnLihatFotoPulang.setOnClickListener {
                        dataPresentasi.checkOut_photo_url?.let {photoUrl ->
                            StfalconImageViewer.Builder<String>(
                                    context,
                                    listOf(photoUrl)
                            ) { view, image ->
                                Glide.with(context)
                                        .load(image).into(view)
                            }.show()
                        }
                    }
                } ?: kotlin.run {
                    txtCheckOut.gone()
                    btnLihatFotoPulang.setImageResource(R.drawable.ic_un_checkout)
                    btnLihatFotoPulang.isEnabled = false
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = dateFormat.parse(dataPresentasi.check_in_datetime)
                itemView.txtDate.text = getDateStringFormatted(date)

                dataPresentasi.user?.let {
                    itemView.txtKantor.text = it.office_name
                    itemView.txtPartner.text = it.division_name
                    itemView.txtName.text = it.full_name.toLowerCase().capitalizeWords()
                }
            }
        }
    }

    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Presence>(){
            override fun areItemsTheSame(oldItem: Presence, newItem: Presence): Boolean = oldItem.id == oldItem.id
            override fun areContentsTheSame(oldItem: Presence, newItem: Presence): Boolean = oldItem == oldItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            ItemRowReportAbsensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataPresentasi = getItem(position)
        Log.d("_presentasiAdapter", "data: $dataPresentasi")
        if (dataPresentasi!=null) holder.bind(dataPresentasi)
    }
}