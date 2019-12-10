package id.android.kmabsensi

import android.app.Application
import com.github.ajalt.timberkt.Timber
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import id.android.kmabsensi.di.myAppModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        ViewPump.init(
//            ViewPump.builder()
//                .addInterceptor(
//                    CalligraphyInterceptor(
//                        CalligraphyConfig.Builder()
//                            .setDefaultFontPath("fonts/ProximaNova-Regular.otf")
//                            .setFontAttrId(R.attr.fontPath)
//                            .build()
//                    )
//                )
//                .build()
//        )
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@App)
            modules(myAppModule)
        }
    }

}