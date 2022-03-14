package dev.vaibhav.quizzify.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.vaibhav.quizzify.util.HASURA_URL
import dev.vaibhav.quizzify.util.HASURA_WEB_SOCKET_URL
import dev.vaibhav.quizzify.util.SECRET_KEY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ApolloModule {

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    @Named("graphQLClient")
    fun providesOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method, original.body)
                builder.header("X-Hasura-Admin-Secret", SECRET_KEY)
                chain.proceed(builder.build())
            }
            .build()

    @Provides
    fun providesCacheKeyHelper(): LruNormalizedCacheFactory =
        LruNormalizedCacheFactory(EvictionPolicy.NO_EVICTION)

    @Provides
    fun providesCacheKeyResolver(): CacheKeyResolver = object : CacheKeyResolver() {
        override fun fromFieldRecordSet(
            field: ResponseField,
            recordSet: Map<String, Any>
        ): CacheKey {
            if (recordSet.containsKey("quiz")) {
                val id = recordSet["quiz"] as String
                return CacheKey.from(id)
            }
            return CacheKey.NO_KEY
        }

        override fun fromFieldArguments(
            field: ResponseField,
            variables: Operation.Variables
        ): CacheKey {
            return CacheKey.NO_KEY
        }
    }

    @Provides
    fun providesApolloClient(
        @Named("graphQLClient") okHttpClient: OkHttpClient,
        cacheKeyResolver: CacheKeyResolver,
        lruCacheFactory: LruNormalizedCacheFactory
    ): ApolloClient =
        ApolloClient.builder()
            .okHttpClient(okHttpClient)
            .subscriptionTransportFactory(
                WebSocketSubscriptionTransport.Factory(HASURA_WEB_SOCKET_URL, okHttpClient)
            )
            .normalizedCache(lruCacheFactory, cacheKeyResolver)
            .serverUrl(HASURA_URL)
            .build()
}