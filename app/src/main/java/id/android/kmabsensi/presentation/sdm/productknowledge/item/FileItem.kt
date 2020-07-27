package id.android.kmabsensi.presentation.sdm.productknowledge.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.ProductKnowledge
import kotlinx.android.synthetic.main.item_row_file_pendukung.*

class FileItem(
    val file: ProductKnowledge.AttachmentFile,
    val listener: (ProductKnowledge.AttachmentFile) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            txtFileName.text = file.attachmentPath.split("/").last()
            btnLihatFile.setOnClickListener {
                listener(file)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_file_pendukung
    }
}