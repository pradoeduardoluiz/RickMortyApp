package br.com.pixelwolf.rickandmorty.http;

import br.com.pixelwolf.rickandmorty.util.Constants
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    companion object {

//        private val LOGGING_LEVEL: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
//        private val logInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
//            level = LOGGING_LEVEL
//        }

        private val clientBuilder: OkHttpClient.Builder = OkHttpClient().newBuilder()
            //.addInterceptor(logInterceptor)

        private val client: OkHttpClient = clientBuilder.build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        private val RICK_MORTY_API: RickMortyAPI = retrofit.create(RickMortyAPI::class.java)

        fun getRickMortyApiAPI() = RICK_MORTY_API
    }
}