package cn.animekid.videokid.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.animekid.videokid.R
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.BasicResponse
import cn.animekid.videokid.data.UserInfo
import cn.animekid.videokid.utils.ToolsHelper
import cn.animekid.videokid.utils.database
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: BaseAAppCompatActivity() {

    private lateinit var email: EditText
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
            val user_email = this.email.text.toString()
            val user_password = this.password.text.toString()
            if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password)) {
                Toast.makeText(this, "邮箱或密码不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "请输入一个正确的邮箱!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Requester.AuthService().authLogin(email = user_email, password = user_password).enqueue(object: Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val u = response.body()!!

                    if (u.code == 200) {
                        Requester.AuthService().getUserinfo(token = u.data).enqueue(object: Callback<UserInfo> {
                            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                                val userInfoData = response.body()!!
                                val userinfo = ContentValues()
                                userinfo.put("userid", userInfoData.data.userid)
                                userinfo.put("token", u.data)
                                userinfo.put("name", userInfoData.data.name)
                                userinfo.put("create_time", userInfoData.data.create_time)
                                userinfo.put("email", userInfoData.data.email)
                                userinfo.put("sex", userInfoData.data.sex)
                                userinfo.put("avatar", userInfoData.data.avatar)
                                this@LoginActivity.database.use {
                                    insert("users","avatar",userinfo)
                                }
                                finish()
                            }

                            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "验证失败了!", Toast.LENGTH_LONG).show()
                            }
                        })

                    } else {
                        Toast.makeText(this@LoginActivity, "该账号不存在！", Toast.LENGTH_SHORT).show()
                    }


                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
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
        this.email = this.findViewById(R.id.email)
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

