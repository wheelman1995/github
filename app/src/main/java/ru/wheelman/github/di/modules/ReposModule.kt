package ru.wheelman.github.di.modules

import dagger.Binds
import dagger.Module
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.repositories.GithubUsersRepo
import ru.wheelman.github.model.repositories.IGithubUsersRepo

@Module
abstract class ReposModule {

    @Binds
    @AppScope
    abstract fun githubUsersRepo(githubUsersRepo: GithubUsersRepo): IGithubUsersRepo
}