package me.m123.video.api


import android.annotation.SuppressLint
import android.util.Log
import me.m123.video.data.*
import me.m123.video.utils.ToolsHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Body
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
        fun VideoService(): VideoService {
            return getService(VideoService.baseUrl, VideoService::class.java)
        }

        fun AuthService(): AuthService {
            return getService(AuthService.baseUrl, AuthService::class.java)
        }

        fun PublicService(): PublicService {
            return getService(PublicService.baseUrl, PublicService::class.java)
        }

        fun UserService(): UserService {
            return getService(UserService.baseUrl, UserService::class.java)
        }

        fun CollectService(): CollectService {
            return getService(CollectService.baseUrl, CollectService::class.java)
        }

    }

}

interface UserService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.123m.me/api/user/"
    }

    @GET("{userid}/")
    fun getUserInfo(@Path("userid") userid: Int, @Header("Authorization") token: String): Call<UserInfoResponse>

    @PUT("{userid}/")
    fun updateUserInfo(@Path("userid") userid: Int, @Body requestBody: RequestBody, @Header("Authorization") token: String): Call<BaseResponse>

    @FormUrlEncoded
    @POST("{userid}/resetpassword/")
    fun resetPassword(@Path("userid") userid: Int, @Field("old_password") old_password: String, @Field("new_password") new_password: String, @Header("Authorization") token: String): Call<BaseResponse>
}

interface AuthService {

    companion object {
        val baseUrl = "https://api.123m.me/api/auth/"
    }

    @FormUrlEncoded
    @POST("login/")
    fun authLogin(@Field("username") username: String, @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register/")
    fun authRegister(@Field("email") email: String, @Field("username") username: String, @Field("password") password: String): Call<BaseResponse>

    @GET("captcha/")
    fun sendCaptcha(@Query("email") email: String, @Query("username") username: String): Call<BaseResponse>

    @FormUrlEncoded
    @POST("forgetpassword/")
    fun forgetPassword(@Field("username") username: String): Call<BaseResponse>

}

interface VideoService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.123m.me/api/video/"
    }

    @GET
    fun getSpic(@Query("spic") spic: String = "true", @Header("Authorization") token: String): Call<SpicDataBean>

    @GET
    fun getNew(@Query("new") new: String, @Header("Authorization") token: String): Call<VideoListResultDataBean>

    @GET
    fun getHotVideo(@Query("hot") ticket: String = "true", @Header("Authorization") token: String): Call<HotSearchDataBean>

    @GET("{videoid}/")
    fun getDetail(@Path("videoid") videoid: Int, @Header("Authorization") token: String): Call<DetailDataBean>

    @GET
    fun search(@Query("search") search: String, @Header("Authorization") token: String): Call<VideoListResultDataBean>

    @GET
    fun getVideo(
            @Query("tid") tid: String, @Query("area") area: String, @Query("lang") lang: String,
            @Query("year") year: String, @Header("Authorization") token: String
    ): Call<VideoListResultDataBean>

    @GET
    fun getRandomVideo(
            @Query("random") random: String = "true", @Header("Authorization") token: String
    ): Call<VideoListResultDataBean>

    @PUT("{videoid}/")
    fun diggTread(@Path("videoid") videoid: Int, @Body requestBody: RequestBody, @Header("Authorization") token: String): Call<BaseResponse>

    @GET("{videoid}/feedback/")
    fun feedbackNotPlay(@Path("videoid") videoid: Int,@Header("Authorization") token: String): Call<BaseResponse>

}


interface CollectService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.123m.me/api/collect/"
    }

    @FormUrlEncoded
    @POST("collectVideo")
    fun collectVideo(@Field("user_id") user_id: Int, @Field("v_id") v_id: Int, @Header("Authorization") token: String): Call<BaseResponse>

    @GET
    fun getCollectList(@Header("Authorization") token: String): Call<VideoListResultDataBean>

}


interface PublicService {

    companion object {
        val baseUrl = "https://api.123m.me/api/app/"
    }

    @GET("notice/")
    fun getAnnouncement(@Query("package_name") package_name: String, @Header("Authorization") token: String): Call<BaseResponse>

    @GET("checkupdate/")
    fun checkUpdate(@Query("package_name") package_name: String, @Query("app_version") app_version: String, @Header("Authorization") token: String): Call<BaseResponse>
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