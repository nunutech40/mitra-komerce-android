package id.android.kmabsensi.presentation.point.penarikan

// type untuk mengategorikan data dalam grouping list penarikan
const val TYPE_HEADER = 1
const val TYPE_WITHDRAWAL = 0

data class PenarikanMainModel(
    val type: Int? = 0,
    val data: PenarikanModel
    )
