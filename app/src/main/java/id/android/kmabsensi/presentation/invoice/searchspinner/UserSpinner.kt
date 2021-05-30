package id.android.kmabsensi.presentation.invoice.searchspinner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserSpinner(
        val id: Int,
        val showName: String,
        val titlePage: String,
        val username: String,
        val type: String
) : Parcelable

const val filterLeaderName = "filterLeaderName"
const val filterLeaderId = "filterLeaderId"
const val filterPartnerName = "filterPartnerName"
const val filterPartnerId = "filterPartnerId"