package ru.wheelman.github.di.modules

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.test.espresso.idling.CountingIdlingResource
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import ru.wheelman.github.di.modules.FakeAppModule.Binder
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.datasources.local.getUsersDb
import ru.wheelman.github.view.utils.GlideImageLoader
import ru.wheelman.github.view.utils.ImageLoader

@Module(includes = [Binder::class])
class FakeAppModule {

    @Provides
    @AppScope
    fun countingIdlingResource(): CountingIdlingResource = mockk<CountingIdlingResource>().apply {
        every { decrement() } just runs
        every { increment() } just runs
    }

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