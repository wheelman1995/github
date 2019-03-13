package ru.wheelman.github.di.modules

import androidx.appcompat.widget.AppCompatImageView
import dagger.Binds
import dagger.Module
import ru.wheelman.github.di.modules.AppModule.Binder
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.view.utils.GlideImageLoader
import ru.wheelman.github.view.utils.ImageLoader

@Module(includes = [Binder::class])
class AppModule {

//    @Provides
//    @AppScope
//    fun dataBindingComponent(bindingAdapters: BindingAdapters) = object : DataBindingComponent {
//        override fun getBindingAdapters(): BindingAdapters = bindingAdapters
//    }

    @Module
    interface Binder {

        @Binds
        @AppScope
        fun glideImageLoader(glideImageLoader: GlideImageLoader): ImageLoader<AppCompatImageView>
    }
}