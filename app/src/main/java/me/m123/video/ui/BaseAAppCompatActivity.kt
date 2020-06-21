package me.m123.video.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import me.m123.video.R
import me.m123.video.api.Requester
import me.m123.video.data.BaseResponse
import me.m123.video.data.UserInfoToken
import me.m123.video.fragment.BaseFFragment
import me.m123.video.utils.ToolsHelper
import me.m123.video.utils.database
import okhttp3.MediaType
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseAAppCompatActivity: AppCompatActivity(),BaseFFragment.FragmentInteraction {

    var UserInfo: UserInfoToken = UserInfoToken(0,"","","","","","", "", "","")
    lateinit var packetInfo: PackageInfo
    val JSON = MediaType.parse("application/json; charset=utf-8")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getData()
        this.packetInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        setContentView(this.getLayoutId())
        val toolbar: Toolbar? = this.findViewById(R.id.toolbar)
        if (toolbar is Toolbar) {
            this.findViewById<TextView>(R.id.main_toolbar_title).text = this.getString(this.getToolbarTitle())
//            val primaryTitle = toolbar.getChildAt(0)
//            primaryTitle.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            setSupportActionBar(toolbar)
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

    }

    override fun isLogin(v_id: Int){
        this.getData()
        if (this.UserInfo.id == 0) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("v_id", v_id)
            startActivity(intent)
        }

    }

    fun getData(){
        val itemdata = this.database.use {
            select("users","userid","token","username","last_name","first_name","email","gender","avatar","date_joined","last_login").exec {
                val itemlist: List<UserInfoToken> = parseList(classParser())
                return@exec itemlist
            }
        }
        if (itemdata.count() >= 1) {
            this.UserInfo = itemdata.first()
        }
    }

    fun updateData(column: String, value: Any, userid: Int) {
        this.database.use {
            update("users",column to value).whereArgs("userid=" + userid).exec()
        }
    }

    fun checkUpdate(act: BaseAAppCompatActivity) {
        Requester.PublicService().checkUpdate(package_name = this.packageName, app_version = this.packetInfo.versionName, token = ToolsHelper.getToken(this)).enqueue(object: Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body()!!.data == "True") {
                    val dialog = AlertDialog.Builder(this@BaseAAppCompatActivity)
                    dialog.setTitle("提示")
                    dialog.setMessage("找到新版本了，请尽快下载更新。")
                    dialog.setPositiveButton("体验新版", DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.animekid.cn/files/ak-video-latest.apk"))
                        startActivity(intent)
                    })
                    dialog.setNegativeButton("不更新", null)
                    dialog.create().show()
                }
                else {
                    if (act is AboutActivity) {
                        Toast.makeText(this@BaseAAppCompatActivity, "已经是最新版本了哦", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(this@BaseAAppCompatActivity, "连接服务器错误", Toast.LENGTH_SHORT).show()
            }
        })
    }

    abstract fun getLayoutId(): Int

    abstract fun getToolbarTitle(): Int


    // 监听导航栏按钮
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return true
    }
}