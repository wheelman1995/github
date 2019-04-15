package ru.wheelman.github

import android.app.Application
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import ru.wheelman.github.di.components.AppComponent
import ru.wheelman.github.di.components.DaggerAppComponent

fun <T : Any> T.logd(msg: Any?) = Log.d(this::class.simpleName, " $msg")

class App : Application() {

    internal companion object {
        internal lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
//        initLeakCanary()
        initDagger()
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this);
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder().context(this).build()
    }
}