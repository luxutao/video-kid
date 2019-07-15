package cn.animekid.videokid.api


import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import cn.animekid.videokid.data.DetailBean
import cn.animekid.videokid.data.HomeNewsDataBean
import cn.animekid.videokid.data.ListDataBean
import cn.animekid.videokid.data.ListSpicBean
import retrofit2.Call
import cn.animekid.videokid.utils.ToolsHelper
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Response
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

        //可用于多种不同种类的请求
//        fun AuthService(): AuthService {
//            return getService(AuthService.baseUrl, AuthService::class.java)
//        }

    }

}

//interface AuthService {
//
//    companion object {
//        val baseUrl = "https://api.animekid.cn/api/auth/"
//    }
//
//    @FormUrlEncoded
//    @POST("login")
//    fun authLogin(@Query("ticket") ticket: String = ToolsHelper.getTicket("login"), @Field("email") email: String, @Field("password") password: String): Call<ResponseDataBean>
//
//    @FormUrlEncoded
//    @POST("register")
//    fun authRegister(@Query("ticket") ticket: String = ToolsHelper.getTicket("register"), @Field("email") email: String, @Field("password") password: String): Call<ResponseDataBean>
//
//    @FormUrlEncoded
//    @POST("sendCaptcha")
//    fun sendCaptcha(@Query("ticket") ticket: String = ToolsHelper.getTicket("sendCaptcha"), @Field("email") email: String): Call<ResponseDataBean>
//
//    @FormUrlEncoded
//    @POST("logout")
//    fun authLogout(@Query("token") token: String, @Field("authtoken") authtoken: String): Call<ResponseDataBean>
//
//    @FormUrlEncoded
//    @POST("forgetpassword")
//    fun forgetPassword(@Query("ticket") ticket: String = ToolsHelper.getTicket("forgetpassword"), @Field("email") email: String): Call<ResponseDataBean>
//
//    @FormUrlEncoded
//    @POST("deluser")
//    fun delUser(@Query("token") token: String, @Field("authid") authid: Int): Call<ResponseDataBean>
//
//    @FormUrlEncoded
//    @POST("changeuser")
//    fun changeProfile(@Query("token") token: String, @Field("email") email: String, @Field("name") name: String,@Field("sex") sex: String): Call<ResponseDataBean>
//
//    @GET("getUserinfo")
//    fun getUserinfo(@Query("token") token: String): Call<UserInfoBean>
//
//}


interface ImageService {

    companion object {
        //此类接口的基地址
//        val baseUrl = "http://192.168.10.100:8432/api/video/"
        val baseUrl = "http://10.0.2.2:8432/api/video/"
    }

    @GET("getNew")
    fun getNew(@Query("ticket") ticket: String = ToolsHelper.getTicket("getNew")): Call<HomeNewsDataBean>

    @GET("getSpic")
    fun getSpic(@Query("ticket") ticket: String = ToolsHelper.getTicket("getSpic")): Call<ListSpicBean>

    @GET("getDetail")
    fun getDetail(@Query("ticket") ticket: String = ToolsHelper.getTicket("getDetail"), @Query("vid") vid: Int): Call<DetailBean>

    @GET("getVideo")
    fun getVideo(
            @Query("ticket") ticket: String = ToolsHelper.getTicket("getVideo"), @Query("page") page: Int,
            @Query("type") type: String, @Query("area") area: String, @Query("lang") lang: String,
            @Query("year") year: String, @Query("vtp") vtp: String
    ): Call<ListDataBean>

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