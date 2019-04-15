package ru.wheelman.github.di.components

import dagger.BindsInstance
import dagger.Component
import ru.wheelman.github.di.modules.FakeAppModule
import ru.wheelman.github.di.modules.NetworkModule
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.repositories.IGithubUsersRepo

@Component(
    modules = [
        FakeAppModule::class,
        NetworkModule::class
    ]
)
@AppScope
interface AppTestComponent : AppComponent {

    @Component.Builder
    interface Builder : AppComponent.Builder {

        @BindsInstance
        fun githubRepo(githubRepo: IGithubUsersRepo): Builder

    }
}