package me.m123.video.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import me.m123.video.R
import me.m123.video.api.Requester
import me.m123.video.data.*
import me.m123.video.utils.ToolsHelper
import me.m123.video.utils.database
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: BaseAAppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var forgotPassword: TextView
    private lateinit var register: TextView
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()

        this.forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        this.login.setOnClickListener {
            val username = this.username.text.toString()
            val user_password = this.password.text.toString()
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(user_password)) {
                Toast.makeText(this, "用户名或密码不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Requester.AuthService().authLogin(username = username, password = user_password).enqueue(object: Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val u = response.body()!!

                    if (u.code == 200) {
                        Requester.UserService().getUserInfo(userid = u.data.userid, token = "Token " + u.data.token).enqueue(object: Callback<UserInfoResponse> {
                            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                                val userInfoData = response.body()!!
                                val userinfo = ContentValues()
                                userinfo.put("userid", userInfoData.id)
                                userinfo.put("token", u.data.token)
                                userinfo.put("email", userInfoData.email)
                                userinfo.put("username", userInfoData.username)
                                userinfo.put("first_name", userInfoData.first_name)
                                userinfo.put("last_name", userInfoData.last_name)
                                userinfo.put("last_login", userInfoData.last_login)
                                userinfo.put("date_joined", userInfoData.date_joined)
                                userinfo.put("gender", userInfoData.gender)
                                userinfo.put("avatar", userInfoData.avatar)
                                this@LoginActivity.database.use {
                                    insert("users","avatar",userinfo)
                                }
                                val intent = Intent()
                                intent.putExtra("result", "true")
                                this@LoginActivity.setResult(1, intent)
                                finish()
                            }

                            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "验证失败了!", Toast.LENGTH_LONG).show()
                            }
                        })

                    } else {
                        Toast.makeText(this@LoginActivity, "登录错误,请检查账号密码是否正确！", Toast.LENGTH_SHORT).show()
                    }


                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("login_error", t.message)
                    Toast.makeText(this@LoginActivity, "登录错误,请检查账号密码是否正确!", Toast.LENGTH_LONG).show()
                }
            })
        }

        this.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    fun initUI(){
        this.register = this.findViewById(R.id.register)
        this.username = this.findViewById(R.id.username)
        this.password = this.findViewById(R.id.password)
        this.forgotPassword = this.findViewById(R.id.forgot_password)
        this.login = this.findViewById(R.id.login)
    }

    override fun getLayoutId(): Int {
        return  R.layout.activity_login
    }

    override fun getToolbarTitle(): Int {
        return R.string.nav_name_login
    }
}

