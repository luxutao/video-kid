package me.m123.video.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import me.m123.video.R
import me.m123.video.adapter.SearchListAdapter
import me.m123.video.api.Requester
import me.m123.video.data.VideoListResultDataBean
import me.m123.video.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RandomActivity: BaseAAppCompatActivity() {

    private var randomDataList: ArrayList<VideoListResultDataBean.Data> = arrayListOf()
    private lateinit var randomList: ListView
    private lateinit var randomRefresh: FloatingActionButton
    private lateinit var randomListAdapter: SearchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()
        this.getRandomResult()
    }

    fun initUI() {
        this.randomRefresh = this.findViewById(R.id.random_refresh)
        this.randomList = this.findViewById(R.id.random_list)
        this.randomListAdapter = SearchListAdapter(this, this.randomDataList)
        this.randomList.adapter = this.randomListAdapter
        this.randomList.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = this@RandomActivity.randomDataList.get(index.toInt())
            this.isLogin(bean.v_id)
        }
        this.randomRefresh.setOnClickListener { this.getRandomResult() }
    }

    fun getRandomResult() {
        Requester.VideoService().getRandomVideo(token = ToolsHelper.getToken(this)).enqueue(object : Callback<VideoListResultDataBean> {
            override fun onResponse(call: Call<VideoListResultDataBean>, response: Response<VideoListResultDataBean>) {
                val res = response.body()!!.results
                this@RandomActivity.randomDataList.clear()
                this@RandomActivity.randomDataList.addAll(res)
                this@RandomActivity.randomListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<VideoListResultDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                Toast.makeText(this@RandomActivity, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_random
    }

    override fun getToolbarTitle(): Int {
        return R.string.navbar_random
    }
}