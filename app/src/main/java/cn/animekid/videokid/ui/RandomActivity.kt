package cn.animekid.videokid.ui

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
import cn.animekid.videokid.R
import cn.animekid.videokid.adapter.SearchListAdapter
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.Data
import cn.animekid.videokid.data.DetailsBean
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RandomActivity: BaseAAppCompatActivity() {

    private var randomDataList: ArrayList<Data> = arrayListOf()
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
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("v_id", bean.v_id)
            startActivity(intent)
        }
        this.randomRefresh.setOnClickListener { this.getRandomResult() }
    }

    fun getRandomResult() {
        Requester.VideoService().getRandomVideo().enqueue(object : Callback<DetailsBean> {
            override fun onResponse(call: Call<DetailsBean>, response: Response<DetailsBean>) {
                val res = response.body()!!.data
                this@RandomActivity.randomDataList.clear()
                this@RandomActivity.randomDataList.addAll(res)
                this@RandomActivity.randomListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<DetailsBean>, t: Throwable) {
                Log.e("failed", t.message)
                Toast.makeText(this@RandomActivity, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_random
    }
}