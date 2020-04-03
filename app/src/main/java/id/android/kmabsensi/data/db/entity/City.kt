package id.android.kmabsensi.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "city")
data class City(
    @SerializedName("KODE_WILAYAH")
    @PrimaryKey
    val kodeWilayah: String = "",
    @SerializedName("LEVEL")
    val level: String = "",
    @SerializedName("MST_KODE_WILAYAH")
    val mstKodeWilayah: String = "",
    @SerializedName("NAMA")
    val nama: String = ""
)