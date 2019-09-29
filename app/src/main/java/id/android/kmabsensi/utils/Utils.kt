package id.android.kmabsensi.utils

import okhttp3.MediaType
import okhttp3.RequestBody



fun String.createRequestBodyText() : RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), this)
}


//when(it){
//    is UiState.Loading -> {}
//    is UiState.Success -> {}
//    is UiState.Error -> {}
//}