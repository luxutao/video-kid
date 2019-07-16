package cn.animekid.videokid.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
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

class SearchActivity: AppCompatActivity() {

    private var searchDataList: ArrayList<Data> = arrayListOf()
    private lateinit var searchList: ListView
    private lateinit var searchListAdapter: SearchListAdapter
    private lateinit var s: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result)
        s = intent.extras.get(SearchManager.QUERY).toString()
        Log.e("search", s)
        this.initUI()
        this.getSearchResult(1)
    }

    fun initUI() {
        this.searchList = this.findViewById(R.id.search_list)
        this.searchListAdapter = SearchListAdapter(this, this.searchDataList)
        this.searchList.adapter = this.searchListAdapter
        this.searchList.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) { }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                val length : Int = this@SearchActivity.searchDataList.size
                val page : Int = length / 10 + 1
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%10 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@SearchActivity.getSearchResult(page)
                }
            }
        })
        this.searchList.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            Log.e("searchid", index.toString())
            val bean = this@SearchActivity.searchDataList.get(index.toInt())
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("v_id", bean.v_id)
            startActivity(intent)
        }
    }

    fun getSearchResult(page: Int) {
        Requester.ImageService().search(page = page, s = s).enqueue(object : Callback<DetailsBean> {
            override fun onResponse(call: Call<DetailsBean>, response: Response<DetailsBean>) {
                val res = response.body()!!.data
                this@SearchActivity.searchDataList.addAll(res)
                this@SearchActivity.searchListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<DetailsBean>, t: Throwable) {
                Log.e("failed", t.message)
                Toast.makeText(this@SearchActivity, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
                this@SearchActivity.finish()
            }
        })
    }
}