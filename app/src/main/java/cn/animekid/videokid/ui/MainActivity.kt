package cn.animekid.videokid.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.animekid.videokid.R
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.BasicResponse
import cn.animekid.videokid.fragment.*
import cn.animekid.videokid.utils.ToolsHelper
import cn.animekid.videokid.utils.database
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.db.delete
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.file.Files.delete

class MainActivity : BaseAAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var currentFragment: Fragment? = null
    private var islogin: Boolean = false
    private lateinit var navView: NavigationView
    private lateinit var navheaderView: View
    private lateinit var UserAvatar: ImageView
    private lateinit var UserName: TextView
    private lateinit var UserEmail: TextView
    private lateinit var UserProfile: MenuItem
    private lateinit var UserLogout: MenuItem
    private lateinit var BottomMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()

        this.UserAvatar.setOnClickListener {
            if (!this.islogin) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        this.BottomMenu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemHome -> {
                    this.openFragment(FragmentHome.newInstance(), "home")
                }
                R.id.itemMovie -> {
                    this.openFragment(FragmentMovie.newInstance(), "movie")
                }
                R.id.itemTv -> {
                    this.openFragment(FragmentTv.newInstance(), "tv")
                }
                R.id.itemAnime -> {
                    this.openFragment(FragmentAnime.newInstance(), "anime")
                }
                R.id.itemShows -> {
                    this.openFragment(FragmentShows.newInstance(), "shows")
                }
            }
            true
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun initUI() {
        this.navView = this.findViewById(R.id.nav_view)
//        this.navheaderView = this.navView.inflateHeaderView(R.layout.nav_header_main)
        this.navheaderView = this.navView.getHeaderView(0)
        this.UserAvatar = this.navheaderView.findViewById(R.id.user_avatar)
        this.UserName = this.navheaderView.findViewById(R.id.user_name)
        this.UserEmail = this.navheaderView.findViewById(R.id.user_email)
        this.UserProfile = this.navView.menu.findItem(R.id.nav_profile)
        this.UserLogout = this.navView.menu.findItem(R.id.nav_logout)
        this.BottomMenu = this.findViewById(R.id.bottom_menu)
        this.openFragment(FragmentHome.newInstance(), "home")
    }

    fun defaultData(default: Boolean) {
        if (default) {
            // 注销账号后将动态数据还原
            this.UserAvatar.setImageResource(R.drawable.default_avatar)
            this.UserEmail.text = getString(R.string.nav_header_subtitle)
            this.UserName.text = getString(R.string.nav_header_title)
            // 获取侧边栏下面的按钮,设置隐藏属性
            this.UserLogout.isVisible = false
            this.UserProfile.isVisible = false
            this.islogin = false
        } else {
            if (this.UserInfo.avatar != "F") {
                Glide.with(this).load(this.UserInfo.avatar).into(this.UserAvatar)
            }
            this.UserEmail.text = this.UserInfo.email
            this.UserName.text = this.UserInfo.name
            this.UserLogout.isVisible = true
            this.UserProfile.isVisible = true
            this.islogin = true
        }
    }

    override fun onResume() {
        super.onResume()
        this.getData()
        if (this.UserInfo.userid != 0) {
            this.defaultData(false)
        } else {
            this.defaultData(true)
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_settings).actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView.setSearchableInfo(searchableInfo)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_random -> {
                val intent = Intent(this, RandomActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_setting -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_share -> {
                val dialogView = View.inflate(this, R.layout.share_dialog, null)
                AlertDialog.Builder(this).setTitle("提示").setView(dialogView)
                        .setPositiveButton("确认") { t_dialog, which -> }
                        .setNegativeButton("取消", null)
                        .create().show()
            }
            R.id.nav_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                AlertDialog.Builder(this).setTitle("提示").setMessage("确认注销当前账号吗？")
                        .setPositiveButton("确认") { t_dialog, which ->
                            Requester.AuthService().authLogout(token = ToolsHelper.getToken(this@MainActivity), authtoken = this.UserInfo.token).enqueue(object: Callback<BasicResponse> {
                                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                                    this@MainActivity.database.use { delete("users") }
                                    Log.d("logoutSuccess","success")
                                    this@MainActivity.defaultData(true)
                                    Toast.makeText(this@MainActivity, "注销账号成功", Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                                    Log.d("logoutError",t.message)
                                }
                            })

                        }
                        .setNegativeButton("取消", null)
                        .create().show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    // 切换fragment保存状态
    private fun openFragment(fragment: Fragment, tag: String) {
        // 如果当前的frag存在就隐藏
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().hide(currentFragment!!).commit()
        }
        // 如果搜索到了要打开的frag则显示，否则则创建
        currentFragment = supportFragmentManager.findFragmentByTag(tag)
        if (currentFragment == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragment, tag)
            transaction.commit()
            currentFragment = fragment
        } else {
            supportFragmentManager.beginTransaction().show(currentFragment!!).commit()
        }

    }
}
