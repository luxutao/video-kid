package me.m123.video.ui

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.m123.video.R
import me.m123.video.api.Requester
import me.m123.video.utils.ToolsHelper
import com.bumptech.glide.Glide
import me.m123.video.data.BaseResponse
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity: BaseAAppCompatActivity() {

    private lateinit var userAvatar: ImageView
    private lateinit var userSex: TextView
    private lateinit var userName: TextView
    private lateinit var layoutName: LinearLayout
    private lateinit var layoutSex: LinearLayout
    private lateinit var layoutAvatar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.userAvatar = this.findViewById(R.id.user_avatar)
        this.userName = this.findViewById(R.id.user_name)
        this.userSex = this.findViewById(R.id.user_sex)
        this.layoutSex = this.findViewById(R.id.layout_sex)
        this.layoutName = this.findViewById(R.id.layout_name)
        this.layoutAvatar = this.findViewById(R.id.layout_avatar)

        this.layoutSex.setOnClickListener {
            val sex_array = arrayOf("男","女")
            val dialog = AlertDialog.Builder(this)
            var json = JSONObject()
            dialog.setTitle("提示").setSingleChoiceItems(sex_array,sex_array.asList().indexOf(this.UserInfo.gender)) { t_dialog, which ->
                Log.d("gender", which.toString())
                json.put("gender", sex_array[which])
                Requester.UserService().updateUserInfo(this.UserInfo.id, RequestBody.create(this.JSON, json.toString()), token = ToolsHelper.getToken(this@ProfileActivity)).enqueue(object: Callback<BaseResponse> {
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        this@ProfileActivity.userSex.text = sex_array[which]
                        this@ProfileActivity.UserInfo.gender = sex_array[which]
                        this@ProfileActivity.updateData("gender", sex_array[which], this@ProfileActivity.UserInfo.id)
                        t_dialog.dismiss()
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        Log.d("Error",t.message)
                    }
                })
            }
            dialog.create().show()
        }

        this.layoutName.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val newview = View.inflate(this, R.layout.change_profile_name, null)
            val newEditText = newview.findViewById<EditText>(R.id.new_name)
            newEditText.text = SpannableStringBuilder(this.UserInfo.last_name)
            dialog.setTitle("提示").setView(newview)
            dialog.setPositiveButton("确认", DialogInterface.OnClickListener { t_dialog, which ->
                val newName = newEditText.text.toString()
                var json = JSONObject()
                json.put("last_name", newName)
                if (TextUtils.isEmpty(newName)) { return@OnClickListener }
                Requester.UserService().updateUserInfo(this.UserInfo.id, RequestBody.create(this.JSON, json.toString()), token = ToolsHelper.getToken(this@ProfileActivity)).enqueue(object: Callback<BaseResponse> {
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        this@ProfileActivity.userName.text = newName
                        this@ProfileActivity.UserInfo.last_name = newName
                        this@ProfileActivity.updateData("last_name", newName, this@ProfileActivity.UserInfo.id)
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        Log.d("logoutError",t.message)
                    }
                })

            })
            dialog.setNegativeButton("取消", null)
            dialog.create().show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (this.UserInfo.id != 0) {
            this.userName.text = this.UserInfo.last_name
            this.userSex.text = this.UserInfo.gender
            Glide.with(this).load(this.UserInfo.avatar).into(this.userAvatar)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_profile
    }

    override fun getToolbarTitle(): Int {
        return R.string.nav_name_profile
    }
}