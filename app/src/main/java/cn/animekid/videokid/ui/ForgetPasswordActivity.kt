package cn.animekid.videokid.ui

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
import cn.animekid.videokid.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgetPasswordActivity: BaseAAppCompatActivity() {

    private lateinit var login: TextView
    private lateinit var email: EditText
    private lateinit var forgetPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.login = this.findViewById(R.id.login)
        this.email = this.findViewById(R.id.email)
        this.forgetPassword = this.findViewById(R.id.forget)

        this.forgetPassword.setOnClickListener {
            val user_email = this.email.text.toString()
            if (TextUtils.isEmpty(user_email) || ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "邮箱不能为空或者邮箱格式不正确!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "重置链接已发送,请注意查收!", Toast.LENGTH_SHORT).show()
            Requester.AuthService().forgetPassword(email = user_email).enqueue(object: Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    val c = response.body()!!
                    if (c.code == 200) {
                        finish()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
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