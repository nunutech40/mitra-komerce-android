package id.android.kmabsensi.data.remote.response

data class JadwalShalatResponse(
    val jadwal: Jadwal,
    val query: Query,
    val status: String
)

data class Jadwal(
    val data: JadwalShalat?,
    val status: String
)

data class JadwalShalat(
    val ashar: String = "00:00:00",
    val dhuha: String,
    val dzuhur: String = "00:00:00",
    val imsak: String,
    val isya: String,
    val maghrib: String,
    val subuh: String,
    val tanggal: String,
    val terbit: String
)

data class Query(
    val format: String,
    val kota: String,
    val tanggal: String
)