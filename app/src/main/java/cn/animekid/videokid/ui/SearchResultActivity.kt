package cn.animekid.videokid.ui

import android.app.SearchManager
import android.os.Bundle
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

class SearchResultActivity: BaseAAppCompatActivity() {

    private var searchDataList: ArrayList<Data> = arrayListOf()
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
        Requester.VideoService().search(page = page, s = this.s).enqueue(object : Callback<DetailsBean> {
            override fun onResponse(call: Call<DetailsBean>, response: Response<DetailsBean>) {
                val res = response.body()!!.data
                this@SearchResultActivity.searchDataList.addAll(res)
                this@SearchResultActivity.searchListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<DetailsBean>, t: Throwable) {
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