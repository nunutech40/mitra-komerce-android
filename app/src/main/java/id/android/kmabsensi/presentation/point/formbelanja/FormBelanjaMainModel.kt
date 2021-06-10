package id.android.kmabsensi.presentation.point.formbelanja

// type untuk mengategorikan data dalam grouping list penarikan
const val TYPE_HEADER = 1
const val TYPE_WITHDRAWAL = 0

data class FormBelanjaMainModel(
    val type: Int? = 0,
    val data: FormBelanjaModel
    )
