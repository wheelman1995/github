package ru.wheelman.github.di.modules

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.wheelman.github.di.qualifiers.ErrorsLiveDataQualifier
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.datasources.remote.GithubApi
import ru.wheelman.github.model.datasources.remote.PageKeyedGithubDataSource
import ru.wheelman.github.model.entities.User

@Module
class NetworkModule {

    private companion object {

        private const val PAGE_SIZE = 20

    }

    @Provides
    @AppScope
    fun githubRetrofit(okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory) =
        Retrofit.Builder()
            .baseUrl("https://api.github.com")
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()

    @Provides
    @AppScope
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
        .build()

    @Provides
    @AppScope
    fun githubApi(retrofit: Retrofit) = retrofit.create(GithubApi::class.java)

    @Provides
    @AppScope
    fun gsonConverterFactory() =
        GsonConverterFactory.create(
            GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        )

    @Provides
    @AppScope
    @ErrorsLiveDataQualifier
    fun errors() = MutableLiveData<String>()

    @Provides
    @AppScope
    fun dataSourceFactory(factory: PageKeyedGithubDataSource.Factory) =
        factory.mapByPage {
            it.map {
                User(
                    it.id,
                    it.login,
                    it.avatarUrl
                )
            }
        }

    @Provides
    @AppScope
    fun pagedListConfig() = PagedList.Config.Builder()
        .setInitialLoadSizeHint(PAGE_SIZE)
        .setPageSize(PAGE_SIZE)
        .build()


}