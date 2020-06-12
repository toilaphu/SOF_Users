package com.phunguyen.stackoverflowuser.di

import android.app.Application
import com.phunguyen.stackoverflowuser.api.SOFService
import com.phunguyen.stackoverflowuser.utils.*
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
class ServiceModule {

    private var loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
        Timber.d(it)
    }).apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    @Singleton
    fun provideRecipeService(builder: Retrofit.Builder): SOFService {
        return builder
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SOFService::class.java)
    }


    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        application: Application,
        clientBuilder: OkHttpClient.Builder
    ): OkHttpClient {
        val okHttpClientBuilder = clientBuilder.apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
        }
        okHttpClientBuilder.addNetworkInterceptor { chain ->
            val original = chain.request()
            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json;charset=utf-t")
                .header("Accept-Language", "en")
                .header(
                    "Cache-Control", "public, max-age=" + 60 * 60
                ) // Get from Cache for 1 hour
                .removeHeader("Pragma")
                .method(original.method(), original.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
            .cache(Cache(application.cacheDir, (10 * 1024 * 1024)))
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
        return okHttpClientBuilder.build()

    }

    @Provides
    fun provideUnsafeOkHttpClient(): OkHttpClient.Builder =
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}