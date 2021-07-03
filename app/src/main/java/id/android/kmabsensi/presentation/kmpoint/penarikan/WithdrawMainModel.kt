package id.android.kmabsensi.presentation.kmpoint.penarikan

import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse.DataWithDraw.DataDetailWithDraw

// type untuk mengategorikan data dalam grouping list penarikan
const val TYPE_HEADER = 1
const val TYPE_WITHDRAWAL = 0

data class WithdrawMainModel(
    val type: Int? = 0,
    val data: DataDetailWithDraw
    )
