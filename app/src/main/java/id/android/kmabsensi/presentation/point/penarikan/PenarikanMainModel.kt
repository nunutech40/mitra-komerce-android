package id.android.kmabsensi.presentation.point.penarikan

import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse.DataWithDraw.DataDetailWithDraw

// type untuk mengategorikan data dalam grouping list penarikan
const val TYPE_HEADER = 1
const val TYPE_WITHDRAWAL = 0

data class PenarikanMainModel(
    val type: Int? = 0,
    val data: DataDetailWithDraw
    )
