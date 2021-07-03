package id.android.kmabsensi.presentation.kmpoint.formbelanja

import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse.Data.DataListShopping

data class ShoppingRequestModel(
    val type: Int? = 0,
    val data: DataListShopping
    )
