package com.mobily.bugit.di

import android.content.Context
import com.mobily.bugit.data.config.ImgBBConfig
import com.mobily.bugit.data.config.NotionConfig
import com.mobily.bugit.data.remote.ImgBBService
import com.mobily.bugit.data.remote.NotionService
import com.mobily.bugit.data.repository.BugReportRepositoryImpl
import com.mobily.bugit.domain.repository.BugReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotionConfig(@ApplicationContext context: Context): NotionConfig {
        return NotionConfig(context).apply {
            validate() // Validate configuration at startup
        }
    }

    @Provides
    @Singleton
    fun provideImgBBConfig(@ApplicationContext context: Context): ImgBBConfig {
        return ImgBBConfig(context).apply {
            validate() // Validate configuration at startup
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideNotionService(okHttpClient: OkHttpClient): NotionService {
        return Retrofit.Builder()
            .baseUrl(NotionService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotionService::class.java)
    }

    @Provides
    @Singleton
    fun provideImgBBService(okHttpClient: OkHttpClient): ImgBBService {
        return Retrofit.Builder()
            .baseUrl(ImgBBService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgBBService::class.java)
    }

    @Provides
    @Singleton
    fun provideBugReportRepository(
        notionService: NotionService,
        imgBBService: ImgBBService,
        @ApplicationContext context: Context,
        notionConfig: NotionConfig,
        imgBBConfig: ImgBBConfig
    ): BugReportRepository {
        return BugReportRepositoryImpl(
            notionService = notionService,
            imgBBService = imgBBService,
            context = context,
            notionConfig = notionConfig,
            imgBBConfig = imgBBConfig
        )
    }
}
