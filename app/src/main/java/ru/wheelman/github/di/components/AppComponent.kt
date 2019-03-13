package ru.wheelman.github.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.wheelman.github.di.AbstractBuilder
import ru.wheelman.github.di.modules.AppModule
import ru.wheelman.github.di.modules.NetworkModule
import ru.wheelman.github.di.modules.ReposModule
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.view.fragments.UsersFragment
import ru.wheelman.github.viewmodel.UsersFragmentViewModel

@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        ReposModule::class
    ]
)
@AppScope
interface AppComponent {

    fun inject(usersFragmentViewModel: UsersFragmentViewModel)
    fun inject(usersFragment: UsersFragment)

    @Component.Builder
    interface Builder : AbstractBuilder<AppComponent> {

        @BindsInstance
        fun context(context: Context): Builder

    }
}