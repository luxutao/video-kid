package cn.animekid.videokid.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.Toast
import cn.animekid.videokid.R
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.BasicResponse
import cn.animekid.videokid.data.UserInfoData
import cn.animekid.videokid.utils.database
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Thread.sleep

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.checkLoginOverdue()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        val mAnimation = AlphaAnimation(0.1f, 1.0f)
        val splash = this.findViewById<ImageView>(R.id.splash_anim)
        mAnimation.duration = 3000
        splash.animation = mAnimation
        mAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                launch {
                    sleep(1000)
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    fun checkLoginOverdue() {
        val itemdata = this.database.use {
            select("users","userid","token","name","create_time","email","sex","avatar").exec {
                val itemlist: List<UserInfoData> = parseList(classParser())
                return@exec itemlist
            }
        }
        if (itemdata.count() >= 1) {
            val userInfo = itemdata.first()
            Requester.AuthService().authTokenOverdue(token = userInfo.token).enqueue(object: Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    val resdata = response.body()!!
                    if (resdata.code == 200) {
                        this@SplashActivity.database.use {
                            update("users","token" to resdata.data).whereArgs("userid=" + userInfo.userid).exec()
                        }
                    }
                    else if (resdata.code == 204) {
                        Toast.makeText(this@SplashActivity, "未知错误，请联系开发者", Toast.LENGTH_SHORT).show()
                    }
                    else if (resdata.code == 403) {
                        Toast.makeText(this@SplashActivity, "验证失败", Toast.LENGTH_SHORT).show()
                    }
                }
    
                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(this@SplashActivity, "连接服务器错误", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}