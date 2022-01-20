package id.android.kmabsensi.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import com.readystatesoftware.chuck.ChuckInterceptor
import id.android.kmabsensi.BuildConfig
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.presentation.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


fun provideOkHttpClient(interceptor: AuthInterceptor, context:Context): OkHttpClient {
    val httpClient = OkHttpClient.Builder()
    httpClient.apply {
        writeTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        callTimeout(60, TimeUnit.SECONDS)
        addInterceptor(interceptor)
        if (id.android.kmabsensi.BuildConfig.DEBUG) {
            addInterceptor(ChuckInterceptor(context))
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(logging)
        }

    }
    return httpClient.build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, baseUrl: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}


class AuthInterceptor(var pref: PreferencesHelper, val context:Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .addHeader("Authorization", "Bearer " +pref.getString(PreferencesHelper.ACCESS_TOKEN_KEY))
            .build()

        Log.d("TAGTAGTAG", "intercept22: ${pref.getString(PreferencesHelper.ACCESS_TOKEN_KEY)}")

        val response = chain.proceed(request)

        if (response.code == 401){
            pref.clear()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        return response
    }
}


inline fun <reified T> createWebServiceKomship(okHttpClient: OkHttpClient, baseUrl: String): T {
    val retrofitKom = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
    return retrofitKom.create(T::class.java)
}
object ApiClient {
    const val BASE_URL = BuildConfig.BASE_URL_ABSENSI
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}
object ApiClientKomboard {
    const val BASE_URL = BuildConfig.BASE_URL_ABSENSI_KOMSHIP
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}
object ApiClientOngkir {
    const val BASE_URL = "https://pro.rajaongkir.com/api/"
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get(){
            if (retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}

