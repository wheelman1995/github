package ru.wheelman.github

import android.app.Application
import android.util.Log
import ru.wheelman.github.di.components.AppComponent
import ru.wheelman.github.di.components.DaggerAppComponent

fun <T : Any> T.logd(msg: Any?) = Log.d(this::class.simpleName, " $msg")

class App : Application() {

    internal companion object {
        internal lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder().context(this).build()
    }
}