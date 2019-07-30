package cn.animekid.videokid.api


import android.annotation.SuppressLint
import android.util.Log
import cn.animekid.videokid.data.*
import cn.animekid.videokid.utils.ToolsHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.*


class Requester {

    companion object {

        private fun <T> getService(baseUrl: String, service: Class<T>): T {

            val clien = OkHttpClient.Builder()
                    //自定义拦截器用于日志输出
                    .addInterceptor(LogInterceptor())
                    .build()

            val retrofit = Retrofit.Builder().baseUrl(baseUrl)
                    //格式转换
                    .addConverterFactory(GsonConverterFactory.create())
                    //正常的retrofit返回的是call，此方法用于将call转化成Rxjava的Observable或其他类型
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(clien)
                    .build()
            return retrofit.create(service)
        }

        //可用于多种不同种类的请求
        fun ImageService(): ImageService {
            return getService(ImageService.baseUrl, ImageService::class.java)
        }

        fun PublicService(): PublicService {
            return getService(PublicService.baseUrl, PublicService::class.java)
        }

    }

}

interface ImageService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.animekid.cn/api/video/"
//        val baseUrl = "http://10.0.2.2:8432/api/video/"
    }

    @GET("getNew")
    fun getNew(@Query("ticket") ticket: String = ToolsHelper.getTicket("getNew")): Call<HomeNewsDataBean>

    @GET("getSpic")
    fun getSpic(@Query("ticket") ticket: String = ToolsHelper.getTicket("getSpic")): Call<ListSpicBean>

    @GET("getDetail")
    fun getDetail(@Query("ticket") ticket: String = ToolsHelper.getTicket("getDetail"), @Query("vid") vid: Int): Call<DetailBean>

    @GET("search")
    fun search(@Query("ticket") ticket: String = ToolsHelper.getTicket("search"),
               @Query("s") s: String, @Query("page") page: Int): Call<DetailsBean>

    @GET("getVideo")
    fun getVideo(
            @Query("ticket") ticket: String = ToolsHelper.getTicket("getVideo"), @Query("page") page: Int,
            @Query("type") type: String, @Query("area") area: String, @Query("lang") lang: String,
            @Query("year") year: String, @Query("vtp") vtp: String, @Query("size") size: Int = 10
    ): Call<ListDataBean>

    @GET("getRandomVideo")
    fun getRandomVideo(
            @Query("ticket") ticket: String = ToolsHelper.getTicket("getRandomVideo")
    ): Call<DetailsBean>

}


interface PublicService {

    companion object {
        val baseUrl = "https://api.animekid.cn/api/public/"
    }

    @GET("checkUpdate")
    fun checkUpdate(@Query("ticket") ticket: String = ToolsHelper.getTicket("checkUpdate"), @Query("app_name") app_name: String = "AKVIDEO", @Query("app_version") app_version: String): Call<BasicResponse>

    @FormUrlEncoded
    @POST("feedback")
    fun feedback(@Query("ticket") ticket: String = ToolsHelper.getTicket("feedback"), @Field("app_name") app_name: String = "AKVIDEO", @Field("email") email: String, @Field("content") content: String): Call<BasicResponse>
}


class LogInterceptor : Interceptor {

    private val tag = "Retrofit"
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        Log.i(tag, format.format(Date()) + " Requeste " + "\nmethod:" + request.method() + "\nurl:" + request.url() + "\nbody:" + request.body().toString())

        val response = chain.proceed(request)

        //response.peekBody不会关闭流
        Log.i(tag, format.format(Date()) + " Response " + "\nsuccessful:" + response.isSuccessful + "\nbody:" + response.peekBody(1024).toString())

        return response
    }

}