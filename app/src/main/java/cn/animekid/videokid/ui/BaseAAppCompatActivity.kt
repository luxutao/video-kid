package cn.animekid.videokid.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import cn.animekid.videokid.R
import cn.animekid.videokid.data.UserInfoData
import cn.animekid.videokid.fragment.BaseFFragment
import cn.animekid.videokid.utils.database
import org.jetbrains.anko.db.*

abstract class BaseAAppCompatActivity: AppCompatActivity(),BaseFFragment.FragmentInteraction {

    var UserInfo: UserInfoData = UserInfoData(0,"","","","","","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getData()
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
        if (this.UserInfo.userid == 0) {
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
            select("users","userid","token","name","create_time","email","sex","avatar").exec {
                val itemlist: List<UserInfoData> = parseList(classParser())
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