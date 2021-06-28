package id.android.kmabsensi.presentation.kmpoint.formbelanja

import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse.Data.DataListShopping

// type untuk mengategorikan data dalam grouping list penarikan
const val TYPE_HEADER = 1
const val TYPE_WITHDRAWAL = 0

data class FormBelanjaMainModel(
    val type: Int? = 0,
    val data: DataListShopping
    )
