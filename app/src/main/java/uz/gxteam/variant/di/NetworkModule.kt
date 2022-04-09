package uz.gxteam.variant.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.gxteam.variant.BuildConfig
import uz.gxteam.variant.interceptor.TokenInterceptor
import uz.gxteam.variant.network.registerApi.AuthService
import uz.gxteam.variant.network.statement.StatementService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideBaseUrl():String{
        if (BuildConfig.DEBUG){
            return BuildConfig.BASE_URL
        }else{
            return BuildConfig.BASE_URL
        }
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor):OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(tokenInterceptor).build()
    }

    @Provides
    @Singleton
    fun proiveRetrofit(baseUrl:String,okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit):AuthService = retrofit.create(AuthService::class.java)
    @Provides
    @Singleton
    fun provideStatementService(retrofit: Retrofit):StatementService = retrofit.create(StatementService::class.java)



}