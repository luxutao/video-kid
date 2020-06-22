package me.m123.video.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import me.m123.video.R
import me.m123.video.api.Requester
import me.m123.video.data.BaseResponse
import me.m123.video.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgetPasswordActivity: BaseAAppCompatActivity() {

    private lateinit var login: TextView
    private lateinit var username: EditText
    private lateinit var forgetPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.login = this.findViewById(R.id.login)
        this.username = this.findViewById(R.id.username)
        this.forgetPassword = this.findViewById(R.id.forget)

        this.forgetPassword.setOnClickListener {
            val username = this.username.text.toString()
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "用户名不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Requester.AuthService().forgetPassword(username = username).enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    val c = response.body()!!
                    if (c.code == 200) {
                        Toast.makeText(this@ForgetPasswordActivity, "重置链接已发送,请注意查收!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity, c.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.d("send_captcha", t.message)
                    Toast.makeText(this@ForgetPasswordActivity, "发送失败,请稍后再试.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        this.login.setOnClickListener { finish() }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_forget_password
    }

    override fun getToolbarTitle(): Int {
        return R.string.str_forget_password
    }
}