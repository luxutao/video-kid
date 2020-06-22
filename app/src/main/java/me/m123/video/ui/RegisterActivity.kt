package me.m123.video.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import kotlinx.android.synthetic.main.activity_register.*
import me.m123.video.R
import me.m123.video.api.Requester
import me.m123.video.data.BaseResponse
import me.m123.video.utils.ToolsHelper
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity: BaseAAppCompatActivity() {

    private var _Captcha: String = ""
    private lateinit var sendCaptcha: ImageButton
    private lateinit var register: Button
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var captcha: EditText
    private lateinit var login: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()

        this.sendCaptcha.setOnClickListener {
            val user_email = this.email.text.toString()
            if (TextUtils.isEmpty(user_email) || ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "邮箱不能为空或者邮箱格式不正确!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "验证码已发送,请注意查收!", Toast.LENGTH_SHORT).show()
            Requester.AuthService().sendCaptcha(email = user_email, username = "").enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    val c = response.body()!!
                    if (c.code != 200) {
                        Toast.makeText(this@RegisterActivity, "验证码发送失败,请稍后再试.", Toast.LENGTH_SHORT).show()
                    }
                    _Captcha = c.data.toString()
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.d("send_captcha", t.message)
                    Toast.makeText(this@RegisterActivity, "验证码发送失败,请稍后再试.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        register.setOnClickListener {
            val username = username.text.toString()
            val user_email = this.email.text.toString()
            val user_captcha = this.captcha.text.toString()
            val user_password = this.password.text.toString()
            if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password) ||
                    TextUtils.isEmpty(user_captcha) || TextUtils.isEmpty(username)) {
                Toast.makeText(this, "不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (user_captcha != _Captcha) {
                Toast.makeText(this, "验证码不正确!请登录邮箱查看正确的验证码!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "请输入一个正确的邮箱!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Requester.AuthService().authRegister(email = user_email, password = user_password, username = username).enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val res = response.body()!!
                    if (res.code == 200) {
                        Toast.makeText(this@RegisterActivity, "注册成功啦!感谢你的支持,赶快去登录吧!", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this@RegisterActivity, res.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.d("login_error", t.message)
                    Toast.makeText(this@RegisterActivity, "注册错误,请检查网络是否正常!", Toast.LENGTH_LONG).show()
                }
            })
        }

        login.setOnClickListener {
            finish()
        }

    }

    fun initUI(){
        this.sendCaptcha = this.findViewById(R.id.send_captcha)
        this.register = this.findViewById(R.id.register)
        this.username = this.findViewById(R.id.username)
        this.email = this.findViewById(R.id.email)
        this.captcha = this.findViewById(R.id.captcha)
        this.password = this.findViewById(R.id.password)
        this.login = this.findViewById(R.id.login)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun getToolbarTitle(): Int {
        return R.string.nav_name_register
    }
}