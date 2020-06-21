package me.m123.video.ui

import android.app.SearchManager
import android.os.Bundle
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

class SearchResultActivity: BaseAAppCompatActivity() {

    private var searchDataList: ArrayList<VideoListResultDataBean.Data> = arrayListOf()
    private lateinit var searchList: ListView
    private lateinit var searchListAdapter: SearchListAdapter
    private lateinit var s: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.s = intent.extras.get(SearchManager.QUERY).toString()
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
                val length : Int = this@SearchResultActivity.searchDataList.size
                val page : Int = length / 10 + 1
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%10 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@SearchResultActivity.getSearchResult(page)
                }
            }
        })
        this.searchList.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = this@SearchResultActivity.searchDataList.get(index.toInt())
            this.isLogin(bean.v_id)
        }
    }

    fun getSearchResult(page: Int) {
        Requester.VideoService().search(this.s, ToolsHelper.getToken(this)).enqueue(object : Callback<VideoListResultDataBean> {
            override fun onResponse(call: Call<VideoListResultDataBean>, response: Response<VideoListResultDataBean>) {
                val res = response.body()!!.results
                this@SearchResultActivity.searchDataList.addAll(res)
                this@SearchResultActivity.searchListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<VideoListResultDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                Toast.makeText(this@SearchResultActivity, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
                this@SearchResultActivity.finish()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search_result
    }

    override fun getToolbarTitle(): Int {
        return R.string.nav_name_search_result
    }
}