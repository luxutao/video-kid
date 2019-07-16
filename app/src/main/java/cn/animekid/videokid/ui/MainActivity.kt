package cn.animekid.videokid.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import cn.animekid.videokid.R
import cn.animekid.videokid.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // 打开默认页
        openFragment(FragmentHome.newInstance(), "home")
        val nav = findViewById<BottomNavigationView>(R.id.bv)
        nav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemHome -> {
                    openFragment(FragmentHome.newInstance(), "home")
                }
                R.id.itemMovie -> {
                    openFragment(FragmentMovie.newInstance(), "movie")
                }
                R.id.itemTv -> {
                    openFragment(FragmentTv.newInstance(), "tv")
                }
                R.id.itemAnime -> {
                    openFragment(FragmentAnime.newInstance(), "anime")
                }
                R.id.itemShows -> {
                    openFragment(FragmentShows.newInstance(), "shows")
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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
                // Handle the camera action
            }
            R.id.nav_profile -> {

            }
            R.id.nav_setting -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_about -> {

            }
            R.id.nav_logout -> {

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
