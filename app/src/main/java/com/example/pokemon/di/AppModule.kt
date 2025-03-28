package com.example.pokemon.di

import android.content.Context
import androidx.room.Room
import com.example.pokemon.BuildConfig
import com.example.pokemon.data.db.AppDatabase
import com.example.pokemon.data.network.PokemonService
import com.example.pokemon.di.AppDispatchers.Default
import com.example.pokemon.di.AppDispatchers.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            },
        ).build()

    @Singleton
    @Provides
    fun providePokemonService(okHttpCallFactory: Call.Factory): PokemonService {
        val networkJson = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
            explicitNulls = false
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .callFactory(okHttpCallFactory)
            .addConverterFactory(networkJson.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build()
        return retrofit.create(PokemonService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext applicationContext: Context
    ): AppDatabase = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "pokemon-database"
    ).build()

    @Singleton
    @Provides
    fun providePokemonDao(db: AppDatabase) = db.pokemonDao()

    @Provides
    @Dispatcher(IO)
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(Default)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val appDispatcher: AppDispatchers)

enum class AppDispatchers {
    Default, IO
}
