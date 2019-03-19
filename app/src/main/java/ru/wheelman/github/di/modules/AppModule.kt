package ru.wheelman.github.di.modules

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.wheelman.github.di.modules.AppModule.Binder
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.datasources.local.getUsersDb
import ru.wheelman.github.view.utils.GlideImageLoader
import ru.wheelman.github.view.utils.ImageLoader

@Module(includes = [Binder::class])
class AppModule {

//    @Provides
//    @AppScope
//    fun dataBindingComponent(bindingAdapters: BindingAdapters) = object : DataBindingComponent {
//        override fun getBindingAdapters(): BindingAdapters = bindingAdapters
//    }

    @Provides
    @AppScope
    fun usersDb(context: Context) = getUsersDb(context)

    @Module
    interface Binder {

        @Binds
        @AppScope
        fun glideImageLoader(glideImageLoader: GlideImageLoader): ImageLoader<AppCompatImageView>
    }
}