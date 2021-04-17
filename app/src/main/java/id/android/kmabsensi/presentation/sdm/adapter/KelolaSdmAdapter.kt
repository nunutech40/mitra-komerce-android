package id.android.kmabsensi.presentation.sdm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.ItemRowSdmBinding

class KelolaSdmAdapter(
    private val context: Context,
    private val listener: onAdapterListener
) : PagedListAdapter<User, KelolaSdmAdapter.ViewHolder>(DIFF_CALLBACK) {

    interface onAdapterListener {
        fun onClicked(user: User)
    }

    inner class ViewHolder(private val binding: ItemRowSdmBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(datauser: User?) {
            with(binding){
                txtNamaSdm.setText(datauser?.full_name)
                txtPekerjaan.setText(datauser?.position_name)
                Glide.with(context)
                    .load(datauser?.photo_profile_url)
                    .into(imageView13)
                itemView.setOnClickListener {
                    listener.onClicked( datauser!! )
                }
            }
        }
    }

    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>(){
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == oldItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == oldItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemRowSdmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: KelolaSdmAdapter.ViewHolder, position: Int) {
        val datauser = getItem(position)
        holder.bind(datauser)
    }
}